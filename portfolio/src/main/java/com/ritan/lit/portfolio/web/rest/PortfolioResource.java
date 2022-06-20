package com.ritan.lit.portfolio.web.rest;

import com.ritan.lit.portfolio.domain.Portfolio;
import com.ritan.lit.portfolio.domain.util.PortfolioData;
import com.ritan.lit.portfolio.domain.util.PortfolioOrderGroup;
import com.ritan.lit.portfolio.repository.PortfolioRepository;
import com.ritan.lit.portfolio.service.OrderService;
import com.ritan.lit.portfolio.service.PortfolioService;
import com.ritan.lit.portfolio.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ritan.lit.portfolio.domain.Portfolio}.
 */
@RestController
@RequestMapping("/api")
public class PortfolioResource {

    private final Logger log = LoggerFactory.getLogger(PortfolioResource.class);

    private static final String ENTITY_NAME = "portfolioPortfolio";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PortfolioService portfolioService;
    private final OrderService orderService;
    private final PortfolioRepository portfolioRepository;

    public PortfolioResource(PortfolioService portfolioService, OrderService orderService, PortfolioRepository portfolioRepository) {
        this.portfolioService = portfolioService;
        this.orderService = orderService;
        this.portfolioRepository = portfolioRepository;
    }

    /**
     * {@code POST  /portfolios} : Create a new portfolio.
     *
     * @param portfolio the portfolio to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new portfolio, or with status {@code 400 (Bad Request)} if the portfolio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/portfolios")
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody Portfolio portfolio) throws URISyntaxException {
        log.debug("REST request to save Portfolio : {}", portfolio);
        if (portfolio.getId() != null) {
            throw new BadRequestAlertException("A new portfolio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Portfolio result = portfolioService.save(portfolio);
        return ResponseEntity
            .created(new URI("/api/portfolios/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /portfolios/:id} : Updates an existing portfolio.
     *
     * @param id        the id of the portfolio to save.
     * @param portfolio the portfolio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolio,
     * or with status {@code 400 (Bad Request)} if the portfolio is not valid,
     * or with status {@code 500 (Internal Server Error)} if the portfolio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/portfolios/{id}")
    public ResponseEntity<Portfolio> updatePortfolio(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Portfolio portfolio
    ) throws URISyntaxException {
        log.debug("REST request to update Portfolio : {}, {}", id, portfolio);
        if (portfolio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!portfolioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Portfolio result = portfolioService.save(portfolio);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, portfolio.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /portfolios/:id} : Partial updates given fields of an existing portfolio, field will ignore if it is null
     *
     * @param id        the id of the portfolio to save.
     * @param portfolio the portfolio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolio,
     * or with status {@code 400 (Bad Request)} if the portfolio is not valid,
     * or with status {@code 404 (Not Found)} if the portfolio is not found,
     * or with status {@code 500 (Internal Server Error)} if the portfolio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/portfolios/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<Portfolio> partialUpdatePortfolio(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Portfolio portfolio
    ) throws URISyntaxException {
        log.debug("REST request to partial update Portfolio partially : {}, {}", id, portfolio);
        if (portfolio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!portfolioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Portfolio> result = portfolioService.partialUpdate(portfolio);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, portfolio.getId().toString())
        );
    }

    /**
     * {@code GET  /portfolios} : get all the portfolios.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of portfolios in body.
     */
    @GetMapping("/portfolios")
    public ResponseEntity<List<Portfolio>> getAllPortfolios(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Portfolios");
        Page<Portfolio> page = portfolioService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/portfolios/details/{user}")
    public ResponseEntity<?> getAllPortfoliosDetailsByUser(@PathVariable String user) {
        Optional<List<Portfolio>> allByUser = portfolioService.findAllByUser(user);

        List<PortfolioData> portfoliosData = new ArrayList<>();

        if (allByUser.isPresent()) {
            /* Iterate all portfolios */
            for (Portfolio portfolio : allByUser.get()) {
                PortfolioData portfolioData = new PortfolioData();
                portfolioData.setName(portfolio.getName());

                Optional<List<Object[]>> allPortfoliosWithDetails = orderService.getAllPortfoliosWithDetails(user, portfolio.getName());
                PortfolioOrderGroup portfolioOrderGroup = new PortfolioOrderGroup();
                if(allPortfoliosWithDetails.isPresent()){
                    portfolioOrderGroup.setNumberOfStocks(allPortfoliosWithDetails.get().size());

                    Double totalValuePortfolio = 0D;
                    for (Object[] orderGroup : allPortfoliosWithDetails.get()) {
                        totalValuePortfolio = Double.valueOf(orderGroup[1].toString()) + totalValuePortfolio;
                    }
                    portfolioData.setInvested(totalValuePortfolio);
                    portfolioData.setStockNumber(allPortfoliosWithDetails.get().size());
                }

                portfoliosData.add(portfolioData);
            }

            portfoliosData.sort(PortfolioData::compareTo);
        }

        return ResponseEntity.ok(portfoliosData);
    }

    /**
     * {@code GET  /portfolios/:id} : get the "id" portfolio.
     *
     * @param id the id of the portfolio to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the portfolio, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/portfolios/{id}")
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable Long id) {
        log.debug("REST request to get Portfolio : {}", id);
        Optional<Portfolio> portfolio = portfolioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(portfolio);
    }

    @GetMapping("/portfolios/user/{user}")
    public ResponseEntity<List<Portfolio>> getPortfoliosByUSer(@PathVariable String user) {
        Optional<List<Portfolio>> portfolios = portfolioService.findAllByUser(user);

        return ResponseUtil.wrapOrNotFound(portfolios);
    }

    /**
     * {@code DELETE  /portfolios/:id} : delete the "id" portfolio.
     *
     * @param id the id of the portfolio to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/portfolios/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        log.debug("REST request to delete Portfolio : {}", id);
        portfolioService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/portfolios?query=:query} : search for the portfolio corresponding
     * to the query.
     *
     * @param query    the query of the portfolio search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/portfolios")
    public ResponseEntity<List<Portfolio>> searchPortfolios(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Portfolios for query {}", query);
        Page<Portfolio> page = portfolioService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
