package com.ritan.lit.portfolio.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.portfolio.domain.PortfolioCurrency;
import com.ritan.lit.portfolio.repository.PortfolioCurrencyRepository;
import com.ritan.lit.portfolio.service.PortfolioCurrencyService;
import com.ritan.lit.portfolio.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ritan.lit.portfolio.domain.PortfolioCurrency}.
 */
@RestController
@RequestMapping("/api")
public class PortfolioCurrencyResource {

    private final Logger log = LoggerFactory.getLogger(PortfolioCurrencyResource.class);

    private static final String ENTITY_NAME = "portfolioPortfolioCurrency";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PortfolioCurrencyService portfolioCurrencyService;

    private final PortfolioCurrencyRepository portfolioCurrencyRepository;

    public PortfolioCurrencyResource(
        PortfolioCurrencyService portfolioCurrencyService,
        PortfolioCurrencyRepository portfolioCurrencyRepository
    ) {
        this.portfolioCurrencyService = portfolioCurrencyService;
        this.portfolioCurrencyRepository = portfolioCurrencyRepository;
    }

    /**
     * {@code POST  /portfolio-currencies} : Create a new portfolioCurrency.
     *
     * @param portfolioCurrency the portfolioCurrency to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new portfolioCurrency, or with status {@code 400 (Bad Request)} if the portfolioCurrency has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/portfolio-currencies")
    public ResponseEntity<PortfolioCurrency> createPortfolioCurrency(@RequestBody PortfolioCurrency portfolioCurrency)
        throws URISyntaxException {
        log.debug("REST request to save PortfolioCurrency : {}", portfolioCurrency);
        if (portfolioCurrency.getId() != null) {
            throw new BadRequestAlertException("A new portfolioCurrency cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PortfolioCurrency result = portfolioCurrencyService.save(portfolioCurrency);
        return ResponseEntity
            .created(new URI("/api/portfolio-currencies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /portfolio-currencies/:id} : Updates an existing portfolioCurrency.
     *
     * @param id the id of the portfolioCurrency to save.
     * @param portfolioCurrency the portfolioCurrency to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolioCurrency,
     * or with status {@code 400 (Bad Request)} if the portfolioCurrency is not valid,
     * or with status {@code 500 (Internal Server Error)} if the portfolioCurrency couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/portfolio-currencies/{id}")
    public ResponseEntity<PortfolioCurrency> updatePortfolioCurrency(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PortfolioCurrency portfolioCurrency
    ) throws URISyntaxException {
        log.debug("REST request to update PortfolioCurrency : {}, {}", id, portfolioCurrency);
        if (portfolioCurrency.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolioCurrency.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!portfolioCurrencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PortfolioCurrency result = portfolioCurrencyService.save(portfolioCurrency);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, portfolioCurrency.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /portfolio-currencies/:id} : Partial updates given fields of an existing portfolioCurrency, field will ignore if it is null
     *
     * @param id the id of the portfolioCurrency to save.
     * @param portfolioCurrency the portfolioCurrency to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolioCurrency,
     * or with status {@code 400 (Bad Request)} if the portfolioCurrency is not valid,
     * or with status {@code 404 (Not Found)} if the portfolioCurrency is not found,
     * or with status {@code 500 (Internal Server Error)} if the portfolioCurrency couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/portfolio-currencies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PortfolioCurrency> partialUpdatePortfolioCurrency(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PortfolioCurrency portfolioCurrency
    ) throws URISyntaxException {
        log.debug("REST request to partial update PortfolioCurrency partially : {}, {}", id, portfolioCurrency);
        if (portfolioCurrency.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolioCurrency.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!portfolioCurrencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PortfolioCurrency> result = portfolioCurrencyService.partialUpdate(portfolioCurrency);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, portfolioCurrency.getId().toString())
        );
    }

    /**
     * {@code GET  /portfolio-currencies} : get all the portfolioCurrencies.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of portfolioCurrencies in body.
     */
    @GetMapping("/portfolio-currencies")
    public List<PortfolioCurrency> getAllPortfolioCurrencies() {
        log.debug("REST request to get all PortfolioCurrencies");
        return portfolioCurrencyService.findAll();
    }

    /**
     * {@code GET  /portfolio-currencies/:id} : get the "id" portfolioCurrency.
     *
     * @param id the id of the portfolioCurrency to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the portfolioCurrency, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/portfolio-currencies/{id}")
    public ResponseEntity<PortfolioCurrency> getPortfolioCurrency(@PathVariable Long id) {
        log.debug("REST request to get PortfolioCurrency : {}", id);
        Optional<PortfolioCurrency> portfolioCurrency = portfolioCurrencyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(portfolioCurrency);
    }

    /**
     * {@code DELETE  /portfolio-currencies/:id} : delete the "id" portfolioCurrency.
     *
     * @param id the id of the portfolioCurrency to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/portfolio-currencies/{id}")
    public ResponseEntity<Void> deletePortfolioCurrency(@PathVariable Long id) {
        log.debug("REST request to delete PortfolioCurrency : {}", id);
        portfolioCurrencyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/portfolio-currencies?query=:query} : search for the portfolioCurrency corresponding
     * to the query.
     *
     * @param query the query of the portfolioCurrency search.
     * @return the result of the search.
     */
    @GetMapping("/_search/portfolio-currencies")
    public List<PortfolioCurrency> searchPortfolioCurrencies(@RequestParam String query) {
        log.debug("REST request to search PortfolioCurrencies for query {}", query);
        return portfolioCurrencyService.search(query);
    }
}
