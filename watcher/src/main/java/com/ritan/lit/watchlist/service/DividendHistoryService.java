package com.ritan.lit.watchlist.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.DividendHistory;
import com.ritan.lit.watchlist.repository.DividendHistoryRepository;
import com.ritan.lit.watchlist.repository.search.DividendHistorySearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link DividendHistory}.
 */
@Service
@Transactional
public class DividendHistoryService {

    private final Logger log = LoggerFactory.getLogger(DividendHistoryService.class);

    private final DividendHistoryRepository dividendHistoryRepository;

    private final DividendHistorySearchRepository dividendHistorySearchRepository;

    public DividendHistoryService(
        DividendHistoryRepository dividendHistoryRepository,
        DividendHistorySearchRepository dividendHistorySearchRepository
    ) {
        this.dividendHistoryRepository = dividendHistoryRepository;
        this.dividendHistorySearchRepository = dividendHistorySearchRepository;
    }

    /**
     * Save a dividendHistory.
     *
     * @param dividendHistory the entity to save.
     * @return the persisted entity.
     */
    public DividendHistory save(DividendHistory dividendHistory) {
        log.debug("Request to save DividendHistory : {}", dividendHistory);
        DividendHistory result = dividendHistoryRepository.save(dividendHistory);
        dividendHistorySearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a dividendHistory.
     *
     * @param dividendHistory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DividendHistory> partialUpdate(DividendHistory dividendHistory) {
        log.debug("Request to partially update DividendHistory : {}", dividendHistory);

        return dividendHistoryRepository
            .findById(dividendHistory.getId())
            .map(existingDividendHistory -> {
                if (dividendHistory.getDate() != null) {
                    existingDividendHistory.setDate(dividendHistory.getDate());
                }
                if (dividendHistory.getDividend() != null) {
                    existingDividendHistory.setDividend(dividendHistory.getDividend());
                }

                return existingDividendHistory;
            })
            .map(dividendHistoryRepository::save)
            .map(savedDividendHistory -> {
                dividendHistorySearchRepository.save(savedDividendHistory);

                return savedDividendHistory;
            });
    }

    /**
     * Get all the dividendHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DividendHistory> findAll(Pageable pageable) {
        log.debug("Request to get all DividendHistories");
        return dividendHistoryRepository.findAll(pageable);
    }

    /**
     * Get one dividendHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DividendHistory> findOne(Long id) {
        log.debug("Request to get DividendHistory : {}", id);
        return dividendHistoryRepository.findById(id);
    }

    /**
     * Delete the dividendHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete DividendHistory : {}", id);
        dividendHistoryRepository.deleteById(id);
        dividendHistorySearchRepository.deleteById(id);
    }

    /**
     * Search for the dividendHistory corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DividendHistory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of DividendHistories for query {}", query);
        return dividendHistorySearchRepository.search(query, pageable);
    }
}
