package com.ritan.lit.portfolio.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.portfolio.domain.Dividend;
import com.ritan.lit.portfolio.repository.DividendRepository;
import com.ritan.lit.portfolio.service.DividendService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ritan.lit.portfolio.domain.Dividend}.
 */
@RestController
@RequestMapping("/api")
public class DividendResource {

    private final Logger log = LoggerFactory.getLogger(DividendResource.class);

    private static final String ENTITY_NAME = "portfolioDividend";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DividendService dividendService;

    private final DividendRepository dividendRepository;

    public DividendResource(DividendService dividendService, DividendRepository dividendRepository) {
        this.dividendService = dividendService;
        this.dividendRepository = dividendRepository;
    }

    /**
     * {@code POST  /dividends} : Create a new dividend.
     *
     * @param dividend the dividend to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dividend, or with status {@code 400 (Bad Request)} if the dividend has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/dividends")
    public ResponseEntity<Dividend> createDividend(@RequestBody Dividend dividend) throws URISyntaxException {
        log.debug("REST request to save Dividend : {}", dividend);
        if (dividend.getId() != null) {
            throw new BadRequestAlertException("A new dividend cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Dividend result = dividendService.save(dividend);
        return ResponseEntity
            .created(new URI("/api/dividends/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /dividends/:id} : Updates an existing dividend.
     *
     * @param id the id of the dividend to save.
     * @param dividend the dividend to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dividend,
     * or with status {@code 400 (Bad Request)} if the dividend is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dividend couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/dividends/{id}")
    public ResponseEntity<Dividend> updateDividend(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Dividend dividend
    ) throws URISyntaxException {
        log.debug("REST request to update Dividend : {}, {}", id, dividend);
        if (dividend.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dividend.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dividendRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Dividend result = dividendService.save(dividend);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dividend.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /dividends/:id} : Partial updates given fields of an existing dividend, field will ignore if it is null
     *
     * @param id the id of the dividend to save.
     * @param dividend the dividend to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dividend,
     * or with status {@code 400 (Bad Request)} if the dividend is not valid,
     * or with status {@code 404 (Not Found)} if the dividend is not found,
     * or with status {@code 500 (Internal Server Error)} if the dividend couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/dividends/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Dividend> partialUpdateDividend(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Dividend dividend
    ) throws URISyntaxException {
        log.debug("REST request to partial update Dividend partially : {}, {}", id, dividend);
        if (dividend.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dividend.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dividendRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Dividend> result = dividendService.partialUpdate(dividend);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dividend.getId().toString())
        );
    }

    /**
     * {@code GET  /dividends} : get all the dividends.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dividends in body.
     */
    @GetMapping("/dividends")
    public ResponseEntity<List<Dividend>> getAllDividends(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Dividends");
        Page<Dividend> page = dividendService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /dividends/:id} : get the "id" dividend.
     *
     * @param id the id of the dividend to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dividend, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/dividends/{id}")
    public ResponseEntity<Dividend> getDividend(@PathVariable Long id) {
        log.debug("REST request to get Dividend : {}", id);
        Optional<Dividend> dividend = dividendService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dividend);
    }

    /**
     * {@code DELETE  /dividends/:id} : delete the "id" dividend.
     *
     * @param id the id of the dividend to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/dividends/{id}")
    public ResponseEntity<Void> deleteDividend(@PathVariable Long id) {
        log.debug("REST request to delete Dividend : {}", id);
        dividendService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/dividends?query=:query} : search for the dividend corresponding
     * to the query.
     *
     * @param query the query of the dividend search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/dividends")
    public ResponseEntity<List<Dividend>> searchDividends(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Dividends for query {}", query);
        Page<Dividend> page = dividendService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
