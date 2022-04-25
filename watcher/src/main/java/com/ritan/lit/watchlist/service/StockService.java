package com.ritan.lit.watchlist.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.Stock;
import com.ritan.lit.watchlist.repository.StockRepository;
import com.ritan.lit.watchlist.repository.search.StockSearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Stock}.
 */
@Service
@Transactional
public class StockService {

    private final Logger log = LoggerFactory.getLogger(StockService.class);

    private final StockRepository stockRepository;

    private final StockSearchRepository stockSearchRepository;

    public StockService(StockRepository stockRepository, StockSearchRepository stockSearchRepository) {
        this.stockRepository = stockRepository;
        this.stockSearchRepository = stockSearchRepository;
    }

    /**
     * Save a stock.
     *
     * @param stock the entity to save.
     * @return the persisted entity.
     */
    public Stock save(Stock stock) {
        log.debug("Request to save Stock : {}", stock);
        Stock result = stockRepository.save(stock);
        stockSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a stock.
     *
     * @param stock the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Stock> partialUpdate(Stock stock) {
        log.debug("Request to partially update Stock : {}", stock);

        return stockRepository
            .findById(stock.getId())
            .map(existingStock -> {
                if (stock.getTicker() != null) {
                    existingStock.setTicker(stock.getTicker());
                }
                if (stock.getName() != null) {
                    existingStock.setName(stock.getName());
                }
                if (stock.getImage() != null) {
                    existingStock.setImage(stock.getImage());
                }
                if (stock.getImageContentType() != null) {
                    existingStock.setImageContentType(stock.getImageContentType());
                }
                if (stock.getMarketCap() != null) {
                    existingStock.setMarketCap(stock.getMarketCap());
                }
                if (stock.getVolume() != null) {
                    existingStock.setVolume(stock.getVolume());
                }
                if (stock.getPeRation() != null) {
                    existingStock.setPeRation(stock.getPeRation());
                }
                if (stock.getIpoDate() != null) {
                    existingStock.setIpoDate(stock.getIpoDate());
                }
                if (stock.getIsin() != null) {
                    existingStock.setIsin(stock.getIsin());
                }
                if (stock.getIsDelisted() != null) {
                    existingStock.setIsDelisted(stock.getIsDelisted());
                }
                if (stock.getHasDividend() != null) {
                    existingStock.setHasDividend(stock.getHasDividend());
                }
                if (stock.getType() != null) {
                    existingStock.setType(stock.getType());
                }
                if (stock.getDividendYield() != null) {
                    existingStock.setDividendYield(stock.getDividendYield());
                }

                return existingStock;
            })
            .map(stockRepository::save)
            .map(savedStock -> {
                stockSearchRepository.save(savedStock);

                return savedStock;
            });
    }

    /**
     * Get all the stocks.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Stock> findAll() {
        log.debug("Request to get all Stocks");
        return stockRepository.findAll();
    }

    /**
     * Get one stock by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Stock> findOne(Long id) {
        log.debug("Request to get Stock : {}", id);
        return stockRepository.findById(id);
    }

    /**
     * Delete the stock by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Stock : {}", id);
        stockRepository.deleteById(id);
        stockSearchRepository.deleteById(id);
    }

    /**
     * Search for the stock corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Stock> search(String query) {
        log.debug("Request to search Stocks for query {}", query);
        return StreamSupport.stream(stockSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
