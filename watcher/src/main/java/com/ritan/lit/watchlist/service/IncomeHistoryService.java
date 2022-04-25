package com.ritan.lit.watchlist.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.IncomeHistory;
import com.ritan.lit.watchlist.repository.IncomeHistoryRepository;
import com.ritan.lit.watchlist.repository.search.IncomeHistorySearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link IncomeHistory}.
 */
@Service
@Transactional
public class IncomeHistoryService {

    private final Logger log = LoggerFactory.getLogger(IncomeHistoryService.class);

    private final IncomeHistoryRepository incomeHistoryRepository;

    private final IncomeHistorySearchRepository incomeHistorySearchRepository;

    public IncomeHistoryService(
        IncomeHistoryRepository incomeHistoryRepository,
        IncomeHistorySearchRepository incomeHistorySearchRepository
    ) {
        this.incomeHistoryRepository = incomeHistoryRepository;
        this.incomeHistorySearchRepository = incomeHistorySearchRepository;
    }

    /**
     * Save a incomeHistory.
     *
     * @param incomeHistory the entity to save.
     * @return the persisted entity.
     */
    public IncomeHistory save(IncomeHistory incomeHistory) {
        log.debug("Request to save IncomeHistory : {}", incomeHistory);
        IncomeHistory result = incomeHistoryRepository.save(incomeHistory);
        incomeHistorySearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a incomeHistory.
     *
     * @param incomeHistory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<IncomeHistory> partialUpdate(IncomeHistory incomeHistory) {
        log.debug("Request to partially update IncomeHistory : {}", incomeHistory);

        return incomeHistoryRepository
            .findById(incomeHistory.getId())
            .map(existingIncomeHistory -> {
                if (incomeHistory.getDate() != null) {
                    existingIncomeHistory.setDate(incomeHistory.getDate());
                }
                if (incomeHistory.getTotalRevenue() != null) {
                    existingIncomeHistory.setTotalRevenue(incomeHistory.getTotalRevenue());
                }
                if (incomeHistory.getCostOfRevenue() != null) {
                    existingIncomeHistory.setCostOfRevenue(incomeHistory.getCostOfRevenue());
                }
                if (incomeHistory.getGrossProfit() != null) {
                    existingIncomeHistory.setGrossProfit(incomeHistory.getGrossProfit());
                }
                if (incomeHistory.getOperatingExpense() != null) {
                    existingIncomeHistory.setOperatingExpense(incomeHistory.getOperatingExpense());
                }
                if (incomeHistory.getOperatingIncome() != null) {
                    existingIncomeHistory.setOperatingIncome(incomeHistory.getOperatingIncome());
                }

                return existingIncomeHistory;
            })
            .map(incomeHistoryRepository::save)
            .map(savedIncomeHistory -> {
                incomeHistorySearchRepository.save(savedIncomeHistory);

                return savedIncomeHistory;
            });
    }

    /**
     * Get all the incomeHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<IncomeHistory> findAll(Pageable pageable) {
        log.debug("Request to get all IncomeHistories");
        return incomeHistoryRepository.findAll(pageable);
    }

    /**
     * Get one incomeHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<IncomeHistory> findOne(Long id) {
        log.debug("Request to get IncomeHistory : {}", id);
        return incomeHistoryRepository.findById(id);
    }

    /**
     * Delete the incomeHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete IncomeHistory : {}", id);
        incomeHistoryRepository.deleteById(id);
        incomeHistorySearchRepository.deleteById(id);
    }

    /**
     * Search for the incomeHistory corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<IncomeHistory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of IncomeHistories for query {}", query);
        return incomeHistorySearchRepository.search(query, pageable);
    }
}
