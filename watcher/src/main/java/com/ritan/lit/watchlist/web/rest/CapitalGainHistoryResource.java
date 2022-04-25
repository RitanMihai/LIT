package com.ritan.lit.watchlist.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.CapitalGainHistory;
import com.ritan.lit.watchlist.repository.CapitalGainHistoryRepository;
import com.ritan.lit.watchlist.service.CapitalGainHistoryService;
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
 * REST controller for managing {@link com.ritan.lit.watchlist.domain.CapitalGainHistory}.
 */
@RestController
@RequestMapping("/api")
public class CapitalGainHistoryResource {

    private final Logger log = LoggerFactory.getLogger(CapitalGainHistoryResource.class);

    private static final String ENTITY_NAME = "watcherCapitalGainHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CapitalGainHistoryService capitalGainHistoryService;

    private final CapitalGainHistoryRepository capitalGainHistoryRepository;

    public CapitalGainHistoryResource(
        CapitalGainHistoryService capitalGainHistoryService,
        CapitalGainHistoryRepository capitalGainHistoryRepository
    ) {
        this.capitalGainHistoryService = capitalGainHistoryService;
        this.capitalGainHistoryRepository = capitalGainHistoryRepository;
    }

    /**
     * {@code POST  /capital-gain-histories} : Create a new capitalGainHistory.
     *
     * @param capitalGainHistory the capitalGainHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new capitalGainHistory, or with status {@code 400 (Bad Request)} if the capitalGainHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/capital-gain-histories")
    public ResponseEntity<CapitalGainHistory> createCapitalGainHistory(@RequestBody CapitalGainHistory capitalGainHistory)
        throws URISyntaxException {
        log.debug("REST request to save CapitalGainHistory : {}", capitalGainHistory);
        if (capitalGainHistory.getId() != null) {
            throw new BadRequestAlertException("A new capitalGainHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CapitalGainHistory result = capitalGainHistoryService.save(capitalGainHistory);
        return ResponseEntity
            .created(new URI("/api/capital-gain-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /capital-gain-histories/:id} : Updates an existing capitalGainHistory.
     *
     * @param id the id of the capitalGainHistory to save.
     * @param capitalGainHistory the capitalGainHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated capitalGainHistory,
     * or with status {@code 400 (Bad Request)} if the capitalGainHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the capitalGainHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/capital-gain-histories/{id}")
    public ResponseEntity<CapitalGainHistory> updateCapitalGainHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CapitalGainHistory capitalGainHistory
    ) throws URISyntaxException {
        log.debug("REST request to update CapitalGainHistory : {}, {}", id, capitalGainHistory);
        if (capitalGainHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, capitalGainHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!capitalGainHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CapitalGainHistory result = capitalGainHistoryService.save(capitalGainHistory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, capitalGainHistory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /capital-gain-histories/:id} : Partial updates given fields of an existing capitalGainHistory, field will ignore if it is null
     *
     * @param id the id of the capitalGainHistory to save.
     * @param capitalGainHistory the capitalGainHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated capitalGainHistory,
     * or with status {@code 400 (Bad Request)} if the capitalGainHistory is not valid,
     * or with status {@code 404 (Not Found)} if the capitalGainHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the capitalGainHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/capital-gain-histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CapitalGainHistory> partialUpdateCapitalGainHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CapitalGainHistory capitalGainHistory
    ) throws URISyntaxException {
        log.debug("REST request to partial update CapitalGainHistory partially : {}, {}", id, capitalGainHistory);
        if (capitalGainHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, capitalGainHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!capitalGainHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CapitalGainHistory> result = capitalGainHistoryService.partialUpdate(capitalGainHistory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, capitalGainHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /capital-gain-histories} : get all the capitalGainHistories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of capitalGainHistories in body.
     */
    @GetMapping("/capital-gain-histories")
    public ResponseEntity<List<CapitalGainHistory>> getAllCapitalGainHistories(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of CapitalGainHistories");
        Page<CapitalGainHistory> page = capitalGainHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /capital-gain-histories/:id} : get the "id" capitalGainHistory.
     *
     * @param id the id of the capitalGainHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the capitalGainHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/capital-gain-histories/{id}")
    public ResponseEntity<CapitalGainHistory> getCapitalGainHistory(@PathVariable Long id) {
        log.debug("REST request to get CapitalGainHistory : {}", id);
        Optional<CapitalGainHistory> capitalGainHistory = capitalGainHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(capitalGainHistory);
    }

    /**
     * {@code DELETE  /capital-gain-histories/:id} : delete the "id" capitalGainHistory.
     *
     * @param id the id of the capitalGainHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/capital-gain-histories/{id}")
    public ResponseEntity<Void> deleteCapitalGainHistory(@PathVariable Long id) {
        log.debug("REST request to delete CapitalGainHistory : {}", id);
        capitalGainHistoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/capital-gain-histories?query=:query} : search for the capitalGainHistory corresponding
     * to the query.
     *
     * @param query the query of the capitalGainHistory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/capital-gain-histories")
    public ResponseEntity<List<CapitalGainHistory>> searchCapitalGainHistories(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of CapitalGainHistories for query {}", query);
        Page<CapitalGainHistory> page = capitalGainHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
