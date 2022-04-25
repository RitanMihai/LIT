package com.ritan.lit.portfolio.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.portfolio.domain.PortfolioCurrency;
import com.ritan.lit.portfolio.repository.PortfolioCurrencyRepository;
import com.ritan.lit.portfolio.repository.search.PortfolioCurrencySearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PortfolioCurrency}.
 */
@Service
@Transactional
public class PortfolioCurrencyService {

    private final Logger log = LoggerFactory.getLogger(PortfolioCurrencyService.class);

    private final PortfolioCurrencyRepository portfolioCurrencyRepository;

    private final PortfolioCurrencySearchRepository portfolioCurrencySearchRepository;

    public PortfolioCurrencyService(
        PortfolioCurrencyRepository portfolioCurrencyRepository,
        PortfolioCurrencySearchRepository portfolioCurrencySearchRepository
    ) {
        this.portfolioCurrencyRepository = portfolioCurrencyRepository;
        this.portfolioCurrencySearchRepository = portfolioCurrencySearchRepository;
    }

    /**
     * Save a portfolioCurrency.
     *
     * @param portfolioCurrency the entity to save.
     * @return the persisted entity.
     */
    public PortfolioCurrency save(PortfolioCurrency portfolioCurrency) {
        log.debug("Request to save PortfolioCurrency : {}", portfolioCurrency);
        PortfolioCurrency result = portfolioCurrencyRepository.save(portfolioCurrency);
        portfolioCurrencySearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a portfolioCurrency.
     *
     * @param portfolioCurrency the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PortfolioCurrency> partialUpdate(PortfolioCurrency portfolioCurrency) {
        log.debug("Request to partially update PortfolioCurrency : {}", portfolioCurrency);

        return portfolioCurrencyRepository
            .findById(portfolioCurrency.getId())
            .map(existingPortfolioCurrency -> {
                if (portfolioCurrency.getCode() != null) {
                    existingPortfolioCurrency.setCode(portfolioCurrency.getCode());
                }
                if (portfolioCurrency.getName() != null) {
                    existingPortfolioCurrency.setName(portfolioCurrency.getName());
                }
                if (portfolioCurrency.getCurrencySymbol() != null) {
                    existingPortfolioCurrency.setCurrencySymbol(portfolioCurrency.getCurrencySymbol());
                }

                return existingPortfolioCurrency;
            })
            .map(portfolioCurrencyRepository::save)
            .map(savedPortfolioCurrency -> {
                portfolioCurrencySearchRepository.save(savedPortfolioCurrency);

                return savedPortfolioCurrency;
            });
    }

    /**
     * Get all the portfolioCurrencies.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PortfolioCurrency> findAll() {
        log.debug("Request to get all PortfolioCurrencies");
        return portfolioCurrencyRepository.findAll();
    }

    /**
     * Get one portfolioCurrency by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PortfolioCurrency> findOne(Long id) {
        log.debug("Request to get PortfolioCurrency : {}", id);
        return portfolioCurrencyRepository.findById(id);
    }

    /**
     * Delete the portfolioCurrency by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PortfolioCurrency : {}", id);
        portfolioCurrencyRepository.deleteById(id);
        portfolioCurrencySearchRepository.deleteById(id);
    }

    /**
     * Search for the portfolioCurrency corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PortfolioCurrency> search(String query) {
        log.debug("Request to search PortfolioCurrencies for query {}", query);
        return StreamSupport.stream(portfolioCurrencySearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
