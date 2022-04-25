package com.ritan.lit.watchlist.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.watchlist.IntegrationTest;
import com.ritan.lit.watchlist.domain.PriceHistory;
import com.ritan.lit.watchlist.repository.PriceHistoryRepository;
import com.ritan.lit.watchlist.repository.search.PriceHistorySearchRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PriceHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PriceHistoryResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_OPEN = 1D;
    private static final Double UPDATED_OPEN = 2D;

    private static final Double DEFAULT_HIGH = 1D;
    private static final Double UPDATED_HIGH = 2D;

    private static final Double DEFAULT_LOW = 1D;
    private static final Double UPDATED_LOW = 2D;

    private static final Double DEFAULT_CLOSE = 1D;
    private static final Double UPDATED_CLOSE = 2D;

    private static final Double DEFAULT_ADJ_CLOSE = 1D;
    private static final Double UPDATED_ADJ_CLOSE = 2D;

    private static final Double DEFAULT_VOLUME = 1D;
    private static final Double UPDATED_VOLUME = 2D;

    private static final String ENTITY_API_URL = "/api/price-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/price-histories";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    /**
     * This repository is mocked in the com.ritan.lit.watchlist.repository.search test package.
     *
     * @see com.ritan.lit.watchlist.repository.search.PriceHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private PriceHistorySearchRepository mockPriceHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPriceHistoryMockMvc;

    private PriceHistory priceHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PriceHistory createEntity(EntityManager em) {
        PriceHistory priceHistory = new PriceHistory()
            .date(DEFAULT_DATE)
            .open(DEFAULT_OPEN)
            .high(DEFAULT_HIGH)
            .low(DEFAULT_LOW)
            .close(DEFAULT_CLOSE)
            .adjClose(DEFAULT_ADJ_CLOSE)
            .volume(DEFAULT_VOLUME);
        return priceHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PriceHistory createUpdatedEntity(EntityManager em) {
        PriceHistory priceHistory = new PriceHistory()
            .date(UPDATED_DATE)
            .open(UPDATED_OPEN)
            .high(UPDATED_HIGH)
            .low(UPDATED_LOW)
            .close(UPDATED_CLOSE)
            .adjClose(UPDATED_ADJ_CLOSE)
            .volume(UPDATED_VOLUME);
        return priceHistory;
    }

    @BeforeEach
    public void initTest() {
        priceHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createPriceHistory() throws Exception {
        int databaseSizeBeforeCreate = priceHistoryRepository.findAll().size();
        // Create the PriceHistory
        restPriceHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(priceHistory)))
            .andExpect(status().isCreated());

        // Validate the PriceHistory in the database
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        PriceHistory testPriceHistory = priceHistoryList.get(priceHistoryList.size() - 1);
        assertThat(testPriceHistory.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testPriceHistory.getOpen()).isEqualTo(DEFAULT_OPEN);
        assertThat(testPriceHistory.getHigh()).isEqualTo(DEFAULT_HIGH);
        assertThat(testPriceHistory.getLow()).isEqualTo(DEFAULT_LOW);
        assertThat(testPriceHistory.getClose()).isEqualTo(DEFAULT_CLOSE);
        assertThat(testPriceHistory.getAdjClose()).isEqualTo(DEFAULT_ADJ_CLOSE);
        assertThat(testPriceHistory.getVolume()).isEqualTo(DEFAULT_VOLUME);

        // Validate the PriceHistory in Elasticsearch
        verify(mockPriceHistorySearchRepository, times(1)).save(testPriceHistory);
    }

    @Test
    @Transactional
    void createPriceHistoryWithExistingId() throws Exception {
        // Create the PriceHistory with an existing ID
        priceHistory.setId(1L);

        int databaseSizeBeforeCreate = priceHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPriceHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(priceHistory)))
            .andExpect(status().isBadRequest());

        // Validate the PriceHistory in the database
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the PriceHistory in Elasticsearch
        verify(mockPriceHistorySearchRepository, times(0)).save(priceHistory);
    }

    @Test
    @Transactional
    void getAllPriceHistories() throws Exception {
        // Initialize the database
        priceHistoryRepository.saveAndFlush(priceHistory);

        // Get all the priceHistoryList
        restPriceHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(priceHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].open").value(hasItem(DEFAULT_OPEN.doubleValue())))
            .andExpect(jsonPath("$.[*].high").value(hasItem(DEFAULT_HIGH.doubleValue())))
            .andExpect(jsonPath("$.[*].low").value(hasItem(DEFAULT_LOW.doubleValue())))
            .andExpect(jsonPath("$.[*].close").value(hasItem(DEFAULT_CLOSE.doubleValue())))
            .andExpect(jsonPath("$.[*].adjClose").value(hasItem(DEFAULT_ADJ_CLOSE.doubleValue())))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())));
    }

    @Test
    @Transactional
    void getPriceHistory() throws Exception {
        // Initialize the database
        priceHistoryRepository.saveAndFlush(priceHistory);

        // Get the priceHistory
        restPriceHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, priceHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(priceHistory.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.open").value(DEFAULT_OPEN.doubleValue()))
            .andExpect(jsonPath("$.high").value(DEFAULT_HIGH.doubleValue()))
            .andExpect(jsonPath("$.low").value(DEFAULT_LOW.doubleValue()))
            .andExpect(jsonPath("$.close").value(DEFAULT_CLOSE.doubleValue()))
            .andExpect(jsonPath("$.adjClose").value(DEFAULT_ADJ_CLOSE.doubleValue()))
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingPriceHistory() throws Exception {
        // Get the priceHistory
        restPriceHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPriceHistory() throws Exception {
        // Initialize the database
        priceHistoryRepository.saveAndFlush(priceHistory);

        int databaseSizeBeforeUpdate = priceHistoryRepository.findAll().size();

        // Update the priceHistory
        PriceHistory updatedPriceHistory = priceHistoryRepository.findById(priceHistory.getId()).get();
        // Disconnect from session so that the updates on updatedPriceHistory are not directly saved in db
        em.detach(updatedPriceHistory);
        updatedPriceHistory
            .date(UPDATED_DATE)
            .open(UPDATED_OPEN)
            .high(UPDATED_HIGH)
            .low(UPDATED_LOW)
            .close(UPDATED_CLOSE)
            .adjClose(UPDATED_ADJ_CLOSE)
            .volume(UPDATED_VOLUME);

        restPriceHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPriceHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPriceHistory))
            )
            .andExpect(status().isOk());

        // Validate the PriceHistory in the database
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeUpdate);
        PriceHistory testPriceHistory = priceHistoryList.get(priceHistoryList.size() - 1);
        assertThat(testPriceHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPriceHistory.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testPriceHistory.getHigh()).isEqualTo(UPDATED_HIGH);
        assertThat(testPriceHistory.getLow()).isEqualTo(UPDATED_LOW);
        assertThat(testPriceHistory.getClose()).isEqualTo(UPDATED_CLOSE);
        assertThat(testPriceHistory.getAdjClose()).isEqualTo(UPDATED_ADJ_CLOSE);
        assertThat(testPriceHistory.getVolume()).isEqualTo(UPDATED_VOLUME);

        // Validate the PriceHistory in Elasticsearch
        verify(mockPriceHistorySearchRepository).save(testPriceHistory);
    }

    @Test
    @Transactional
    void putNonExistingPriceHistory() throws Exception {
        int databaseSizeBeforeUpdate = priceHistoryRepository.findAll().size();
        priceHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPriceHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, priceHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(priceHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the PriceHistory in the database
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PriceHistory in Elasticsearch
        verify(mockPriceHistorySearchRepository, times(0)).save(priceHistory);
    }

    @Test
    @Transactional
    void putWithIdMismatchPriceHistory() throws Exception {
        int databaseSizeBeforeUpdate = priceHistoryRepository.findAll().size();
        priceHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(priceHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the PriceHistory in the database
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PriceHistory in Elasticsearch
        verify(mockPriceHistorySearchRepository, times(0)).save(priceHistory);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPriceHistory() throws Exception {
        int databaseSizeBeforeUpdate = priceHistoryRepository.findAll().size();
        priceHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(priceHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PriceHistory in the database
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PriceHistory in Elasticsearch
        verify(mockPriceHistorySearchRepository, times(0)).save(priceHistory);
    }

    @Test
    @Transactional
    void partialUpdatePriceHistoryWithPatch() throws Exception {
        // Initialize the database
        priceHistoryRepository.saveAndFlush(priceHistory);

        int databaseSizeBeforeUpdate = priceHistoryRepository.findAll().size();

        // Update the priceHistory using partial update
        PriceHistory partialUpdatedPriceHistory = new PriceHistory();
        partialUpdatedPriceHistory.setId(priceHistory.getId());

        partialUpdatedPriceHistory.date(UPDATED_DATE).open(UPDATED_OPEN).low(UPDATED_LOW).close(UPDATED_CLOSE).volume(UPDATED_VOLUME);

        restPriceHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPriceHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPriceHistory))
            )
            .andExpect(status().isOk());

        // Validate the PriceHistory in the database
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeUpdate);
        PriceHistory testPriceHistory = priceHistoryList.get(priceHistoryList.size() - 1);
        assertThat(testPriceHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPriceHistory.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testPriceHistory.getHigh()).isEqualTo(DEFAULT_HIGH);
        assertThat(testPriceHistory.getLow()).isEqualTo(UPDATED_LOW);
        assertThat(testPriceHistory.getClose()).isEqualTo(UPDATED_CLOSE);
        assertThat(testPriceHistory.getAdjClose()).isEqualTo(DEFAULT_ADJ_CLOSE);
        assertThat(testPriceHistory.getVolume()).isEqualTo(UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void fullUpdatePriceHistoryWithPatch() throws Exception {
        // Initialize the database
        priceHistoryRepository.saveAndFlush(priceHistory);

        int databaseSizeBeforeUpdate = priceHistoryRepository.findAll().size();

        // Update the priceHistory using partial update
        PriceHistory partialUpdatedPriceHistory = new PriceHistory();
        partialUpdatedPriceHistory.setId(priceHistory.getId());

        partialUpdatedPriceHistory
            .date(UPDATED_DATE)
            .open(UPDATED_OPEN)
            .high(UPDATED_HIGH)
            .low(UPDATED_LOW)
            .close(UPDATED_CLOSE)
            .adjClose(UPDATED_ADJ_CLOSE)
            .volume(UPDATED_VOLUME);

        restPriceHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPriceHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPriceHistory))
            )
            .andExpect(status().isOk());

        // Validate the PriceHistory in the database
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeUpdate);
        PriceHistory testPriceHistory = priceHistoryList.get(priceHistoryList.size() - 1);
        assertThat(testPriceHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPriceHistory.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testPriceHistory.getHigh()).isEqualTo(UPDATED_HIGH);
        assertThat(testPriceHistory.getLow()).isEqualTo(UPDATED_LOW);
        assertThat(testPriceHistory.getClose()).isEqualTo(UPDATED_CLOSE);
        assertThat(testPriceHistory.getAdjClose()).isEqualTo(UPDATED_ADJ_CLOSE);
        assertThat(testPriceHistory.getVolume()).isEqualTo(UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void patchNonExistingPriceHistory() throws Exception {
        int databaseSizeBeforeUpdate = priceHistoryRepository.findAll().size();
        priceHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPriceHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, priceHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(priceHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the PriceHistory in the database
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PriceHistory in Elasticsearch
        verify(mockPriceHistorySearchRepository, times(0)).save(priceHistory);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPriceHistory() throws Exception {
        int databaseSizeBeforeUpdate = priceHistoryRepository.findAll().size();
        priceHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(priceHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the PriceHistory in the database
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PriceHistory in Elasticsearch
        verify(mockPriceHistorySearchRepository, times(0)).save(priceHistory);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPriceHistory() throws Exception {
        int databaseSizeBeforeUpdate = priceHistoryRepository.findAll().size();
        priceHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(priceHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PriceHistory in the database
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PriceHistory in Elasticsearch
        verify(mockPriceHistorySearchRepository, times(0)).save(priceHistory);
    }

    @Test
    @Transactional
    void deletePriceHistory() throws Exception {
        // Initialize the database
        priceHistoryRepository.saveAndFlush(priceHistory);

        int databaseSizeBeforeDelete = priceHistoryRepository.findAll().size();

        // Delete the priceHistory
        restPriceHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, priceHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAll();
        assertThat(priceHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PriceHistory in Elasticsearch
        verify(mockPriceHistorySearchRepository, times(1)).deleteById(priceHistory.getId());
    }

    @Test
    @Transactional
    void searchPriceHistory() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        priceHistoryRepository.saveAndFlush(priceHistory);
        when(mockPriceHistorySearchRepository.search("id:" + priceHistory.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(priceHistory), PageRequest.of(0, 1), 1));

        // Search the priceHistory
        restPriceHistoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + priceHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(priceHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].open").value(hasItem(DEFAULT_OPEN.doubleValue())))
            .andExpect(jsonPath("$.[*].high").value(hasItem(DEFAULT_HIGH.doubleValue())))
            .andExpect(jsonPath("$.[*].low").value(hasItem(DEFAULT_LOW.doubleValue())))
            .andExpect(jsonPath("$.[*].close").value(hasItem(DEFAULT_CLOSE.doubleValue())))
            .andExpect(jsonPath("$.[*].adjClose").value(hasItem(DEFAULT_ADJ_CLOSE.doubleValue())))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())));
    }
}
