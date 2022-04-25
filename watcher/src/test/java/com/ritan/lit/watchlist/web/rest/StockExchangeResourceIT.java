package com.ritan.lit.watchlist.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.watchlist.IntegrationTest;
import com.ritan.lit.watchlist.domain.StockExchange;
import com.ritan.lit.watchlist.repository.StockExchangeRepository;
import com.ritan.lit.watchlist.repository.search.StockExchangeSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StockExchangeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockExchangeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stock-exchanges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/stock-exchanges";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockExchangeRepository stockExchangeRepository;

    /**
     * This repository is mocked in the com.ritan.lit.watchlist.repository.search test package.
     *
     * @see com.ritan.lit.watchlist.repository.search.StockExchangeSearchRepositoryMockConfiguration
     */
    @Autowired
    private StockExchangeSearchRepository mockStockExchangeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockExchangeMockMvc;

    private StockExchange stockExchange;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockExchange createEntity(EntityManager em) {
        StockExchange stockExchange = new StockExchange().name(DEFAULT_NAME).symbol(DEFAULT_SYMBOL).country(DEFAULT_COUNTRY);
        return stockExchange;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockExchange createUpdatedEntity(EntityManager em) {
        StockExchange stockExchange = new StockExchange().name(UPDATED_NAME).symbol(UPDATED_SYMBOL).country(UPDATED_COUNTRY);
        return stockExchange;
    }

    @BeforeEach
    public void initTest() {
        stockExchange = createEntity(em);
    }

    @Test
    @Transactional
    void createStockExchange() throws Exception {
        int databaseSizeBeforeCreate = stockExchangeRepository.findAll().size();
        // Create the StockExchange
        restStockExchangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockExchange)))
            .andExpect(status().isCreated());

        // Validate the StockExchange in the database
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeCreate + 1);
        StockExchange testStockExchange = stockExchangeList.get(stockExchangeList.size() - 1);
        assertThat(testStockExchange.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStockExchange.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testStockExchange.getCountry()).isEqualTo(DEFAULT_COUNTRY);

        // Validate the StockExchange in Elasticsearch
        verify(mockStockExchangeSearchRepository, times(1)).save(testStockExchange);
    }

    @Test
    @Transactional
    void createStockExchangeWithExistingId() throws Exception {
        // Create the StockExchange with an existing ID
        stockExchange.setId(1L);

        int databaseSizeBeforeCreate = stockExchangeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockExchangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockExchange)))
            .andExpect(status().isBadRequest());

        // Validate the StockExchange in the database
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeCreate);

        // Validate the StockExchange in Elasticsearch
        verify(mockStockExchangeSearchRepository, times(0)).save(stockExchange);
    }

    @Test
    @Transactional
    void getAllStockExchanges() throws Exception {
        // Initialize the database
        stockExchangeRepository.saveAndFlush(stockExchange);

        // Get all the stockExchangeList
        restStockExchangeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockExchange.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)));
    }

    @Test
    @Transactional
    void getStockExchange() throws Exception {
        // Initialize the database
        stockExchangeRepository.saveAndFlush(stockExchange);

        // Get the stockExchange
        restStockExchangeMockMvc
            .perform(get(ENTITY_API_URL_ID, stockExchange.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockExchange.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY));
    }

    @Test
    @Transactional
    void getNonExistingStockExchange() throws Exception {
        // Get the stockExchange
        restStockExchangeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewStockExchange() throws Exception {
        // Initialize the database
        stockExchangeRepository.saveAndFlush(stockExchange);

        int databaseSizeBeforeUpdate = stockExchangeRepository.findAll().size();

        // Update the stockExchange
        StockExchange updatedStockExchange = stockExchangeRepository.findById(stockExchange.getId()).get();
        // Disconnect from session so that the updates on updatedStockExchange are not directly saved in db
        em.detach(updatedStockExchange);
        updatedStockExchange.name(UPDATED_NAME).symbol(UPDATED_SYMBOL).country(UPDATED_COUNTRY);

        restStockExchangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStockExchange.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStockExchange))
            )
            .andExpect(status().isOk());

        // Validate the StockExchange in the database
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeUpdate);
        StockExchange testStockExchange = stockExchangeList.get(stockExchangeList.size() - 1);
        assertThat(testStockExchange.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStockExchange.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testStockExchange.getCountry()).isEqualTo(UPDATED_COUNTRY);

        // Validate the StockExchange in Elasticsearch
        verify(mockStockExchangeSearchRepository).save(testStockExchange);
    }

    @Test
    @Transactional
    void putNonExistingStockExchange() throws Exception {
        int databaseSizeBeforeUpdate = stockExchangeRepository.findAll().size();
        stockExchange.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockExchangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockExchange.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockExchange))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockExchange in the database
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockExchange in Elasticsearch
        verify(mockStockExchangeSearchRepository, times(0)).save(stockExchange);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockExchange() throws Exception {
        int databaseSizeBeforeUpdate = stockExchangeRepository.findAll().size();
        stockExchange.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockExchangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockExchange))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockExchange in the database
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockExchange in Elasticsearch
        verify(mockStockExchangeSearchRepository, times(0)).save(stockExchange);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockExchange() throws Exception {
        int databaseSizeBeforeUpdate = stockExchangeRepository.findAll().size();
        stockExchange.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockExchangeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockExchange)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockExchange in the database
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockExchange in Elasticsearch
        verify(mockStockExchangeSearchRepository, times(0)).save(stockExchange);
    }

    @Test
    @Transactional
    void partialUpdateStockExchangeWithPatch() throws Exception {
        // Initialize the database
        stockExchangeRepository.saveAndFlush(stockExchange);

        int databaseSizeBeforeUpdate = stockExchangeRepository.findAll().size();

        // Update the stockExchange using partial update
        StockExchange partialUpdatedStockExchange = new StockExchange();
        partialUpdatedStockExchange.setId(stockExchange.getId());

        partialUpdatedStockExchange.name(UPDATED_NAME);

        restStockExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockExchange.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockExchange))
            )
            .andExpect(status().isOk());

        // Validate the StockExchange in the database
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeUpdate);
        StockExchange testStockExchange = stockExchangeList.get(stockExchangeList.size() - 1);
        assertThat(testStockExchange.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStockExchange.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testStockExchange.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    @Transactional
    void fullUpdateStockExchangeWithPatch() throws Exception {
        // Initialize the database
        stockExchangeRepository.saveAndFlush(stockExchange);

        int databaseSizeBeforeUpdate = stockExchangeRepository.findAll().size();

        // Update the stockExchange using partial update
        StockExchange partialUpdatedStockExchange = new StockExchange();
        partialUpdatedStockExchange.setId(stockExchange.getId());

        partialUpdatedStockExchange.name(UPDATED_NAME).symbol(UPDATED_SYMBOL).country(UPDATED_COUNTRY);

        restStockExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockExchange.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockExchange))
            )
            .andExpect(status().isOk());

        // Validate the StockExchange in the database
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeUpdate);
        StockExchange testStockExchange = stockExchangeList.get(stockExchangeList.size() - 1);
        assertThat(testStockExchange.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStockExchange.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testStockExchange.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void patchNonExistingStockExchange() throws Exception {
        int databaseSizeBeforeUpdate = stockExchangeRepository.findAll().size();
        stockExchange.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockExchange.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockExchange))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockExchange in the database
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockExchange in Elasticsearch
        verify(mockStockExchangeSearchRepository, times(0)).save(stockExchange);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockExchange() throws Exception {
        int databaseSizeBeforeUpdate = stockExchangeRepository.findAll().size();
        stockExchange.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockExchange))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockExchange in the database
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockExchange in Elasticsearch
        verify(mockStockExchangeSearchRepository, times(0)).save(stockExchange);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockExchange() throws Exception {
        int databaseSizeBeforeUpdate = stockExchangeRepository.findAll().size();
        stockExchange.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(stockExchange))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockExchange in the database
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockExchange in Elasticsearch
        verify(mockStockExchangeSearchRepository, times(0)).save(stockExchange);
    }

    @Test
    @Transactional
    void deleteStockExchange() throws Exception {
        // Initialize the database
        stockExchangeRepository.saveAndFlush(stockExchange);

        int databaseSizeBeforeDelete = stockExchangeRepository.findAll().size();

        // Delete the stockExchange
        restStockExchangeMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockExchange.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StockExchange> stockExchangeList = stockExchangeRepository.findAll();
        assertThat(stockExchangeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the StockExchange in Elasticsearch
        verify(mockStockExchangeSearchRepository, times(1)).deleteById(stockExchange.getId());
    }

    @Test
    @Transactional
    void searchStockExchange() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        stockExchangeRepository.saveAndFlush(stockExchange);
        when(mockStockExchangeSearchRepository.search("id:" + stockExchange.getId())).thenReturn(Stream.of(stockExchange));

        // Search the stockExchange
        restStockExchangeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + stockExchange.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockExchange.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)));
    }
}
