package com.ritan.lit.watchlist.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.StockSplitHistory;
import com.ritan.lit.watchlist.repository.StockSplitHistoryRepository;
import com.ritan.lit.watchlist.repository.search.StockSplitHistorySearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockSplitHistory}.
 */
@Service
@Transactional
public class StockSplitHistoryService {

    private final Logger log = LoggerFactory.getLogger(StockSplitHistoryService.class);

    private final StockSplitHistoryRepository stockSplitHistoryRepository;

    private final StockSplitHistorySearchRepository stockSplitHistorySearchRepository;

    public StockSplitHistoryService(
        StockSplitHistoryRepository stockSplitHistoryRepository,
        StockSplitHistorySearchRepository stockSplitHistorySearchRepository
    ) {
        this.stockSplitHistoryRepository = stockSplitHistoryRepository;
        this.stockSplitHistorySearchRepository = stockSplitHistorySearchRepository;
    }

    /**
     * Save a stockSplitHistory.
     *
     * @param stockSplitHistory the entity to save.
     * @return the persisted entity.
     */
    public StockSplitHistory save(StockSplitHistory stockSplitHistory) {
        log.debug("Request to save StockSplitHistory : {}", stockSplitHistory);
        StockSplitHistory result = stockSplitHistoryRepository.save(stockSplitHistory);
        stockSplitHistorySearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a stockSplitHistory.
     *
     * @param stockSplitHistory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockSplitHistory> partialUpdate(StockSplitHistory stockSplitHistory) {
        log.debug("Request to partially update StockSplitHistory : {}", stockSplitHistory);

        return stockSplitHistoryRepository
            .findById(stockSplitHistory.getId())
            .map(existingStockSplitHistory -> {
                if (stockSplitHistory.getDate() != null) {
                    existingStockSplitHistory.setDate(stockSplitHistory.getDate());
                }
                if (stockSplitHistory.getRatio() != null) {
                    existingStockSplitHistory.setRatio(stockSplitHistory.getRatio());
                }

                return existingStockSplitHistory;
            })
            .map(stockSplitHistoryRepository::save)
            .map(savedStockSplitHistory -> {
                stockSplitHistorySearchRepository.save(savedStockSplitHistory);

                return savedStockSplitHistory;
            });
    }

    /**
     * Get all the stockSplitHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StockSplitHistory> findAll(Pageable pageable) {
        log.debug("Request to get all StockSplitHistories");
        return stockSplitHistoryRepository.findAll(pageable);
    }

    /**
     * Get one stockSplitHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockSplitHistory> findOne(Long id) {
        log.debug("Request to get StockSplitHistory : {}", id);
        return stockSplitHistoryRepository.findById(id);
    }

    /**
     * Delete the stockSplitHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StockSplitHistory : {}", id);
        stockSplitHistoryRepository.deleteById(id);
        stockSplitHistorySearchRepository.deleteById(id);
    }

    /**
     * Search for the stockSplitHistory corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StockSplitHistory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of StockSplitHistories for query {}", query);
        return stockSplitHistorySearchRepository.search(query, pageable);
    }
}
