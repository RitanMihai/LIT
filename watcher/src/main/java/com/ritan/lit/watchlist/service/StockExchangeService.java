package com.ritan.lit.watchlist.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.StockExchange;
import com.ritan.lit.watchlist.repository.StockExchangeRepository;
import com.ritan.lit.watchlist.repository.search.StockExchangeSearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockExchange}.
 */
@Service
@Transactional
public class StockExchangeService {

    private final Logger log = LoggerFactory.getLogger(StockExchangeService.class);

    private final StockExchangeRepository stockExchangeRepository;

    private final StockExchangeSearchRepository stockExchangeSearchRepository;

    public StockExchangeService(
        StockExchangeRepository stockExchangeRepository,
        StockExchangeSearchRepository stockExchangeSearchRepository
    ) {
        this.stockExchangeRepository = stockExchangeRepository;
        this.stockExchangeSearchRepository = stockExchangeSearchRepository;
    }

    /**
     * Save a stockExchange.
     *
     * @param stockExchange the entity to save.
     * @return the persisted entity.
     */
    public StockExchange save(StockExchange stockExchange) {
        log.debug("Request to save StockExchange : {}", stockExchange);
        StockExchange result = stockExchangeRepository.save(stockExchange);
        stockExchangeSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a stockExchange.
     *
     * @param stockExchange the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockExchange> partialUpdate(StockExchange stockExchange) {
        log.debug("Request to partially update StockExchange : {}", stockExchange);

        return stockExchangeRepository
            .findById(stockExchange.getId())
            .map(existingStockExchange -> {
                if (stockExchange.getName() != null) {
                    existingStockExchange.setName(stockExchange.getName());
                }
                if (stockExchange.getSymbol() != null) {
                    existingStockExchange.setSymbol(stockExchange.getSymbol());
                }
                if (stockExchange.getCountry() != null) {
                    existingStockExchange.setCountry(stockExchange.getCountry());
                }

                return existingStockExchange;
            })
            .map(stockExchangeRepository::save)
            .map(savedStockExchange -> {
                stockExchangeSearchRepository.save(savedStockExchange);

                return savedStockExchange;
            });
    }

    /**
     * Get all the stockExchanges.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StockExchange> findAll() {
        log.debug("Request to get all StockExchanges");
        return stockExchangeRepository.findAll();
    }

    /**
     * Get one stockExchange by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockExchange> findOne(Long id) {
        log.debug("Request to get StockExchange : {}", id);
        return stockExchangeRepository.findById(id);
    }

    /**
     * Delete the stockExchange by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StockExchange : {}", id);
        stockExchangeRepository.deleteById(id);
        stockExchangeSearchRepository.deleteById(id);
    }

    /**
     * Search for the stockExchange corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StockExchange> search(String query) {
        log.debug("Request to search StockExchanges for query {}", query);
        return StreamSupport.stream(stockExchangeSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
