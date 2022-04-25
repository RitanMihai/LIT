package com.ritan.lit.portfolio.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.portfolio.IntegrationTest;
import com.ritan.lit.portfolio.domain.PortfolioCurrency;
import com.ritan.lit.portfolio.repository.PortfolioCurrencyRepository;
import com.ritan.lit.portfolio.repository.search.PortfolioCurrencySearchRepository;
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
 * Integration tests for the {@link PortfolioCurrencyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PortfolioCurrencyResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_SYMBOL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/portfolio-currencies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/portfolio-currencies";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PortfolioCurrencyRepository portfolioCurrencyRepository;

    /**
     * This repository is mocked in the com.ritan.lit.portfolio.repository.search test package.
     *
     * @see com.ritan.lit.portfolio.repository.search.PortfolioCurrencySearchRepositoryMockConfiguration
     */
    @Autowired
    private PortfolioCurrencySearchRepository mockPortfolioCurrencySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPortfolioCurrencyMockMvc;

    private PortfolioCurrency portfolioCurrency;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PortfolioCurrency createEntity(EntityManager em) {
        PortfolioCurrency portfolioCurrency = new PortfolioCurrency()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .currencySymbol(DEFAULT_CURRENCY_SYMBOL);
        return portfolioCurrency;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PortfolioCurrency createUpdatedEntity(EntityManager em) {
        PortfolioCurrency portfolioCurrency = new PortfolioCurrency()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .currencySymbol(UPDATED_CURRENCY_SYMBOL);
        return portfolioCurrency;
    }

    @BeforeEach
    public void initTest() {
        portfolioCurrency = createEntity(em);
    }

    @Test
    @Transactional
    void createPortfolioCurrency() throws Exception {
        int databaseSizeBeforeCreate = portfolioCurrencyRepository.findAll().size();
        // Create the PortfolioCurrency
        restPortfolioCurrencyMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(portfolioCurrency))
            )
            .andExpect(status().isCreated());

        // Validate the PortfolioCurrency in the database
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeCreate + 1);
        PortfolioCurrency testPortfolioCurrency = portfolioCurrencyList.get(portfolioCurrencyList.size() - 1);
        assertThat(testPortfolioCurrency.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testPortfolioCurrency.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPortfolioCurrency.getCurrencySymbol()).isEqualTo(DEFAULT_CURRENCY_SYMBOL);

        // Validate the PortfolioCurrency in Elasticsearch
        verify(mockPortfolioCurrencySearchRepository, times(1)).save(testPortfolioCurrency);
    }

    @Test
    @Transactional
    void createPortfolioCurrencyWithExistingId() throws Exception {
        // Create the PortfolioCurrency with an existing ID
        portfolioCurrency.setId(1L);

        int databaseSizeBeforeCreate = portfolioCurrencyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPortfolioCurrencyMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(portfolioCurrency))
            )
            .andExpect(status().isBadRequest());

        // Validate the PortfolioCurrency in the database
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeCreate);

        // Validate the PortfolioCurrency in Elasticsearch
        verify(mockPortfolioCurrencySearchRepository, times(0)).save(portfolioCurrency);
    }

    @Test
    @Transactional
    void getAllPortfolioCurrencies() throws Exception {
        // Initialize the database
        portfolioCurrencyRepository.saveAndFlush(portfolioCurrency);

        // Get all the portfolioCurrencyList
        restPortfolioCurrencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(portfolioCurrency.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].currencySymbol").value(hasItem(DEFAULT_CURRENCY_SYMBOL)));
    }

    @Test
    @Transactional
    void getPortfolioCurrency() throws Exception {
        // Initialize the database
        portfolioCurrencyRepository.saveAndFlush(portfolioCurrency);

        // Get the portfolioCurrency
        restPortfolioCurrencyMockMvc
            .perform(get(ENTITY_API_URL_ID, portfolioCurrency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(portfolioCurrency.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.currencySymbol").value(DEFAULT_CURRENCY_SYMBOL));
    }

    @Test
    @Transactional
    void getNonExistingPortfolioCurrency() throws Exception {
        // Get the portfolioCurrency
        restPortfolioCurrencyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPortfolioCurrency() throws Exception {
        // Initialize the database
        portfolioCurrencyRepository.saveAndFlush(portfolioCurrency);

        int databaseSizeBeforeUpdate = portfolioCurrencyRepository.findAll().size();

        // Update the portfolioCurrency
        PortfolioCurrency updatedPortfolioCurrency = portfolioCurrencyRepository.findById(portfolioCurrency.getId()).get();
        // Disconnect from session so that the updates on updatedPortfolioCurrency are not directly saved in db
        em.detach(updatedPortfolioCurrency);
        updatedPortfolioCurrency.code(UPDATED_CODE).name(UPDATED_NAME).currencySymbol(UPDATED_CURRENCY_SYMBOL);

        restPortfolioCurrencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPortfolioCurrency.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPortfolioCurrency))
            )
            .andExpect(status().isOk());

        // Validate the PortfolioCurrency in the database
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeUpdate);
        PortfolioCurrency testPortfolioCurrency = portfolioCurrencyList.get(portfolioCurrencyList.size() - 1);
        assertThat(testPortfolioCurrency.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testPortfolioCurrency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPortfolioCurrency.getCurrencySymbol()).isEqualTo(UPDATED_CURRENCY_SYMBOL);

        // Validate the PortfolioCurrency in Elasticsearch
        verify(mockPortfolioCurrencySearchRepository).save(testPortfolioCurrency);
    }

    @Test
    @Transactional
    void putNonExistingPortfolioCurrency() throws Exception {
        int databaseSizeBeforeUpdate = portfolioCurrencyRepository.findAll().size();
        portfolioCurrency.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPortfolioCurrencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, portfolioCurrency.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(portfolioCurrency))
            )
            .andExpect(status().isBadRequest());

        // Validate the PortfolioCurrency in the database
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioCurrency in Elasticsearch
        verify(mockPortfolioCurrencySearchRepository, times(0)).save(portfolioCurrency);
    }

    @Test
    @Transactional
    void putWithIdMismatchPortfolioCurrency() throws Exception {
        int databaseSizeBeforeUpdate = portfolioCurrencyRepository.findAll().size();
        portfolioCurrency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioCurrencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(portfolioCurrency))
            )
            .andExpect(status().isBadRequest());

        // Validate the PortfolioCurrency in the database
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioCurrency in Elasticsearch
        verify(mockPortfolioCurrencySearchRepository, times(0)).save(portfolioCurrency);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPortfolioCurrency() throws Exception {
        int databaseSizeBeforeUpdate = portfolioCurrencyRepository.findAll().size();
        portfolioCurrency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioCurrencyMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(portfolioCurrency))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PortfolioCurrency in the database
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioCurrency in Elasticsearch
        verify(mockPortfolioCurrencySearchRepository, times(0)).save(portfolioCurrency);
    }

    @Test
    @Transactional
    void partialUpdatePortfolioCurrencyWithPatch() throws Exception {
        // Initialize the database
        portfolioCurrencyRepository.saveAndFlush(portfolioCurrency);

        int databaseSizeBeforeUpdate = portfolioCurrencyRepository.findAll().size();

        // Update the portfolioCurrency using partial update
        PortfolioCurrency partialUpdatedPortfolioCurrency = new PortfolioCurrency();
        partialUpdatedPortfolioCurrency.setId(portfolioCurrency.getId());

        partialUpdatedPortfolioCurrency.code(UPDATED_CODE).currencySymbol(UPDATED_CURRENCY_SYMBOL);

        restPortfolioCurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPortfolioCurrency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPortfolioCurrency))
            )
            .andExpect(status().isOk());

        // Validate the PortfolioCurrency in the database
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeUpdate);
        PortfolioCurrency testPortfolioCurrency = portfolioCurrencyList.get(portfolioCurrencyList.size() - 1);
        assertThat(testPortfolioCurrency.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testPortfolioCurrency.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPortfolioCurrency.getCurrencySymbol()).isEqualTo(UPDATED_CURRENCY_SYMBOL);
    }

    @Test
    @Transactional
    void fullUpdatePortfolioCurrencyWithPatch() throws Exception {
        // Initialize the database
        portfolioCurrencyRepository.saveAndFlush(portfolioCurrency);

        int databaseSizeBeforeUpdate = portfolioCurrencyRepository.findAll().size();

        // Update the portfolioCurrency using partial update
        PortfolioCurrency partialUpdatedPortfolioCurrency = new PortfolioCurrency();
        partialUpdatedPortfolioCurrency.setId(portfolioCurrency.getId());

        partialUpdatedPortfolioCurrency.code(UPDATED_CODE).name(UPDATED_NAME).currencySymbol(UPDATED_CURRENCY_SYMBOL);

        restPortfolioCurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPortfolioCurrency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPortfolioCurrency))
            )
            .andExpect(status().isOk());

        // Validate the PortfolioCurrency in the database
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeUpdate);
        PortfolioCurrency testPortfolioCurrency = portfolioCurrencyList.get(portfolioCurrencyList.size() - 1);
        assertThat(testPortfolioCurrency.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testPortfolioCurrency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPortfolioCurrency.getCurrencySymbol()).isEqualTo(UPDATED_CURRENCY_SYMBOL);
    }

    @Test
    @Transactional
    void patchNonExistingPortfolioCurrency() throws Exception {
        int databaseSizeBeforeUpdate = portfolioCurrencyRepository.findAll().size();
        portfolioCurrency.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPortfolioCurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, portfolioCurrency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(portfolioCurrency))
            )
            .andExpect(status().isBadRequest());

        // Validate the PortfolioCurrency in the database
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioCurrency in Elasticsearch
        verify(mockPortfolioCurrencySearchRepository, times(0)).save(portfolioCurrency);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPortfolioCurrency() throws Exception {
        int databaseSizeBeforeUpdate = portfolioCurrencyRepository.findAll().size();
        portfolioCurrency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioCurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(portfolioCurrency))
            )
            .andExpect(status().isBadRequest());

        // Validate the PortfolioCurrency in the database
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioCurrency in Elasticsearch
        verify(mockPortfolioCurrencySearchRepository, times(0)).save(portfolioCurrency);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPortfolioCurrency() throws Exception {
        int databaseSizeBeforeUpdate = portfolioCurrencyRepository.findAll().size();
        portfolioCurrency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioCurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(portfolioCurrency))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PortfolioCurrency in the database
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioCurrency in Elasticsearch
        verify(mockPortfolioCurrencySearchRepository, times(0)).save(portfolioCurrency);
    }

    @Test
    @Transactional
    void deletePortfolioCurrency() throws Exception {
        // Initialize the database
        portfolioCurrencyRepository.saveAndFlush(portfolioCurrency);

        int databaseSizeBeforeDelete = portfolioCurrencyRepository.findAll().size();

        // Delete the portfolioCurrency
        restPortfolioCurrencyMockMvc
            .perform(delete(ENTITY_API_URL_ID, portfolioCurrency.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PortfolioCurrency> portfolioCurrencyList = portfolioCurrencyRepository.findAll();
        assertThat(portfolioCurrencyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PortfolioCurrency in Elasticsearch
        verify(mockPortfolioCurrencySearchRepository, times(1)).deleteById(portfolioCurrency.getId());
    }

    @Test
    @Transactional
    void searchPortfolioCurrency() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        portfolioCurrencyRepository.saveAndFlush(portfolioCurrency);
        when(mockPortfolioCurrencySearchRepository.search("id:" + portfolioCurrency.getId())).thenReturn(Stream.of(portfolioCurrency));

        // Search the portfolioCurrency
        restPortfolioCurrencyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + portfolioCurrency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(portfolioCurrency.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].currencySymbol").value(hasItem(DEFAULT_CURRENCY_SYMBOL)));
    }
}
