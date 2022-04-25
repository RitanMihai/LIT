package com.ritan.lit.watchlist.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.watchlist.IntegrationTest;
import com.ritan.lit.watchlist.domain.StockSplitHistory;
import com.ritan.lit.watchlist.repository.StockSplitHistoryRepository;
import com.ritan.lit.watchlist.repository.search.StockSplitHistorySearchRepository;
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
 * Integration tests for the {@link StockSplitHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockSplitHistoryResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_RATIO = 1D;
    private static final Double UPDATED_RATIO = 2D;

    private static final String ENTITY_API_URL = "/api/stock-split-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/stock-split-histories";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockSplitHistoryRepository stockSplitHistoryRepository;

    /**
     * This repository is mocked in the com.ritan.lit.watchlist.repository.search test package.
     *
     * @see com.ritan.lit.watchlist.repository.search.StockSplitHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private StockSplitHistorySearchRepository mockStockSplitHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockSplitHistoryMockMvc;

    private StockSplitHistory stockSplitHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockSplitHistory createEntity(EntityManager em) {
        StockSplitHistory stockSplitHistory = new StockSplitHistory().date(DEFAULT_DATE).ratio(DEFAULT_RATIO);
        return stockSplitHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockSplitHistory createUpdatedEntity(EntityManager em) {
        StockSplitHistory stockSplitHistory = new StockSplitHistory().date(UPDATED_DATE).ratio(UPDATED_RATIO);
        return stockSplitHistory;
    }

    @BeforeEach
    public void initTest() {
        stockSplitHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createStockSplitHistory() throws Exception {
        int databaseSizeBeforeCreate = stockSplitHistoryRepository.findAll().size();
        // Create the StockSplitHistory
        restStockSplitHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockSplitHistory))
            )
            .andExpect(status().isCreated());

        // Validate the StockSplitHistory in the database
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        StockSplitHistory testStockSplitHistory = stockSplitHistoryList.get(stockSplitHistoryList.size() - 1);
        assertThat(testStockSplitHistory.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testStockSplitHistory.getRatio()).isEqualTo(DEFAULT_RATIO);

        // Validate the StockSplitHistory in Elasticsearch
        verify(mockStockSplitHistorySearchRepository, times(1)).save(testStockSplitHistory);
    }

    @Test
    @Transactional
    void createStockSplitHistoryWithExistingId() throws Exception {
        // Create the StockSplitHistory with an existing ID
        stockSplitHistory.setId(1L);

        int databaseSizeBeforeCreate = stockSplitHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockSplitHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockSplitHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockSplitHistory in the database
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the StockSplitHistory in Elasticsearch
        verify(mockStockSplitHistorySearchRepository, times(0)).save(stockSplitHistory);
    }

    @Test
    @Transactional
    void getAllStockSplitHistories() throws Exception {
        // Initialize the database
        stockSplitHistoryRepository.saveAndFlush(stockSplitHistory);

        // Get all the stockSplitHistoryList
        restStockSplitHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockSplitHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].ratio").value(hasItem(DEFAULT_RATIO.doubleValue())));
    }

    @Test
    @Transactional
    void getStockSplitHistory() throws Exception {
        // Initialize the database
        stockSplitHistoryRepository.saveAndFlush(stockSplitHistory);

        // Get the stockSplitHistory
        restStockSplitHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, stockSplitHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockSplitHistory.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.ratio").value(DEFAULT_RATIO.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingStockSplitHistory() throws Exception {
        // Get the stockSplitHistory
        restStockSplitHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewStockSplitHistory() throws Exception {
        // Initialize the database
        stockSplitHistoryRepository.saveAndFlush(stockSplitHistory);

        int databaseSizeBeforeUpdate = stockSplitHistoryRepository.findAll().size();

        // Update the stockSplitHistory
        StockSplitHistory updatedStockSplitHistory = stockSplitHistoryRepository.findById(stockSplitHistory.getId()).get();
        // Disconnect from session so that the updates on updatedStockSplitHistory are not directly saved in db
        em.detach(updatedStockSplitHistory);
        updatedStockSplitHistory.date(UPDATED_DATE).ratio(UPDATED_RATIO);

        restStockSplitHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStockSplitHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStockSplitHistory))
            )
            .andExpect(status().isOk());

        // Validate the StockSplitHistory in the database
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeUpdate);
        StockSplitHistory testStockSplitHistory = stockSplitHistoryList.get(stockSplitHistoryList.size() - 1);
        assertThat(testStockSplitHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testStockSplitHistory.getRatio()).isEqualTo(UPDATED_RATIO);

        // Validate the StockSplitHistory in Elasticsearch
        verify(mockStockSplitHistorySearchRepository).save(testStockSplitHistory);
    }

    @Test
    @Transactional
    void putNonExistingStockSplitHistory() throws Exception {
        int databaseSizeBeforeUpdate = stockSplitHistoryRepository.findAll().size();
        stockSplitHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockSplitHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockSplitHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockSplitHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockSplitHistory in the database
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockSplitHistory in Elasticsearch
        verify(mockStockSplitHistorySearchRepository, times(0)).save(stockSplitHistory);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockSplitHistory() throws Exception {
        int databaseSizeBeforeUpdate = stockSplitHistoryRepository.findAll().size();
        stockSplitHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockSplitHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockSplitHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockSplitHistory in the database
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockSplitHistory in Elasticsearch
        verify(mockStockSplitHistorySearchRepository, times(0)).save(stockSplitHistory);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockSplitHistory() throws Exception {
        int databaseSizeBeforeUpdate = stockSplitHistoryRepository.findAll().size();
        stockSplitHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockSplitHistoryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockSplitHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockSplitHistory in the database
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockSplitHistory in Elasticsearch
        verify(mockStockSplitHistorySearchRepository, times(0)).save(stockSplitHistory);
    }

    @Test
    @Transactional
    void partialUpdateStockSplitHistoryWithPatch() throws Exception {
        // Initialize the database
        stockSplitHistoryRepository.saveAndFlush(stockSplitHistory);

        int databaseSizeBeforeUpdate = stockSplitHistoryRepository.findAll().size();

        // Update the stockSplitHistory using partial update
        StockSplitHistory partialUpdatedStockSplitHistory = new StockSplitHistory();
        partialUpdatedStockSplitHistory.setId(stockSplitHistory.getId());

        partialUpdatedStockSplitHistory.ratio(UPDATED_RATIO);

        restStockSplitHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockSplitHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockSplitHistory))
            )
            .andExpect(status().isOk());

        // Validate the StockSplitHistory in the database
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeUpdate);
        StockSplitHistory testStockSplitHistory = stockSplitHistoryList.get(stockSplitHistoryList.size() - 1);
        assertThat(testStockSplitHistory.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testStockSplitHistory.getRatio()).isEqualTo(UPDATED_RATIO);
    }

    @Test
    @Transactional
    void fullUpdateStockSplitHistoryWithPatch() throws Exception {
        // Initialize the database
        stockSplitHistoryRepository.saveAndFlush(stockSplitHistory);

        int databaseSizeBeforeUpdate = stockSplitHistoryRepository.findAll().size();

        // Update the stockSplitHistory using partial update
        StockSplitHistory partialUpdatedStockSplitHistory = new StockSplitHistory();
        partialUpdatedStockSplitHistory.setId(stockSplitHistory.getId());

        partialUpdatedStockSplitHistory.date(UPDATED_DATE).ratio(UPDATED_RATIO);

        restStockSplitHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockSplitHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockSplitHistory))
            )
            .andExpect(status().isOk());

        // Validate the StockSplitHistory in the database
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeUpdate);
        StockSplitHistory testStockSplitHistory = stockSplitHistoryList.get(stockSplitHistoryList.size() - 1);
        assertThat(testStockSplitHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testStockSplitHistory.getRatio()).isEqualTo(UPDATED_RATIO);
    }

    @Test
    @Transactional
    void patchNonExistingStockSplitHistory() throws Exception {
        int databaseSizeBeforeUpdate = stockSplitHistoryRepository.findAll().size();
        stockSplitHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockSplitHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockSplitHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockSplitHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockSplitHistory in the database
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockSplitHistory in Elasticsearch
        verify(mockStockSplitHistorySearchRepository, times(0)).save(stockSplitHistory);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockSplitHistory() throws Exception {
        int databaseSizeBeforeUpdate = stockSplitHistoryRepository.findAll().size();
        stockSplitHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockSplitHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockSplitHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockSplitHistory in the database
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockSplitHistory in Elasticsearch
        verify(mockStockSplitHistorySearchRepository, times(0)).save(stockSplitHistory);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockSplitHistory() throws Exception {
        int databaseSizeBeforeUpdate = stockSplitHistoryRepository.findAll().size();
        stockSplitHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockSplitHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockSplitHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockSplitHistory in the database
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockSplitHistory in Elasticsearch
        verify(mockStockSplitHistorySearchRepository, times(0)).save(stockSplitHistory);
    }

    @Test
    @Transactional
    void deleteStockSplitHistory() throws Exception {
        // Initialize the database
        stockSplitHistoryRepository.saveAndFlush(stockSplitHistory);

        int databaseSizeBeforeDelete = stockSplitHistoryRepository.findAll().size();

        // Delete the stockSplitHistory
        restStockSplitHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockSplitHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StockSplitHistory> stockSplitHistoryList = stockSplitHistoryRepository.findAll();
        assertThat(stockSplitHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the StockSplitHistory in Elasticsearch
        verify(mockStockSplitHistorySearchRepository, times(1)).deleteById(stockSplitHistory.getId());
    }

    @Test
    @Transactional
    void searchStockSplitHistory() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        stockSplitHistoryRepository.saveAndFlush(stockSplitHistory);
        when(mockStockSplitHistorySearchRepository.search("id:" + stockSplitHistory.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(stockSplitHistory), PageRequest.of(0, 1), 1));

        // Search the stockSplitHistory
        restStockSplitHistoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + stockSplitHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockSplitHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].ratio").value(hasItem(DEFAULT_RATIO.doubleValue())));
    }
}
