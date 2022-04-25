package com.ritan.lit.portfolio.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.portfolio.domain.Dividend;
import com.ritan.lit.portfolio.repository.DividendRepository;
import com.ritan.lit.portfolio.repository.search.DividendSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Dividend}.
 */
@Service
@Transactional
public class DividendService {

    private final Logger log = LoggerFactory.getLogger(DividendService.class);

    private final DividendRepository dividendRepository;

    private final DividendSearchRepository dividendSearchRepository;

    public DividendService(DividendRepository dividendRepository, DividendSearchRepository dividendSearchRepository) {
        this.dividendRepository = dividendRepository;
        this.dividendSearchRepository = dividendSearchRepository;
    }

    /**
     * Save a dividend.
     *
     * @param dividend the entity to save.
     * @return the persisted entity.
     */
    public Dividend save(Dividend dividend) {
        log.debug("Request to save Dividend : {}", dividend);
        Dividend result = dividendRepository.save(dividend);
        dividendSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a dividend.
     *
     * @param dividend the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Dividend> partialUpdate(Dividend dividend) {
        log.debug("Request to partially update Dividend : {}", dividend);

        return dividendRepository
            .findById(dividend.getId())
            .map(existingDividend -> {
                if (dividend.getDateRecived() != null) {
                    existingDividend.setDateRecived(dividend.getDateRecived());
                }
                if (dividend.getTaxRate() != null) {
                    existingDividend.setTaxRate(dividend.getTaxRate());
                }
                if (dividend.getTotalRecived() != null) {
                    existingDividend.setTotalRecived(dividend.getTotalRecived());
                }
                if (dividend.getDividendType() != null) {
                    existingDividend.setDividendType(dividend.getDividendType());
                }

                return existingDividend;
            })
            .map(dividendRepository::save)
            .map(savedDividend -> {
                dividendSearchRepository.save(savedDividend);

                return savedDividend;
            });
    }

    /**
     * Get all the dividends.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Dividend> findAll(Pageable pageable) {
        log.debug("Request to get all Dividends");
        return dividendRepository.findAll(pageable);
    }

    /**
     * Get one dividend by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Dividend> findOne(Long id) {
        log.debug("Request to get Dividend : {}", id);
        return dividendRepository.findById(id);
    }

    /**
     * Delete the dividend by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Dividend : {}", id);
        dividendRepository.deleteById(id);
        dividendSearchRepository.deleteById(id);
    }

    /**
     * Search for the dividend corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Dividend> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Dividends for query {}", query);
        return dividendSearchRepository.search(query, pageable);
    }
}
