package com.ritan.lit.portfolio.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.portfolio.IntegrationTest;
import com.ritan.lit.portfolio.domain.Dividend;
import com.ritan.lit.portfolio.domain.enumeration.DividendType;
import com.ritan.lit.portfolio.repository.DividendRepository;
import com.ritan.lit.portfolio.repository.search.DividendSearchRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link DividendResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DividendResourceIT {

    private static final Instant DEFAULT_DATE_RECIVED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_RECIVED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_TAX_RATE = 1;
    private static final Integer UPDATED_TAX_RATE = 2;

    private static final Double DEFAULT_TOTAL_RECIVED = 1D;
    private static final Double UPDATED_TOTAL_RECIVED = 2D;

    private static final DividendType DEFAULT_DIVIDEND_TYPE = DividendType.PROPERTY_INCOME;
    private static final DividendType UPDATED_DIVIDEND_TYPE = DividendType.ORDINARY;

    private static final String ENTITY_API_URL = "/api/dividends";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/dividends";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DividendRepository dividendRepository;

    /**
     * This repository is mocked in the com.ritan.lit.portfolio.repository.search test package.
     *
     * @see com.ritan.lit.portfolio.repository.search.DividendSearchRepositoryMockConfiguration
     */
    @Autowired
    private DividendSearchRepository mockDividendSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDividendMockMvc;

    private Dividend dividend;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dividend createEntity(EntityManager em) {
        Dividend dividend = new Dividend()
            .dateRecived(DEFAULT_DATE_RECIVED)
            .taxRate(DEFAULT_TAX_RATE)
            .totalRecived(DEFAULT_TOTAL_RECIVED)
            .dividendType(DEFAULT_DIVIDEND_TYPE);
        return dividend;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dividend createUpdatedEntity(EntityManager em) {
        Dividend dividend = new Dividend()
            .dateRecived(UPDATED_DATE_RECIVED)
            .taxRate(UPDATED_TAX_RATE)
            .totalRecived(UPDATED_TOTAL_RECIVED)
            .dividendType(UPDATED_DIVIDEND_TYPE);
        return dividend;
    }

    @BeforeEach
    public void initTest() {
        dividend = createEntity(em);
    }

    @Test
    @Transactional
    void createDividend() throws Exception {
        int databaseSizeBeforeCreate = dividendRepository.findAll().size();
        // Create the Dividend
        restDividendMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dividend)))
            .andExpect(status().isCreated());

        // Validate the Dividend in the database
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeCreate + 1);
        Dividend testDividend = dividendList.get(dividendList.size() - 1);
        assertThat(testDividend.getDateRecived()).isEqualTo(DEFAULT_DATE_RECIVED);
        assertThat(testDividend.getTaxRate()).isEqualTo(DEFAULT_TAX_RATE);
        assertThat(testDividend.getTotalRecived()).isEqualTo(DEFAULT_TOTAL_RECIVED);
        assertThat(testDividend.getDividendType()).isEqualTo(DEFAULT_DIVIDEND_TYPE);

        // Validate the Dividend in Elasticsearch
        verify(mockDividendSearchRepository, times(1)).save(testDividend);
    }

    @Test
    @Transactional
    void createDividendWithExistingId() throws Exception {
        // Create the Dividend with an existing ID
        dividend.setId(1L);

        int databaseSizeBeforeCreate = dividendRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDividendMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dividend)))
            .andExpect(status().isBadRequest());

        // Validate the Dividend in the database
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeCreate);

        // Validate the Dividend in Elasticsearch
        verify(mockDividendSearchRepository, times(0)).save(dividend);
    }

    @Test
    @Transactional
    void getAllDividends() throws Exception {
        // Initialize the database
        dividendRepository.saveAndFlush(dividend);

        // Get all the dividendList
        restDividendMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dividend.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateRecived").value(hasItem(DEFAULT_DATE_RECIVED.toString())))
            .andExpect(jsonPath("$.[*].taxRate").value(hasItem(DEFAULT_TAX_RATE)))
            .andExpect(jsonPath("$.[*].totalRecived").value(hasItem(DEFAULT_TOTAL_RECIVED.doubleValue())))
            .andExpect(jsonPath("$.[*].dividendType").value(hasItem(DEFAULT_DIVIDEND_TYPE.toString())));
    }

    @Test
    @Transactional
    void getDividend() throws Exception {
        // Initialize the database
        dividendRepository.saveAndFlush(dividend);

        // Get the dividend
        restDividendMockMvc
            .perform(get(ENTITY_API_URL_ID, dividend.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dividend.getId().intValue()))
            .andExpect(jsonPath("$.dateRecived").value(DEFAULT_DATE_RECIVED.toString()))
            .andExpect(jsonPath("$.taxRate").value(DEFAULT_TAX_RATE))
            .andExpect(jsonPath("$.totalRecived").value(DEFAULT_TOTAL_RECIVED.doubleValue()))
            .andExpect(jsonPath("$.dividendType").value(DEFAULT_DIVIDEND_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDividend() throws Exception {
        // Get the dividend
        restDividendMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDividend() throws Exception {
        // Initialize the database
        dividendRepository.saveAndFlush(dividend);

        int databaseSizeBeforeUpdate = dividendRepository.findAll().size();

        // Update the dividend
        Dividend updatedDividend = dividendRepository.findById(dividend.getId()).get();
        // Disconnect from session so that the updates on updatedDividend are not directly saved in db
        em.detach(updatedDividend);
        updatedDividend
            .dateRecived(UPDATED_DATE_RECIVED)
            .taxRate(UPDATED_TAX_RATE)
            .totalRecived(UPDATED_TOTAL_RECIVED)
            .dividendType(UPDATED_DIVIDEND_TYPE);

        restDividendMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDividend.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDividend))
            )
            .andExpect(status().isOk());

        // Validate the Dividend in the database
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeUpdate);
        Dividend testDividend = dividendList.get(dividendList.size() - 1);
        assertThat(testDividend.getDateRecived()).isEqualTo(UPDATED_DATE_RECIVED);
        assertThat(testDividend.getTaxRate()).isEqualTo(UPDATED_TAX_RATE);
        assertThat(testDividend.getTotalRecived()).isEqualTo(UPDATED_TOTAL_RECIVED);
        assertThat(testDividend.getDividendType()).isEqualTo(UPDATED_DIVIDEND_TYPE);

        // Validate the Dividend in Elasticsearch
        verify(mockDividendSearchRepository).save(testDividend);
    }

    @Test
    @Transactional
    void putNonExistingDividend() throws Exception {
        int databaseSizeBeforeUpdate = dividendRepository.findAll().size();
        dividend.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDividendMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dividend.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dividend))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dividend in the database
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dividend in Elasticsearch
        verify(mockDividendSearchRepository, times(0)).save(dividend);
    }

    @Test
    @Transactional
    void putWithIdMismatchDividend() throws Exception {
        int databaseSizeBeforeUpdate = dividendRepository.findAll().size();
        dividend.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDividendMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dividend))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dividend in the database
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dividend in Elasticsearch
        verify(mockDividendSearchRepository, times(0)).save(dividend);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDividend() throws Exception {
        int databaseSizeBeforeUpdate = dividendRepository.findAll().size();
        dividend.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDividendMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dividend)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dividend in the database
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dividend in Elasticsearch
        verify(mockDividendSearchRepository, times(0)).save(dividend);
    }

    @Test
    @Transactional
    void partialUpdateDividendWithPatch() throws Exception {
        // Initialize the database
        dividendRepository.saveAndFlush(dividend);

        int databaseSizeBeforeUpdate = dividendRepository.findAll().size();

        // Update the dividend using partial update
        Dividend partialUpdatedDividend = new Dividend();
        partialUpdatedDividend.setId(dividend.getId());

        partialUpdatedDividend.totalRecived(UPDATED_TOTAL_RECIVED);

        restDividendMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDividend.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDividend))
            )
            .andExpect(status().isOk());

        // Validate the Dividend in the database
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeUpdate);
        Dividend testDividend = dividendList.get(dividendList.size() - 1);
        assertThat(testDividend.getDateRecived()).isEqualTo(DEFAULT_DATE_RECIVED);
        assertThat(testDividend.getTaxRate()).isEqualTo(DEFAULT_TAX_RATE);
        assertThat(testDividend.getTotalRecived()).isEqualTo(UPDATED_TOTAL_RECIVED);
        assertThat(testDividend.getDividendType()).isEqualTo(DEFAULT_DIVIDEND_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateDividendWithPatch() throws Exception {
        // Initialize the database
        dividendRepository.saveAndFlush(dividend);

        int databaseSizeBeforeUpdate = dividendRepository.findAll().size();

        // Update the dividend using partial update
        Dividend partialUpdatedDividend = new Dividend();
        partialUpdatedDividend.setId(dividend.getId());

        partialUpdatedDividend
            .dateRecived(UPDATED_DATE_RECIVED)
            .taxRate(UPDATED_TAX_RATE)
            .totalRecived(UPDATED_TOTAL_RECIVED)
            .dividendType(UPDATED_DIVIDEND_TYPE);

        restDividendMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDividend.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDividend))
            )
            .andExpect(status().isOk());

        // Validate the Dividend in the database
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeUpdate);
        Dividend testDividend = dividendList.get(dividendList.size() - 1);
        assertThat(testDividend.getDateRecived()).isEqualTo(UPDATED_DATE_RECIVED);
        assertThat(testDividend.getTaxRate()).isEqualTo(UPDATED_TAX_RATE);
        assertThat(testDividend.getTotalRecived()).isEqualTo(UPDATED_TOTAL_RECIVED);
        assertThat(testDividend.getDividendType()).isEqualTo(UPDATED_DIVIDEND_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingDividend() throws Exception {
        int databaseSizeBeforeUpdate = dividendRepository.findAll().size();
        dividend.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDividendMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dividend.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dividend))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dividend in the database
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dividend in Elasticsearch
        verify(mockDividendSearchRepository, times(0)).save(dividend);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDividend() throws Exception {
        int databaseSizeBeforeUpdate = dividendRepository.findAll().size();
        dividend.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDividendMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dividend))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dividend in the database
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dividend in Elasticsearch
        verify(mockDividendSearchRepository, times(0)).save(dividend);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDividend() throws Exception {
        int databaseSizeBeforeUpdate = dividendRepository.findAll().size();
        dividend.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDividendMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dividend)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dividend in the database
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dividend in Elasticsearch
        verify(mockDividendSearchRepository, times(0)).save(dividend);
    }

    @Test
    @Transactional
    void deleteDividend() throws Exception {
        // Initialize the database
        dividendRepository.saveAndFlush(dividend);

        int databaseSizeBeforeDelete = dividendRepository.findAll().size();

        // Delete the dividend
        restDividendMockMvc
            .perform(delete(ENTITY_API_URL_ID, dividend.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Dividend> dividendList = dividendRepository.findAll();
        assertThat(dividendList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Dividend in Elasticsearch
        verify(mockDividendSearchRepository, times(1)).deleteById(dividend.getId());
    }

    @Test
    @Transactional
    void searchDividend() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        dividendRepository.saveAndFlush(dividend);
        when(mockDividendSearchRepository.search("id:" + dividend.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(dividend), PageRequest.of(0, 1), 1));

        // Search the dividend
        restDividendMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + dividend.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dividend.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateRecived").value(hasItem(DEFAULT_DATE_RECIVED.toString())))
            .andExpect(jsonPath("$.[*].taxRate").value(hasItem(DEFAULT_TAX_RATE)))
            .andExpect(jsonPath("$.[*].totalRecived").value(hasItem(DEFAULT_TOTAL_RECIVED.doubleValue())))
            .andExpect(jsonPath("$.[*].dividendType").value(hasItem(DEFAULT_DIVIDEND_TYPE.toString())));
    }
}
