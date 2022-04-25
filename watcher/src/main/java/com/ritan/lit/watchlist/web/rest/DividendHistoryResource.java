package com.ritan.lit.watchlist.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.DividendHistory;
import com.ritan.lit.watchlist.repository.DividendHistoryRepository;
import com.ritan.lit.watchlist.service.DividendHistoryService;
import com.ritan.lit.watchlist.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.ritan.lit.watchlist.domain.DividendHistory}.
 */
@RestController
@RequestMapping("/api")
public class DividendHistoryResource {

    private final Logger log = LoggerFactory.getLogger(DividendHistoryResource.class);

    private static final String ENTITY_NAME = "watcherDividendHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DividendHistoryService dividendHistoryService;

    private final DividendHistoryRepository dividendHistoryRepository;

    public DividendHistoryResource(DividendHistoryService dividendHistoryService, DividendHistoryRepository dividendHistoryRepository) {
        this.dividendHistoryService = dividendHistoryService;
        this.dividendHistoryRepository = dividendHistoryRepository;
    }

    /**
     * {@code POST  /dividend-histories} : Create a new dividendHistory.
     *
     * @param dividendHistory the dividendHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dividendHistory, or with status {@code 400 (Bad Request)} if the dividendHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/dividend-histories")
    public ResponseEntity<DividendHistory> createDividendHistory(@RequestBody DividendHistory dividendHistory) throws URISyntaxException {
        log.debug("REST request to save DividendHistory : {}", dividendHistory);
        if (dividendHistory.getId() != null) {
            throw new BadRequestAlertException("A new dividendHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DividendHistory result = dividendHistoryService.save(dividendHistory);
        return ResponseEntity
            .created(new URI("/api/dividend-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /dividend-histories/:id} : Updates an existing dividendHistory.
     *
     * @param id the id of the dividendHistory to save.
     * @param dividendHistory the dividendHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dividendHistory,
     * or with status {@code 400 (Bad Request)} if the dividendHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dividendHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/dividend-histories/{id}")
    public ResponseEntity<DividendHistory> updateDividendHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DividendHistory dividendHistory
    ) throws URISyntaxException {
        log.debug("REST request to update DividendHistory : {}, {}", id, dividendHistory);
        if (dividendHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dividendHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dividendHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DividendHistory result = dividendHistoryService.save(dividendHistory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dividendHistory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /dividend-histories/:id} : Partial updates given fields of an existing dividendHistory, field will ignore if it is null
     *
     * @param id the id of the dividendHistory to save.
     * @param dividendHistory the dividendHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dividendHistory,
     * or with status {@code 400 (Bad Request)} if the dividendHistory is not valid,
     * or with status {@code 404 (Not Found)} if the dividendHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the dividendHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/dividend-histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DividendHistory> partialUpdateDividendHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DividendHistory dividendHistory
    ) throws URISyntaxException {
        log.debug("REST request to partial update DividendHistory partially : {}, {}", id, dividendHistory);
        if (dividendHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dividendHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dividendHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DividendHistory> result = dividendHistoryService.partialUpdate(dividendHistory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dividendHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /dividend-histories} : get all the dividendHistories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dividendHistories in body.
     */
    @GetMapping("/dividend-histories")
    public ResponseEntity<List<DividendHistory>> getAllDividendHistories(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of DividendHistories");
        Page<DividendHistory> page = dividendHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /dividend-histories/:id} : get the "id" dividendHistory.
     *
     * @param id the id of the dividendHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dividendHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/dividend-histories/{id}")
    public ResponseEntity<DividendHistory> getDividendHistory(@PathVariable Long id) {
        log.debug("REST request to get DividendHistory : {}", id);
        Optional<DividendHistory> dividendHistory = dividendHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dividendHistory);
    }

    /**
     * {@code DELETE  /dividend-histories/:id} : delete the "id" dividendHistory.
     *
     * @param id the id of the dividendHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/dividend-histories/{id}")
    public ResponseEntity<Void> deleteDividendHistory(@PathVariable Long id) {
        log.debug("REST request to delete DividendHistory : {}", id);
        dividendHistoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/dividend-histories?query=:query} : search for the dividendHistory corresponding
     * to the query.
     *
     * @param query the query of the dividendHistory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/dividend-histories")
    public ResponseEntity<List<DividendHistory>> searchDividendHistories(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of DividendHistories for query {}", query);
        Page<DividendHistory> page = dividendHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
