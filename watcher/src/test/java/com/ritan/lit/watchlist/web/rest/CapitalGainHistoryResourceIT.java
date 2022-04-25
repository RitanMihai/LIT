package com.ritan.lit.watchlist.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.watchlist.IntegrationTest;
import com.ritan.lit.watchlist.domain.CapitalGainHistory;
import com.ritan.lit.watchlist.repository.CapitalGainHistoryRepository;
import com.ritan.lit.watchlist.repository.search.CapitalGainHistorySearchRepository;
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
 * Integration tests for the {@link CapitalGainHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CapitalGainHistoryResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_CAPITAL_GAIN = 1D;
    private static final Double UPDATED_CAPITAL_GAIN = 2D;

    private static final String ENTITY_API_URL = "/api/capital-gain-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/capital-gain-histories";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CapitalGainHistoryRepository capitalGainHistoryRepository;

    /**
     * This repository is mocked in the com.ritan.lit.watchlist.repository.search test package.
     *
     * @see com.ritan.lit.watchlist.repository.search.CapitalGainHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private CapitalGainHistorySearchRepository mockCapitalGainHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCapitalGainHistoryMockMvc;

    private CapitalGainHistory capitalGainHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CapitalGainHistory createEntity(EntityManager em) {
        CapitalGainHistory capitalGainHistory = new CapitalGainHistory().date(DEFAULT_DATE).capitalGain(DEFAULT_CAPITAL_GAIN);
        return capitalGainHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CapitalGainHistory createUpdatedEntity(EntityManager em) {
        CapitalGainHistory capitalGainHistory = new CapitalGainHistory().date(UPDATED_DATE).capitalGain(UPDATED_CAPITAL_GAIN);
        return capitalGainHistory;
    }

    @BeforeEach
    public void initTest() {
        capitalGainHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createCapitalGainHistory() throws Exception {
        int databaseSizeBeforeCreate = capitalGainHistoryRepository.findAll().size();
        // Create the CapitalGainHistory
        restCapitalGainHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(capitalGainHistory))
            )
            .andExpect(status().isCreated());

        // Validate the CapitalGainHistory in the database
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        CapitalGainHistory testCapitalGainHistory = capitalGainHistoryList.get(capitalGainHistoryList.size() - 1);
        assertThat(testCapitalGainHistory.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testCapitalGainHistory.getCapitalGain()).isEqualTo(DEFAULT_CAPITAL_GAIN);

        // Validate the CapitalGainHistory in Elasticsearch
        verify(mockCapitalGainHistorySearchRepository, times(1)).save(testCapitalGainHistory);
    }

    @Test
    @Transactional
    void createCapitalGainHistoryWithExistingId() throws Exception {
        // Create the CapitalGainHistory with an existing ID
        capitalGainHistory.setId(1L);

        int databaseSizeBeforeCreate = capitalGainHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCapitalGainHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(capitalGainHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the CapitalGainHistory in the database
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the CapitalGainHistory in Elasticsearch
        verify(mockCapitalGainHistorySearchRepository, times(0)).save(capitalGainHistory);
    }

    @Test
    @Transactional
    void getAllCapitalGainHistories() throws Exception {
        // Initialize the database
        capitalGainHistoryRepository.saveAndFlush(capitalGainHistory);

        // Get all the capitalGainHistoryList
        restCapitalGainHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capitalGainHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].capitalGain").value(hasItem(DEFAULT_CAPITAL_GAIN.doubleValue())));
    }

    @Test
    @Transactional
    void getCapitalGainHistory() throws Exception {
        // Initialize the database
        capitalGainHistoryRepository.saveAndFlush(capitalGainHistory);

        // Get the capitalGainHistory
        restCapitalGainHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, capitalGainHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(capitalGainHistory.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.capitalGain").value(DEFAULT_CAPITAL_GAIN.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingCapitalGainHistory() throws Exception {
        // Get the capitalGainHistory
        restCapitalGainHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCapitalGainHistory() throws Exception {
        // Initialize the database
        capitalGainHistoryRepository.saveAndFlush(capitalGainHistory);

        int databaseSizeBeforeUpdate = capitalGainHistoryRepository.findAll().size();

        // Update the capitalGainHistory
        CapitalGainHistory updatedCapitalGainHistory = capitalGainHistoryRepository.findById(capitalGainHistory.getId()).get();
        // Disconnect from session so that the updates on updatedCapitalGainHistory are not directly saved in db
        em.detach(updatedCapitalGainHistory);
        updatedCapitalGainHistory.date(UPDATED_DATE).capitalGain(UPDATED_CAPITAL_GAIN);

        restCapitalGainHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCapitalGainHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCapitalGainHistory))
            )
            .andExpect(status().isOk());

        // Validate the CapitalGainHistory in the database
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeUpdate);
        CapitalGainHistory testCapitalGainHistory = capitalGainHistoryList.get(capitalGainHistoryList.size() - 1);
        assertThat(testCapitalGainHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testCapitalGainHistory.getCapitalGain()).isEqualTo(UPDATED_CAPITAL_GAIN);

        // Validate the CapitalGainHistory in Elasticsearch
        verify(mockCapitalGainHistorySearchRepository).save(testCapitalGainHistory);
    }

    @Test
    @Transactional
    void putNonExistingCapitalGainHistory() throws Exception {
        int databaseSizeBeforeUpdate = capitalGainHistoryRepository.findAll().size();
        capitalGainHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCapitalGainHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, capitalGainHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(capitalGainHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the CapitalGainHistory in the database
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CapitalGainHistory in Elasticsearch
        verify(mockCapitalGainHistorySearchRepository, times(0)).save(capitalGainHistory);
    }

    @Test
    @Transactional
    void putWithIdMismatchCapitalGainHistory() throws Exception {
        int databaseSizeBeforeUpdate = capitalGainHistoryRepository.findAll().size();
        capitalGainHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapitalGainHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(capitalGainHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the CapitalGainHistory in the database
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CapitalGainHistory in Elasticsearch
        verify(mockCapitalGainHistorySearchRepository, times(0)).save(capitalGainHistory);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCapitalGainHistory() throws Exception {
        int databaseSizeBeforeUpdate = capitalGainHistoryRepository.findAll().size();
        capitalGainHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapitalGainHistoryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(capitalGainHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CapitalGainHistory in the database
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CapitalGainHistory in Elasticsearch
        verify(mockCapitalGainHistorySearchRepository, times(0)).save(capitalGainHistory);
    }

    @Test
    @Transactional
    void partialUpdateCapitalGainHistoryWithPatch() throws Exception {
        // Initialize the database
        capitalGainHistoryRepository.saveAndFlush(capitalGainHistory);

        int databaseSizeBeforeUpdate = capitalGainHistoryRepository.findAll().size();

        // Update the capitalGainHistory using partial update
        CapitalGainHistory partialUpdatedCapitalGainHistory = new CapitalGainHistory();
        partialUpdatedCapitalGainHistory.setId(capitalGainHistory.getId());

        partialUpdatedCapitalGainHistory.capitalGain(UPDATED_CAPITAL_GAIN);

        restCapitalGainHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCapitalGainHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCapitalGainHistory))
            )
            .andExpect(status().isOk());

        // Validate the CapitalGainHistory in the database
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeUpdate);
        CapitalGainHistory testCapitalGainHistory = capitalGainHistoryList.get(capitalGainHistoryList.size() - 1);
        assertThat(testCapitalGainHistory.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testCapitalGainHistory.getCapitalGain()).isEqualTo(UPDATED_CAPITAL_GAIN);
    }

    @Test
    @Transactional
    void fullUpdateCapitalGainHistoryWithPatch() throws Exception {
        // Initialize the database
        capitalGainHistoryRepository.saveAndFlush(capitalGainHistory);

        int databaseSizeBeforeUpdate = capitalGainHistoryRepository.findAll().size();

        // Update the capitalGainHistory using partial update
        CapitalGainHistory partialUpdatedCapitalGainHistory = new CapitalGainHistory();
        partialUpdatedCapitalGainHistory.setId(capitalGainHistory.getId());

        partialUpdatedCapitalGainHistory.date(UPDATED_DATE).capitalGain(UPDATED_CAPITAL_GAIN);

        restCapitalGainHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCapitalGainHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCapitalGainHistory))
            )
            .andExpect(status().isOk());

        // Validate the CapitalGainHistory in the database
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeUpdate);
        CapitalGainHistory testCapitalGainHistory = capitalGainHistoryList.get(capitalGainHistoryList.size() - 1);
        assertThat(testCapitalGainHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testCapitalGainHistory.getCapitalGain()).isEqualTo(UPDATED_CAPITAL_GAIN);
    }

    @Test
    @Transactional
    void patchNonExistingCapitalGainHistory() throws Exception {
        int databaseSizeBeforeUpdate = capitalGainHistoryRepository.findAll().size();
        capitalGainHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCapitalGainHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, capitalGainHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(capitalGainHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the CapitalGainHistory in the database
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CapitalGainHistory in Elasticsearch
        verify(mockCapitalGainHistorySearchRepository, times(0)).save(capitalGainHistory);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCapitalGainHistory() throws Exception {
        int databaseSizeBeforeUpdate = capitalGainHistoryRepository.findAll().size();
        capitalGainHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapitalGainHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(capitalGainHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the CapitalGainHistory in the database
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CapitalGainHistory in Elasticsearch
        verify(mockCapitalGainHistorySearchRepository, times(0)).save(capitalGainHistory);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCapitalGainHistory() throws Exception {
        int databaseSizeBeforeUpdate = capitalGainHistoryRepository.findAll().size();
        capitalGainHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapitalGainHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(capitalGainHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CapitalGainHistory in the database
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CapitalGainHistory in Elasticsearch
        verify(mockCapitalGainHistorySearchRepository, times(0)).save(capitalGainHistory);
    }

    @Test
    @Transactional
    void deleteCapitalGainHistory() throws Exception {
        // Initialize the database
        capitalGainHistoryRepository.saveAndFlush(capitalGainHistory);

        int databaseSizeBeforeDelete = capitalGainHistoryRepository.findAll().size();

        // Delete the capitalGainHistory
        restCapitalGainHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, capitalGainHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CapitalGainHistory> capitalGainHistoryList = capitalGainHistoryRepository.findAll();
        assertThat(capitalGainHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CapitalGainHistory in Elasticsearch
        verify(mockCapitalGainHistorySearchRepository, times(1)).deleteById(capitalGainHistory.getId());
    }

    @Test
    @Transactional
    void searchCapitalGainHistory() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        capitalGainHistoryRepository.saveAndFlush(capitalGainHistory);
        when(mockCapitalGainHistorySearchRepository.search("id:" + capitalGainHistory.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(capitalGainHistory), PageRequest.of(0, 1), 1));

        // Search the capitalGainHistory
        restCapitalGainHistoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + capitalGainHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capitalGainHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].capitalGain").value(hasItem(DEFAULT_CAPITAL_GAIN.doubleValue())));
    }
}
