package com.ritan.lit.watchlist.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.watchlist.IntegrationTest;
import com.ritan.lit.watchlist.domain.IncomeHistory;
import com.ritan.lit.watchlist.repository.IncomeHistoryRepository;
import com.ritan.lit.watchlist.repository.search.IncomeHistorySearchRepository;
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
 * Integration tests for the {@link IncomeHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class IncomeHistoryResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_TOTAL_REVENUE = 1L;
    private static final Long UPDATED_TOTAL_REVENUE = 2L;

    private static final Long DEFAULT_COST_OF_REVENUE = 1L;
    private static final Long UPDATED_COST_OF_REVENUE = 2L;

    private static final Long DEFAULT_GROSS_PROFIT = 1L;
    private static final Long UPDATED_GROSS_PROFIT = 2L;

    private static final Long DEFAULT_OPERATING_EXPENSE = 1L;
    private static final Long UPDATED_OPERATING_EXPENSE = 2L;

    private static final Long DEFAULT_OPERATING_INCOME = 1L;
    private static final Long UPDATED_OPERATING_INCOME = 2L;

    private static final String ENTITY_API_URL = "/api/income-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/income-histories";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IncomeHistoryRepository incomeHistoryRepository;

    /**
     * This repository is mocked in the com.ritan.lit.watchlist.repository.search test package.
     *
     * @see com.ritan.lit.watchlist.repository.search.IncomeHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private IncomeHistorySearchRepository mockIncomeHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIncomeHistoryMockMvc;

    private IncomeHistory incomeHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IncomeHistory createEntity(EntityManager em) {
        IncomeHistory incomeHistory = new IncomeHistory()
            .date(DEFAULT_DATE)
            .totalRevenue(DEFAULT_TOTAL_REVENUE)
            .costOfRevenue(DEFAULT_COST_OF_REVENUE)
            .grossProfit(DEFAULT_GROSS_PROFIT)
            .operatingExpense(DEFAULT_OPERATING_EXPENSE)
            .operatingIncome(DEFAULT_OPERATING_INCOME);
        return incomeHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IncomeHistory createUpdatedEntity(EntityManager em) {
        IncomeHistory incomeHistory = new IncomeHistory()
            .date(UPDATED_DATE)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .costOfRevenue(UPDATED_COST_OF_REVENUE)
            .grossProfit(UPDATED_GROSS_PROFIT)
            .operatingExpense(UPDATED_OPERATING_EXPENSE)
            .operatingIncome(UPDATED_OPERATING_INCOME);
        return incomeHistory;
    }

    @BeforeEach
    public void initTest() {
        incomeHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createIncomeHistory() throws Exception {
        int databaseSizeBeforeCreate = incomeHistoryRepository.findAll().size();
        // Create the IncomeHistory
        restIncomeHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(incomeHistory)))
            .andExpect(status().isCreated());

        // Validate the IncomeHistory in the database
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        IncomeHistory testIncomeHistory = incomeHistoryList.get(incomeHistoryList.size() - 1);
        assertThat(testIncomeHistory.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testIncomeHistory.getTotalRevenue()).isEqualTo(DEFAULT_TOTAL_REVENUE);
        assertThat(testIncomeHistory.getCostOfRevenue()).isEqualTo(DEFAULT_COST_OF_REVENUE);
        assertThat(testIncomeHistory.getGrossProfit()).isEqualTo(DEFAULT_GROSS_PROFIT);
        assertThat(testIncomeHistory.getOperatingExpense()).isEqualTo(DEFAULT_OPERATING_EXPENSE);
        assertThat(testIncomeHistory.getOperatingIncome()).isEqualTo(DEFAULT_OPERATING_INCOME);

        // Validate the IncomeHistory in Elasticsearch
        verify(mockIncomeHistorySearchRepository, times(1)).save(testIncomeHistory);
    }

    @Test
    @Transactional
    void createIncomeHistoryWithExistingId() throws Exception {
        // Create the IncomeHistory with an existing ID
        incomeHistory.setId(1L);

        int databaseSizeBeforeCreate = incomeHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIncomeHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(incomeHistory)))
            .andExpect(status().isBadRequest());

        // Validate the IncomeHistory in the database
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the IncomeHistory in Elasticsearch
        verify(mockIncomeHistorySearchRepository, times(0)).save(incomeHistory);
    }

    @Test
    @Transactional
    void getAllIncomeHistories() throws Exception {
        // Initialize the database
        incomeHistoryRepository.saveAndFlush(incomeHistory);

        // Get all the incomeHistoryList
        restIncomeHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(incomeHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].totalRevenue").value(hasItem(DEFAULT_TOTAL_REVENUE.intValue())))
            .andExpect(jsonPath("$.[*].costOfRevenue").value(hasItem(DEFAULT_COST_OF_REVENUE.intValue())))
            .andExpect(jsonPath("$.[*].grossProfit").value(hasItem(DEFAULT_GROSS_PROFIT.intValue())))
            .andExpect(jsonPath("$.[*].operatingExpense").value(hasItem(DEFAULT_OPERATING_EXPENSE.intValue())))
            .andExpect(jsonPath("$.[*].operatingIncome").value(hasItem(DEFAULT_OPERATING_INCOME.intValue())));
    }

    @Test
    @Transactional
    void getIncomeHistory() throws Exception {
        // Initialize the database
        incomeHistoryRepository.saveAndFlush(incomeHistory);

        // Get the incomeHistory
        restIncomeHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, incomeHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(incomeHistory.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.totalRevenue").value(DEFAULT_TOTAL_REVENUE.intValue()))
            .andExpect(jsonPath("$.costOfRevenue").value(DEFAULT_COST_OF_REVENUE.intValue()))
            .andExpect(jsonPath("$.grossProfit").value(DEFAULT_GROSS_PROFIT.intValue()))
            .andExpect(jsonPath("$.operatingExpense").value(DEFAULT_OPERATING_EXPENSE.intValue()))
            .andExpect(jsonPath("$.operatingIncome").value(DEFAULT_OPERATING_INCOME.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingIncomeHistory() throws Exception {
        // Get the incomeHistory
        restIncomeHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewIncomeHistory() throws Exception {
        // Initialize the database
        incomeHistoryRepository.saveAndFlush(incomeHistory);

        int databaseSizeBeforeUpdate = incomeHistoryRepository.findAll().size();

        // Update the incomeHistory
        IncomeHistory updatedIncomeHistory = incomeHistoryRepository.findById(incomeHistory.getId()).get();
        // Disconnect from session so that the updates on updatedIncomeHistory are not directly saved in db
        em.detach(updatedIncomeHistory);
        updatedIncomeHistory
            .date(UPDATED_DATE)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .costOfRevenue(UPDATED_COST_OF_REVENUE)
            .grossProfit(UPDATED_GROSS_PROFIT)
            .operatingExpense(UPDATED_OPERATING_EXPENSE)
            .operatingIncome(UPDATED_OPERATING_INCOME);

        restIncomeHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedIncomeHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedIncomeHistory))
            )
            .andExpect(status().isOk());

        // Validate the IncomeHistory in the database
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeUpdate);
        IncomeHistory testIncomeHistory = incomeHistoryList.get(incomeHistoryList.size() - 1);
        assertThat(testIncomeHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testIncomeHistory.getTotalRevenue()).isEqualTo(UPDATED_TOTAL_REVENUE);
        assertThat(testIncomeHistory.getCostOfRevenue()).isEqualTo(UPDATED_COST_OF_REVENUE);
        assertThat(testIncomeHistory.getGrossProfit()).isEqualTo(UPDATED_GROSS_PROFIT);
        assertThat(testIncomeHistory.getOperatingExpense()).isEqualTo(UPDATED_OPERATING_EXPENSE);
        assertThat(testIncomeHistory.getOperatingIncome()).isEqualTo(UPDATED_OPERATING_INCOME);

        // Validate the IncomeHistory in Elasticsearch
        verify(mockIncomeHistorySearchRepository).save(testIncomeHistory);
    }

    @Test
    @Transactional
    void putNonExistingIncomeHistory() throws Exception {
        int databaseSizeBeforeUpdate = incomeHistoryRepository.findAll().size();
        incomeHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIncomeHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, incomeHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(incomeHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the IncomeHistory in the database
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IncomeHistory in Elasticsearch
        verify(mockIncomeHistorySearchRepository, times(0)).save(incomeHistory);
    }

    @Test
    @Transactional
    void putWithIdMismatchIncomeHistory() throws Exception {
        int databaseSizeBeforeUpdate = incomeHistoryRepository.findAll().size();
        incomeHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncomeHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(incomeHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the IncomeHistory in the database
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IncomeHistory in Elasticsearch
        verify(mockIncomeHistorySearchRepository, times(0)).save(incomeHistory);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIncomeHistory() throws Exception {
        int databaseSizeBeforeUpdate = incomeHistoryRepository.findAll().size();
        incomeHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncomeHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(incomeHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the IncomeHistory in the database
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IncomeHistory in Elasticsearch
        verify(mockIncomeHistorySearchRepository, times(0)).save(incomeHistory);
    }

    @Test
    @Transactional
    void partialUpdateIncomeHistoryWithPatch() throws Exception {
        // Initialize the database
        incomeHistoryRepository.saveAndFlush(incomeHistory);

        int databaseSizeBeforeUpdate = incomeHistoryRepository.findAll().size();

        // Update the incomeHistory using partial update
        IncomeHistory partialUpdatedIncomeHistory = new IncomeHistory();
        partialUpdatedIncomeHistory.setId(incomeHistory.getId());

        partialUpdatedIncomeHistory
            .costOfRevenue(UPDATED_COST_OF_REVENUE)
            .grossProfit(UPDATED_GROSS_PROFIT)
            .operatingIncome(UPDATED_OPERATING_INCOME);

        restIncomeHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIncomeHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIncomeHistory))
            )
            .andExpect(status().isOk());

        // Validate the IncomeHistory in the database
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeUpdate);
        IncomeHistory testIncomeHistory = incomeHistoryList.get(incomeHistoryList.size() - 1);
        assertThat(testIncomeHistory.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testIncomeHistory.getTotalRevenue()).isEqualTo(DEFAULT_TOTAL_REVENUE);
        assertThat(testIncomeHistory.getCostOfRevenue()).isEqualTo(UPDATED_COST_OF_REVENUE);
        assertThat(testIncomeHistory.getGrossProfit()).isEqualTo(UPDATED_GROSS_PROFIT);
        assertThat(testIncomeHistory.getOperatingExpense()).isEqualTo(DEFAULT_OPERATING_EXPENSE);
        assertThat(testIncomeHistory.getOperatingIncome()).isEqualTo(UPDATED_OPERATING_INCOME);
    }

    @Test
    @Transactional
    void fullUpdateIncomeHistoryWithPatch() throws Exception {
        // Initialize the database
        incomeHistoryRepository.saveAndFlush(incomeHistory);

        int databaseSizeBeforeUpdate = incomeHistoryRepository.findAll().size();

        // Update the incomeHistory using partial update
        IncomeHistory partialUpdatedIncomeHistory = new IncomeHistory();
        partialUpdatedIncomeHistory.setId(incomeHistory.getId());

        partialUpdatedIncomeHistory
            .date(UPDATED_DATE)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .costOfRevenue(UPDATED_COST_OF_REVENUE)
            .grossProfit(UPDATED_GROSS_PROFIT)
            .operatingExpense(UPDATED_OPERATING_EXPENSE)
            .operatingIncome(UPDATED_OPERATING_INCOME);

        restIncomeHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIncomeHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIncomeHistory))
            )
            .andExpect(status().isOk());

        // Validate the IncomeHistory in the database
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeUpdate);
        IncomeHistory testIncomeHistory = incomeHistoryList.get(incomeHistoryList.size() - 1);
        assertThat(testIncomeHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testIncomeHistory.getTotalRevenue()).isEqualTo(UPDATED_TOTAL_REVENUE);
        assertThat(testIncomeHistory.getCostOfRevenue()).isEqualTo(UPDATED_COST_OF_REVENUE);
        assertThat(testIncomeHistory.getGrossProfit()).isEqualTo(UPDATED_GROSS_PROFIT);
        assertThat(testIncomeHistory.getOperatingExpense()).isEqualTo(UPDATED_OPERATING_EXPENSE);
        assertThat(testIncomeHistory.getOperatingIncome()).isEqualTo(UPDATED_OPERATING_INCOME);
    }

    @Test
    @Transactional
    void patchNonExistingIncomeHistory() throws Exception {
        int databaseSizeBeforeUpdate = incomeHistoryRepository.findAll().size();
        incomeHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIncomeHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, incomeHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(incomeHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the IncomeHistory in the database
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IncomeHistory in Elasticsearch
        verify(mockIncomeHistorySearchRepository, times(0)).save(incomeHistory);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIncomeHistory() throws Exception {
        int databaseSizeBeforeUpdate = incomeHistoryRepository.findAll().size();
        incomeHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncomeHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(incomeHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the IncomeHistory in the database
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IncomeHistory in Elasticsearch
        verify(mockIncomeHistorySearchRepository, times(0)).save(incomeHistory);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIncomeHistory() throws Exception {
        int databaseSizeBeforeUpdate = incomeHistoryRepository.findAll().size();
        incomeHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncomeHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(incomeHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the IncomeHistory in the database
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IncomeHistory in Elasticsearch
        verify(mockIncomeHistorySearchRepository, times(0)).save(incomeHistory);
    }

    @Test
    @Transactional
    void deleteIncomeHistory() throws Exception {
        // Initialize the database
        incomeHistoryRepository.saveAndFlush(incomeHistory);

        int databaseSizeBeforeDelete = incomeHistoryRepository.findAll().size();

        // Delete the incomeHistory
        restIncomeHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, incomeHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<IncomeHistory> incomeHistoryList = incomeHistoryRepository.findAll();
        assertThat(incomeHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the IncomeHistory in Elasticsearch
        verify(mockIncomeHistorySearchRepository, times(1)).deleteById(incomeHistory.getId());
    }

    @Test
    @Transactional
    void searchIncomeHistory() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        incomeHistoryRepository.saveAndFlush(incomeHistory);
        when(mockIncomeHistorySearchRepository.search("id:" + incomeHistory.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(incomeHistory), PageRequest.of(0, 1), 1));

        // Search the incomeHistory
        restIncomeHistoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + incomeHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(incomeHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].totalRevenue").value(hasItem(DEFAULT_TOTAL_REVENUE.intValue())))
            .andExpect(jsonPath("$.[*].costOfRevenue").value(hasItem(DEFAULT_COST_OF_REVENUE.intValue())))
            .andExpect(jsonPath("$.[*].grossProfit").value(hasItem(DEFAULT_GROSS_PROFIT.intValue())))
            .andExpect(jsonPath("$.[*].operatingExpense").value(hasItem(DEFAULT_OPERATING_EXPENSE.intValue())))
            .andExpect(jsonPath("$.[*].operatingIncome").value(hasItem(DEFAULT_OPERATING_INCOME.intValue())));
    }
}
