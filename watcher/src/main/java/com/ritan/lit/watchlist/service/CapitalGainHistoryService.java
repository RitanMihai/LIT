package com.ritan.lit.watchlist.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.CapitalGainHistory;
import com.ritan.lit.watchlist.repository.CapitalGainHistoryRepository;
import com.ritan.lit.watchlist.repository.search.CapitalGainHistorySearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CapitalGainHistory}.
 */
@Service
@Transactional
public class CapitalGainHistoryService {

    private final Logger log = LoggerFactory.getLogger(CapitalGainHistoryService.class);

    private final CapitalGainHistoryRepository capitalGainHistoryRepository;

    private final CapitalGainHistorySearchRepository capitalGainHistorySearchRepository;

    public CapitalGainHistoryService(
        CapitalGainHistoryRepository capitalGainHistoryRepository,
        CapitalGainHistorySearchRepository capitalGainHistorySearchRepository
    ) {
        this.capitalGainHistoryRepository = capitalGainHistoryRepository;
        this.capitalGainHistorySearchRepository = capitalGainHistorySearchRepository;
    }

    /**
     * Save a capitalGainHistory.
     *
     * @param capitalGainHistory the entity to save.
     * @return the persisted entity.
     */
    public CapitalGainHistory save(CapitalGainHistory capitalGainHistory) {
        log.debug("Request to save CapitalGainHistory : {}", capitalGainHistory);
        CapitalGainHistory result = capitalGainHistoryRepository.save(capitalGainHistory);
        capitalGainHistorySearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a capitalGainHistory.
     *
     * @param capitalGainHistory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CapitalGainHistory> partialUpdate(CapitalGainHistory capitalGainHistory) {
        log.debug("Request to partially update CapitalGainHistory : {}", capitalGainHistory);

        return capitalGainHistoryRepository
            .findById(capitalGainHistory.getId())
            .map(existingCapitalGainHistory -> {
                if (capitalGainHistory.getDate() != null) {
                    existingCapitalGainHistory.setDate(capitalGainHistory.getDate());
                }
                if (capitalGainHistory.getCapitalGain() != null) {
                    existingCapitalGainHistory.setCapitalGain(capitalGainHistory.getCapitalGain());
                }

                return existingCapitalGainHistory;
            })
            .map(capitalGainHistoryRepository::save)
            .map(savedCapitalGainHistory -> {
                capitalGainHistorySearchRepository.save(savedCapitalGainHistory);

                return savedCapitalGainHistory;
            });
    }

    /**
     * Get all the capitalGainHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CapitalGainHistory> findAll(Pageable pageable) {
        log.debug("Request to get all CapitalGainHistories");
        return capitalGainHistoryRepository.findAll(pageable);
    }

    /**
     * Get one capitalGainHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CapitalGainHistory> findOne(Long id) {
        log.debug("Request to get CapitalGainHistory : {}", id);
        return capitalGainHistoryRepository.findById(id);
    }

    /**
     * Delete the capitalGainHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete CapitalGainHistory : {}", id);
        capitalGainHistoryRepository.deleteById(id);
        capitalGainHistorySearchRepository.deleteById(id);
    }

    /**
     * Search for the capitalGainHistory corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CapitalGainHistory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CapitalGainHistories for query {}", query);
        return capitalGainHistorySearchRepository.search(query, pageable);
    }
}
