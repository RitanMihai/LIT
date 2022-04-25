package com.ritan.lit.watchlist.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.Currency;
import com.ritan.lit.watchlist.repository.CurrencyRepository;
import com.ritan.lit.watchlist.repository.search.CurrencySearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Currency}.
 */
@Service
@Transactional
public class CurrencyService {

    private final Logger log = LoggerFactory.getLogger(CurrencyService.class);

    private final CurrencyRepository currencyRepository;

    private final CurrencySearchRepository currencySearchRepository;

    public CurrencyService(CurrencyRepository currencyRepository, CurrencySearchRepository currencySearchRepository) {
        this.currencyRepository = currencyRepository;
        this.currencySearchRepository = currencySearchRepository;
    }

    /**
     * Save a currency.
     *
     * @param currency the entity to save.
     * @return the persisted entity.
     */
    public Currency save(Currency currency) {
        log.debug("Request to save Currency : {}", currency);
        Currency result = currencyRepository.save(currency);
        currencySearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a currency.
     *
     * @param currency the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Currency> partialUpdate(Currency currency) {
        log.debug("Request to partially update Currency : {}", currency);

        return currencyRepository
            .findById(currency.getId())
            .map(existingCurrency -> {
                if (currency.getCode() != null) {
                    existingCurrency.setCode(currency.getCode());
                }
                if (currency.getName() != null) {
                    existingCurrency.setName(currency.getName());
                }
                if (currency.getCurrencySymbol() != null) {
                    existingCurrency.setCurrencySymbol(currency.getCurrencySymbol());
                }

                return existingCurrency;
            })
            .map(currencyRepository::save)
            .map(savedCurrency -> {
                currencySearchRepository.save(savedCurrency);

                return savedCurrency;
            });
    }

    /**
     * Get all the currencies.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Currency> findAll() {
        log.debug("Request to get all Currencies");
        return currencyRepository.findAll();
    }

    /**
     * Get one currency by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Currency> findOne(Long id) {
        log.debug("Request to get Currency : {}", id);
        return currencyRepository.findById(id);
    }

    /**
     * Delete the currency by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Currency : {}", id);
        currencyRepository.deleteById(id);
        currencySearchRepository.deleteById(id);
    }

    /**
     * Search for the currency corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Currency> search(String query) {
        log.debug("Request to search Currencies for query {}", query);
        return StreamSupport.stream(currencySearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
