package com.ritan.lit.watchlist.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.StockSplitHistory;
import com.ritan.lit.watchlist.repository.StockSplitHistoryRepository;
import com.ritan.lit.watchlist.service.StockSplitHistoryService;
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
 * REST controller for managing {@link com.ritan.lit.watchlist.domain.StockSplitHistory}.
 */
@RestController
@RequestMapping("/api")
public class StockSplitHistoryResource {

    private final Logger log = LoggerFactory.getLogger(StockSplitHistoryResource.class);

    private static final String ENTITY_NAME = "watcherStockSplitHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockSplitHistoryService stockSplitHistoryService;

    private final StockSplitHistoryRepository stockSplitHistoryRepository;

    public StockSplitHistoryResource(
        StockSplitHistoryService stockSplitHistoryService,
        StockSplitHistoryRepository stockSplitHistoryRepository
    ) {
        this.stockSplitHistoryService = stockSplitHistoryService;
        this.stockSplitHistoryRepository = stockSplitHistoryRepository;
    }

    /**
     * {@code POST  /stock-split-histories} : Create a new stockSplitHistory.
     *
     * @param stockSplitHistory the stockSplitHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockSplitHistory, or with status {@code 400 (Bad Request)} if the stockSplitHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stock-split-histories")
    public ResponseEntity<StockSplitHistory> createStockSplitHistory(@RequestBody StockSplitHistory stockSplitHistory)
        throws URISyntaxException {
        log.debug("REST request to save StockSplitHistory : {}", stockSplitHistory);
        if (stockSplitHistory.getId() != null) {
            throw new BadRequestAlertException("A new stockSplitHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StockSplitHistory result = stockSplitHistoryService.save(stockSplitHistory);
        return ResponseEntity
            .created(new URI("/api/stock-split-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stock-split-histories/:id} : Updates an existing stockSplitHistory.
     *
     * @param id the id of the stockSplitHistory to save.
     * @param stockSplitHistory the stockSplitHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockSplitHistory,
     * or with status {@code 400 (Bad Request)} if the stockSplitHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockSplitHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stock-split-histories/{id}")
    public ResponseEntity<StockSplitHistory> updateStockSplitHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StockSplitHistory stockSplitHistory
    ) throws URISyntaxException {
        log.debug("REST request to update StockSplitHistory : {}, {}", id, stockSplitHistory);
        if (stockSplitHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockSplitHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockSplitHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StockSplitHistory result = stockSplitHistoryService.save(stockSplitHistory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockSplitHistory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stock-split-histories/:id} : Partial updates given fields of an existing stockSplitHistory, field will ignore if it is null
     *
     * @param id the id of the stockSplitHistory to save.
     * @param stockSplitHistory the stockSplitHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockSplitHistory,
     * or with status {@code 400 (Bad Request)} if the stockSplitHistory is not valid,
     * or with status {@code 404 (Not Found)} if the stockSplitHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockSplitHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stock-split-histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockSplitHistory> partialUpdateStockSplitHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StockSplitHistory stockSplitHistory
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockSplitHistory partially : {}, {}", id, stockSplitHistory);
        if (stockSplitHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockSplitHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockSplitHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockSplitHistory> result = stockSplitHistoryService.partialUpdate(stockSplitHistory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockSplitHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-split-histories} : get all the stockSplitHistories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockSplitHistories in body.
     */
    @GetMapping("/stock-split-histories")
    public ResponseEntity<List<StockSplitHistory>> getAllStockSplitHistories(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of StockSplitHistories");
        Page<StockSplitHistory> page = stockSplitHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-split-histories/:id} : get the "id" stockSplitHistory.
     *
     * @param id the id of the stockSplitHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockSplitHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stock-split-histories/{id}")
    public ResponseEntity<StockSplitHistory> getStockSplitHistory(@PathVariable Long id) {
        log.debug("REST request to get StockSplitHistory : {}", id);
        Optional<StockSplitHistory> stockSplitHistory = stockSplitHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockSplitHistory);
    }

    /**
     * {@code DELETE  /stock-split-histories/:id} : delete the "id" stockSplitHistory.
     *
     * @param id the id of the stockSplitHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stock-split-histories/{id}")
    public ResponseEntity<Void> deleteStockSplitHistory(@PathVariable Long id) {
        log.debug("REST request to delete StockSplitHistory : {}", id);
        stockSplitHistoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/stock-split-histories?query=:query} : search for the stockSplitHistory corresponding
     * to the query.
     *
     * @param query the query of the stockSplitHistory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/stock-split-histories")
    public ResponseEntity<List<StockSplitHistory>> searchStockSplitHistories(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of StockSplitHistories for query {}", query);
        Page<StockSplitHistory> page = stockSplitHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
