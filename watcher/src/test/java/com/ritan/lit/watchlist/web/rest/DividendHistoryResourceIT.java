package com.ritan.lit.watchlist.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.watchlist.IntegrationTest;
import com.ritan.lit.watchlist.domain.DividendHistory;
import com.ritan.lit.watchlist.repository.DividendHistoryRepository;
import com.ritan.lit.watchlist.repository.search.DividendHistorySearchRepository;
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
 * Integration tests for the {@link DividendHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DividendHistoryResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_DIVIDEND = 1D;
    private static final Double UPDATED_DIVIDEND = 2D;

    private static final String ENTITY_API_URL = "/api/dividend-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/dividend-histories";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DividendHistoryRepository dividendHistoryRepository;

    /**
     * This repository is mocked in the com.ritan.lit.watchlist.repository.search test package.
     *
     * @see com.ritan.lit.watchlist.repository.search.DividendHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private DividendHistorySearchRepository mockDividendHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDividendHistoryMockMvc;

    private DividendHistory dividendHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DividendHistory createEntity(EntityManager em) {
        DividendHistory dividendHistory = new DividendHistory().date(DEFAULT_DATE).dividend(DEFAULT_DIVIDEND);
        return dividendHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DividendHistory createUpdatedEntity(EntityManager em) {
        DividendHistory dividendHistory = new DividendHistory().date(UPDATED_DATE).dividend(UPDATED_DIVIDEND);
        return dividendHistory;
    }

    @BeforeEach
    public void initTest() {
        dividendHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createDividendHistory() throws Exception {
        int databaseSizeBeforeCreate = dividendHistoryRepository.findAll().size();
        // Create the DividendHistory
        restDividendHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dividendHistory))
            )
            .andExpect(status().isCreated());

        // Validate the DividendHistory in the database
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        DividendHistory testDividendHistory = dividendHistoryList.get(dividendHistoryList.size() - 1);
        assertThat(testDividendHistory.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testDividendHistory.getDividend()).isEqualTo(DEFAULT_DIVIDEND);

        // Validate the DividendHistory in Elasticsearch
        verify(mockDividendHistorySearchRepository, times(1)).save(testDividendHistory);
    }

    @Test
    @Transactional
    void createDividendHistoryWithExistingId() throws Exception {
        // Create the DividendHistory with an existing ID
        dividendHistory.setId(1L);

        int databaseSizeBeforeCreate = dividendHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDividendHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dividendHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the DividendHistory in the database
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the DividendHistory in Elasticsearch
        verify(mockDividendHistorySearchRepository, times(0)).save(dividendHistory);
    }

    @Test
    @Transactional
    void getAllDividendHistories() throws Exception {
        // Initialize the database
        dividendHistoryRepository.saveAndFlush(dividendHistory);

        // Get all the dividendHistoryList
        restDividendHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dividendHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].dividend").value(hasItem(DEFAULT_DIVIDEND.doubleValue())));
    }

    @Test
    @Transactional
    void getDividendHistory() throws Exception {
        // Initialize the database
        dividendHistoryRepository.saveAndFlush(dividendHistory);

        // Get the dividendHistory
        restDividendHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, dividendHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dividendHistory.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.dividend").value(DEFAULT_DIVIDEND.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingDividendHistory() throws Exception {
        // Get the dividendHistory
        restDividendHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDividendHistory() throws Exception {
        // Initialize the database
        dividendHistoryRepository.saveAndFlush(dividendHistory);

        int databaseSizeBeforeUpdate = dividendHistoryRepository.findAll().size();

        // Update the dividendHistory
        DividendHistory updatedDividendHistory = dividendHistoryRepository.findById(dividendHistory.getId()).get();
        // Disconnect from session so that the updates on updatedDividendHistory are not directly saved in db
        em.detach(updatedDividendHistory);
        updatedDividendHistory.date(UPDATED_DATE).dividend(UPDATED_DIVIDEND);

        restDividendHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDividendHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDividendHistory))
            )
            .andExpect(status().isOk());

        // Validate the DividendHistory in the database
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeUpdate);
        DividendHistory testDividendHistory = dividendHistoryList.get(dividendHistoryList.size() - 1);
        assertThat(testDividendHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDividendHistory.getDividend()).isEqualTo(UPDATED_DIVIDEND);

        // Validate the DividendHistory in Elasticsearch
        verify(mockDividendHistorySearchRepository).save(testDividendHistory);
    }

    @Test
    @Transactional
    void putNonExistingDividendHistory() throws Exception {
        int databaseSizeBeforeUpdate = dividendHistoryRepository.findAll().size();
        dividendHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDividendHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dividendHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dividendHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the DividendHistory in the database
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DividendHistory in Elasticsearch
        verify(mockDividendHistorySearchRepository, times(0)).save(dividendHistory);
    }

    @Test
    @Transactional
    void putWithIdMismatchDividendHistory() throws Exception {
        int databaseSizeBeforeUpdate = dividendHistoryRepository.findAll().size();
        dividendHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDividendHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dividendHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the DividendHistory in the database
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DividendHistory in Elasticsearch
        verify(mockDividendHistorySearchRepository, times(0)).save(dividendHistory);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDividendHistory() throws Exception {
        int databaseSizeBeforeUpdate = dividendHistoryRepository.findAll().size();
        dividendHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDividendHistoryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dividendHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DividendHistory in the database
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DividendHistory in Elasticsearch
        verify(mockDividendHistorySearchRepository, times(0)).save(dividendHistory);
    }

    @Test
    @Transactional
    void partialUpdateDividendHistoryWithPatch() throws Exception {
        // Initialize the database
        dividendHistoryRepository.saveAndFlush(dividendHistory);

        int databaseSizeBeforeUpdate = dividendHistoryRepository.findAll().size();

        // Update the dividendHistory using partial update
        DividendHistory partialUpdatedDividendHistory = new DividendHistory();
        partialUpdatedDividendHistory.setId(dividendHistory.getId());

        partialUpdatedDividendHistory.date(UPDATED_DATE).dividend(UPDATED_DIVIDEND);

        restDividendHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDividendHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDividendHistory))
            )
            .andExpect(status().isOk());

        // Validate the DividendHistory in the database
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeUpdate);
        DividendHistory testDividendHistory = dividendHistoryList.get(dividendHistoryList.size() - 1);
        assertThat(testDividendHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDividendHistory.getDividend()).isEqualTo(UPDATED_DIVIDEND);
    }

    @Test
    @Transactional
    void fullUpdateDividendHistoryWithPatch() throws Exception {
        // Initialize the database
        dividendHistoryRepository.saveAndFlush(dividendHistory);

        int databaseSizeBeforeUpdate = dividendHistoryRepository.findAll().size();

        // Update the dividendHistory using partial update
        DividendHistory partialUpdatedDividendHistory = new DividendHistory();
        partialUpdatedDividendHistory.setId(dividendHistory.getId());

        partialUpdatedDividendHistory.date(UPDATED_DATE).dividend(UPDATED_DIVIDEND);

        restDividendHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDividendHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDividendHistory))
            )
            .andExpect(status().isOk());

        // Validate the DividendHistory in the database
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeUpdate);
        DividendHistory testDividendHistory = dividendHistoryList.get(dividendHistoryList.size() - 1);
        assertThat(testDividendHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDividendHistory.getDividend()).isEqualTo(UPDATED_DIVIDEND);
    }

    @Test
    @Transactional
    void patchNonExistingDividendHistory() throws Exception {
        int databaseSizeBeforeUpdate = dividendHistoryRepository.findAll().size();
        dividendHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDividendHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dividendHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dividendHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the DividendHistory in the database
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DividendHistory in Elasticsearch
        verify(mockDividendHistorySearchRepository, times(0)).save(dividendHistory);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDividendHistory() throws Exception {
        int databaseSizeBeforeUpdate = dividendHistoryRepository.findAll().size();
        dividendHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDividendHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dividendHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the DividendHistory in the database
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DividendHistory in Elasticsearch
        verify(mockDividendHistorySearchRepository, times(0)).save(dividendHistory);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDividendHistory() throws Exception {
        int databaseSizeBeforeUpdate = dividendHistoryRepository.findAll().size();
        dividendHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDividendHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dividendHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DividendHistory in the database
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DividendHistory in Elasticsearch
        verify(mockDividendHistorySearchRepository, times(0)).save(dividendHistory);
    }

    @Test
    @Transactional
    void deleteDividendHistory() throws Exception {
        // Initialize the database
        dividendHistoryRepository.saveAndFlush(dividendHistory);

        int databaseSizeBeforeDelete = dividendHistoryRepository.findAll().size();

        // Delete the dividendHistory
        restDividendHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, dividendHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DividendHistory> dividendHistoryList = dividendHistoryRepository.findAll();
        assertThat(dividendHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the DividendHistory in Elasticsearch
        verify(mockDividendHistorySearchRepository, times(1)).deleteById(dividendHistory.getId());
    }

    @Test
    @Transactional
    void searchDividendHistory() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        dividendHistoryRepository.saveAndFlush(dividendHistory);
        when(mockDividendHistorySearchRepository.search("id:" + dividendHistory.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(dividendHistory), PageRequest.of(0, 1), 1));

        // Search the dividendHistory
        restDividendHistoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + dividendHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dividendHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].dividend").value(hasItem(DEFAULT_DIVIDEND.doubleValue())));
    }
}
