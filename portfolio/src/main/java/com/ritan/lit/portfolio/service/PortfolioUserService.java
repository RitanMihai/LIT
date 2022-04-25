package com.ritan.lit.portfolio.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.portfolio.domain.PortfolioUser;
import com.ritan.lit.portfolio.repository.PortfolioUserRepository;
import com.ritan.lit.portfolio.repository.search.PortfolioUserSearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PortfolioUser}.
 */
@Service
@Transactional
public class PortfolioUserService {

    private final Logger log = LoggerFactory.getLogger(PortfolioUserService.class);

    private final PortfolioUserRepository portfolioUserRepository;

    private final PortfolioUserSearchRepository portfolioUserSearchRepository;

    public PortfolioUserService(
        PortfolioUserRepository portfolioUserRepository,
        PortfolioUserSearchRepository portfolioUserSearchRepository
    ) {
        this.portfolioUserRepository = portfolioUserRepository;
        this.portfolioUserSearchRepository = portfolioUserSearchRepository;
    }

    /**
     * Save a portfolioUser.
     *
     * @param portfolioUser the entity to save.
     * @return the persisted entity.
     */
    public PortfolioUser save(PortfolioUser portfolioUser) {
        log.debug("Request to save PortfolioUser : {}", portfolioUser);
        PortfolioUser result = portfolioUserRepository.save(portfolioUser);
        portfolioUserSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a portfolioUser.
     *
     * @param portfolioUser the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PortfolioUser> partialUpdate(PortfolioUser portfolioUser) {
        log.debug("Request to partially update PortfolioUser : {}", portfolioUser);

        return portfolioUserRepository
            .findById(portfolioUser.getId())
            .map(existingPortfolioUser -> {
                if (portfolioUser.getUser() != null) {
                    existingPortfolioUser.setUser(portfolioUser.getUser());
                }

                return existingPortfolioUser;
            })
            .map(portfolioUserRepository::save)
            .map(savedPortfolioUser -> {
                portfolioUserSearchRepository.save(savedPortfolioUser);

                return savedPortfolioUser;
            });
    }

    /**
     * Get all the portfolioUsers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PortfolioUser> findAll() {
        log.debug("Request to get all PortfolioUsers");
        return portfolioUserRepository.findAll();
    }

    /**
     * Get one portfolioUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PortfolioUser> findOne(Long id) {
        log.debug("Request to get PortfolioUser : {}", id);
        return portfolioUserRepository.findById(id);
    }

    /**
     * Delete the portfolioUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PortfolioUser : {}", id);
        portfolioUserRepository.deleteById(id);
        portfolioUserSearchRepository.deleteById(id);
    }

    /**
     * Search for the portfolioUser corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PortfolioUser> search(String query) {
        log.debug("Request to search PortfolioUsers for query {}", query);
        return StreamSupport.stream(portfolioUserSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
