package com.ritan.lit.portfolio.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.portfolio.domain.StockInfo;
import com.ritan.lit.portfolio.repository.StockInfoRepository;
import com.ritan.lit.portfolio.repository.search.StockInfoSearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockInfo}.
 */
@Service
@Transactional
public class StockInfoService {

    private final Logger log = LoggerFactory.getLogger(StockInfoService.class);

    private final StockInfoRepository stockInfoRepository;

    private final StockInfoSearchRepository stockInfoSearchRepository;

    public StockInfoService(StockInfoRepository stockInfoRepository, StockInfoSearchRepository stockInfoSearchRepository) {
        this.stockInfoRepository = stockInfoRepository;
        this.stockInfoSearchRepository = stockInfoSearchRepository;
    }

    /**
     * Save a stockInfo.
     *
     * @param stockInfo the entity to save.
     * @return the persisted entity.
     */
    public StockInfo save(StockInfo stockInfo) {
        log.debug("Request to save StockInfo : {}", stockInfo);
        StockInfo result = stockInfoRepository.save(stockInfo);
        stockInfoSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a stockInfo.
     *
     * @param stockInfo the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockInfo> partialUpdate(StockInfo stockInfo) {
        log.debug("Request to partially update StockInfo : {}", stockInfo);

        return stockInfoRepository
            .findById(stockInfo.getId())
            .map(existingStockInfo -> {
                if (stockInfo.getTicker() != null) {
                    existingStockInfo.setTicker(stockInfo.getTicker());
                }
                if (stockInfo.getName() != null) {
                    existingStockInfo.setName(stockInfo.getName());
                }
                if (stockInfo.getImage() != null) {
                    existingStockInfo.setImage(stockInfo.getImage());
                }
                if (stockInfo.getImageContentType() != null) {
                    existingStockInfo.setImageContentType(stockInfo.getImageContentType());
                }
                if (stockInfo.getIsin() != null) {
                    existingStockInfo.setIsin(stockInfo.getIsin());
                }
                if (stockInfo.getDividendYield() != null) {
                    existingStockInfo.setDividendYield(stockInfo.getDividendYield());
                }
                if (stockInfo.getSector() != null) {
                    existingStockInfo.setSector(stockInfo.getSector());
                }
                if (stockInfo.getIndustry() != null) {
                    existingStockInfo.setIndustry(stockInfo.getIndustry());
                }

                return existingStockInfo;
            })
            .map(stockInfoRepository::save)
            .map(savedStockInfo -> {
                stockInfoSearchRepository.save(savedStockInfo);

                return savedStockInfo;
            });
    }

    /**
     * Get all the stockInfos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StockInfo> findAll() {
        log.debug("Request to get all StockInfos");
        return stockInfoRepository.findAll();
    }

    /**
     * Get one stockInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockInfo> findOne(Long id) {
        log.debug("Request to get StockInfo : {}", id);
        return stockInfoRepository.findById(id);
    }

    /**
     * Delete the stockInfo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StockInfo : {}", id);
        stockInfoRepository.deleteById(id);
        stockInfoSearchRepository.deleteById(id);
    }

    /**
     * Search for the stockInfo corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StockInfo> search(String query) {
        log.debug("Request to search StockInfos for query {}", query);
        return StreamSupport.stream(stockInfoSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
