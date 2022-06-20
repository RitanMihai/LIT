package com.ritan.lit.portfolio.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.portfolio.domain.Portfolio;
import com.ritan.lit.portfolio.repository.PortfolioRepository;
import com.ritan.lit.portfolio.repository.search.PortfolioSearchRepository;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Portfolio}.
 */
@Service
@Transactional
public class PortfolioService {

    private final Logger log = LoggerFactory.getLogger(PortfolioService.class);

    private final PortfolioRepository portfolioRepository;

    private final PortfolioSearchRepository portfolioSearchRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, PortfolioSearchRepository portfolioSearchRepository) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioSearchRepository = portfolioSearchRepository;
    }

    /**
     * Save a portfolio.
     *
     * @param portfolio the entity to save.
     * @return the persisted entity.
     */
    public Portfolio save(Portfolio portfolio) {
        log.debug("Request to save Portfolio : {}", portfolio);
        Portfolio result = portfolioRepository.save(portfolio);
        portfolioSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a portfolio.
     *
     * @param portfolio the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Portfolio> partialUpdate(Portfolio portfolio) {
        log.debug("Request to partially update Portfolio : {}", portfolio);

        return portfolioRepository
            .findById(portfolio.getId())
            .map(existingPortfolio -> {
                if (portfolio.getName() != null) {
                    existingPortfolio.setName(portfolio.getName());
                }
                if (portfolio.getValue() != null) {
                    existingPortfolio.setValue(portfolio.getValue());
                }
                if (portfolio.getImage() != null) {
                    existingPortfolio.setImage(portfolio.getImage());
                }
                if (portfolio.getImageContentType() != null) {
                    existingPortfolio.setImageContentType(portfolio.getImageContentType());
                }
                if (portfolio.getUnrealisedValue() != null) {
                    existingPortfolio.setUnrealisedValue(portfolio.getUnrealisedValue());
                }
                if (portfolio.getProfitOrLoss() != null) {
                    existingPortfolio.setProfitOrLoss(portfolio.getProfitOrLoss());
                }

                return existingPortfolio;
            })
            .map(portfolioRepository::save)
            .map(savedPortfolio -> {
                portfolioSearchRepository.save(savedPortfolio);

                return savedPortfolio;
            });
    }

    /**
     * Get all the portfolios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Portfolio> findAll(Pageable pageable) {
        log.debug("Request to get all Portfolios");
        return portfolioRepository.findAll(pageable);
    }

    /**
     * Get one portfolio by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Portfolio> findOne(Long id) {
        log.debug("Request to get Portfolio : {}", id);
        return portfolioRepository.findById(id);
    }

    /**
     * Delete the portfolio by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Portfolio : {}", id);
        portfolioRepository.deleteById(id);
        portfolioSearchRepository.deleteById(id);
    }

    /**
     * Search for the portfolio corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Portfolio> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Portfolios for query {}", query);
        return portfolioSearchRepository.search(query, pageable);
    }

    public Optional<List<Portfolio>> findAllByUser(String user) {
        return portfolioRepository.getAllByPortfolioUserUser(user);
    }
}
