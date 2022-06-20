package com.ritan.lit.watchlist.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.PriceHistory;
import com.ritan.lit.watchlist.domain.Stock;
import com.ritan.lit.watchlist.repository.PriceHistoryRepository;
import com.ritan.lit.watchlist.repository.search.PriceHistorySearchRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PriceHistory}.
 */
@Service
@Transactional
public class PriceHistoryService {

    private final Logger log = LoggerFactory.getLogger(PriceHistoryService.class);

    private final PriceHistoryRepository priceHistoryRepository;

    private final PriceHistorySearchRepository priceHistorySearchRepository;

    public PriceHistoryService(PriceHistoryRepository priceHistoryRepository, PriceHistorySearchRepository priceHistorySearchRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
        this.priceHistorySearchRepository = priceHistorySearchRepository;
    }

    /**
     * Save a priceHistory.
     *
     * @param priceHistory the entity to save.
     * @return the persisted entity.
     */
    public PriceHistory save(PriceHistory priceHistory) {
        log.debug("Request to save PriceHistory : {}", priceHistory);
        PriceHistory result = priceHistoryRepository.save(priceHistory);
        priceHistorySearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a priceHistory.
     *
     * @param priceHistory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PriceHistory> partialUpdate(PriceHistory priceHistory) {
        log.debug("Request to partially update PriceHistory : {}", priceHistory);

        return priceHistoryRepository
            .findById(priceHistory.getId())
            .map(existingPriceHistory -> {
                if (priceHistory.getDate() != null) {
                    existingPriceHistory.setDate(priceHistory.getDate());
                }
                if (priceHistory.getOpen() != null) {
                    existingPriceHistory.setOpen(priceHistory.getOpen());
                }
                if (priceHistory.getHigh() != null) {
                    existingPriceHistory.setHigh(priceHistory.getHigh());
                }
                if (priceHistory.getLow() != null) {
                    existingPriceHistory.setLow(priceHistory.getLow());
                }
                if (priceHistory.getClose() != null) {
                    existingPriceHistory.setClose(priceHistory.getClose());
                }
                if (priceHistory.getAdjClose() != null) {
                    existingPriceHistory.setAdjClose(priceHistory.getAdjClose());
                }
                if (priceHistory.getVolume() != null) {
                    existingPriceHistory.setVolume(priceHistory.getVolume());
                }

                return existingPriceHistory;
            })
            .map(priceHistoryRepository::save)
            .map(savedPriceHistory -> {
                priceHistorySearchRepository.save(savedPriceHistory);

                return savedPriceHistory;
            });
    }

    /**
     * Get all the priceHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PriceHistory> findAll(Pageable pageable) {
        log.debug("Request to get all PriceHistories");
        return priceHistoryRepository.findAll(pageable);
    }

    /**
     * Get one priceHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PriceHistory> findOne(Long id) {
        log.debug("Request to get PriceHistory : {}", id);
        return priceHistoryRepository.findById(id);
    }

    /**
     * Delete the priceHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PriceHistory : {}", id);
        priceHistoryRepository.deleteById(id);
        priceHistorySearchRepository.deleteById(id);
    }

    /**
     * Search for the priceHistory corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PriceHistory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PriceHistories for query {}", query);
        return priceHistorySearchRepository.search(query, pageable);
    }

    public List<PriceHistory> findAllByStock(Stock stock) {
        List<PriceHistory> allByStock = priceHistoryRepository.findAllByStockOrderByDate(stock);
        //allByStock.forEach(priceHistory -> priceHistory.setStock(null));
        return allByStock;
    }

    public List<PriceHistory> findAllByStockAndDate(Stock stock, LocalDate date) {
        List<PriceHistory> allByStock = priceHistoryRepository.findAllByStockAndDateAfterOrderByDate(stock, date);
        //allByStock.forEach(priceHistory -> priceHistory.setStock(null));
        return allByStock;
    }
}
