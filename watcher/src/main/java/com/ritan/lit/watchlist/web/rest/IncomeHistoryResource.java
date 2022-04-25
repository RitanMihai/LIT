package com.ritan.lit.watchlist.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.IncomeHistory;
import com.ritan.lit.watchlist.repository.IncomeHistoryRepository;
import com.ritan.lit.watchlist.service.IncomeHistoryService;
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
 * REST controller for managing {@link com.ritan.lit.watchlist.domain.IncomeHistory}.
 */
@RestController
@RequestMapping("/api")
public class IncomeHistoryResource {

    private final Logger log = LoggerFactory.getLogger(IncomeHistoryResource.class);

    private static final String ENTITY_NAME = "watcherIncomeHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IncomeHistoryService incomeHistoryService;

    private final IncomeHistoryRepository incomeHistoryRepository;

    public IncomeHistoryResource(IncomeHistoryService incomeHistoryService, IncomeHistoryRepository incomeHistoryRepository) {
        this.incomeHistoryService = incomeHistoryService;
        this.incomeHistoryRepository = incomeHistoryRepository;
    }

    /**
     * {@code POST  /income-histories} : Create a new incomeHistory.
     *
     * @param incomeHistory the incomeHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new incomeHistory, or with status {@code 400 (Bad Request)} if the incomeHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/income-histories")
    public ResponseEntity<IncomeHistory> createIncomeHistory(@RequestBody IncomeHistory incomeHistory) throws URISyntaxException {
        log.debug("REST request to save IncomeHistory : {}", incomeHistory);
        if (incomeHistory.getId() != null) {
            throw new BadRequestAlertException("A new incomeHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        IncomeHistory result = incomeHistoryService.save(incomeHistory);
        return ResponseEntity
            .created(new URI("/api/income-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /income-histories/:id} : Updates an existing incomeHistory.
     *
     * @param id the id of the incomeHistory to save.
     * @param incomeHistory the incomeHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incomeHistory,
     * or with status {@code 400 (Bad Request)} if the incomeHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the incomeHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/income-histories/{id}")
    public ResponseEntity<IncomeHistory> updateIncomeHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody IncomeHistory incomeHistory
    ) throws URISyntaxException {
        log.debug("REST request to update IncomeHistory : {}, {}", id, incomeHistory);
        if (incomeHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incomeHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!incomeHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        IncomeHistory result = incomeHistoryService.save(incomeHistory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, incomeHistory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /income-histories/:id} : Partial updates given fields of an existing incomeHistory, field will ignore if it is null
     *
     * @param id the id of the incomeHistory to save.
     * @param incomeHistory the incomeHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incomeHistory,
     * or with status {@code 400 (Bad Request)} if the incomeHistory is not valid,
     * or with status {@code 404 (Not Found)} if the incomeHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the incomeHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/income-histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IncomeHistory> partialUpdateIncomeHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody IncomeHistory incomeHistory
    ) throws URISyntaxException {
        log.debug("REST request to partial update IncomeHistory partially : {}, {}", id, incomeHistory);
        if (incomeHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incomeHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!incomeHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IncomeHistory> result = incomeHistoryService.partialUpdate(incomeHistory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, incomeHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /income-histories} : get all the incomeHistories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of incomeHistories in body.
     */
    @GetMapping("/income-histories")
    public ResponseEntity<List<IncomeHistory>> getAllIncomeHistories(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of IncomeHistories");
        Page<IncomeHistory> page = incomeHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /income-histories/:id} : get the "id" incomeHistory.
     *
     * @param id the id of the incomeHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the incomeHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/income-histories/{id}")
    public ResponseEntity<IncomeHistory> getIncomeHistory(@PathVariable Long id) {
        log.debug("REST request to get IncomeHistory : {}", id);
        Optional<IncomeHistory> incomeHistory = incomeHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(incomeHistory);
    }

    /**
     * {@code DELETE  /income-histories/:id} : delete the "id" incomeHistory.
     *
     * @param id the id of the incomeHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/income-histories/{id}")
    public ResponseEntity<Void> deleteIncomeHistory(@PathVariable Long id) {
        log.debug("REST request to delete IncomeHistory : {}", id);
        incomeHistoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/income-histories?query=:query} : search for the incomeHistory corresponding
     * to the query.
     *
     * @param query the query of the incomeHistory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/income-histories")
    public ResponseEntity<List<IncomeHistory>> searchIncomeHistories(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of IncomeHistories for query {}", query);
        Page<IncomeHistory> page = incomeHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
