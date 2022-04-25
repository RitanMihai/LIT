package com.ritan.lit.watchlist.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.StockExchange;
import com.ritan.lit.watchlist.repository.StockExchangeRepository;
import com.ritan.lit.watchlist.service.StockExchangeService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ritan.lit.watchlist.domain.StockExchange}.
 */
@RestController
@RequestMapping("/api")
public class StockExchangeResource {

    private final Logger log = LoggerFactory.getLogger(StockExchangeResource.class);

    private static final String ENTITY_NAME = "watcherStockExchange";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockExchangeService stockExchangeService;

    private final StockExchangeRepository stockExchangeRepository;

    public StockExchangeResource(StockExchangeService stockExchangeService, StockExchangeRepository stockExchangeRepository) {
        this.stockExchangeService = stockExchangeService;
        this.stockExchangeRepository = stockExchangeRepository;
    }

    /**
     * {@code POST  /stock-exchanges} : Create a new stockExchange.
     *
     * @param stockExchange the stockExchange to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockExchange, or with status {@code 400 (Bad Request)} if the stockExchange has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stock-exchanges")
    public ResponseEntity<StockExchange> createStockExchange(@RequestBody StockExchange stockExchange) throws URISyntaxException {
        log.debug("REST request to save StockExchange : {}", stockExchange);
        if (stockExchange.getId() != null) {
            throw new BadRequestAlertException("A new stockExchange cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StockExchange result = stockExchangeService.save(stockExchange);
        return ResponseEntity
            .created(new URI("/api/stock-exchanges/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stock-exchanges/:id} : Updates an existing stockExchange.
     *
     * @param id the id of the stockExchange to save.
     * @param stockExchange the stockExchange to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockExchange,
     * or with status {@code 400 (Bad Request)} if the stockExchange is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockExchange couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stock-exchanges/{id}")
    public ResponseEntity<StockExchange> updateStockExchange(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StockExchange stockExchange
    ) throws URISyntaxException {
        log.debug("REST request to update StockExchange : {}, {}", id, stockExchange);
        if (stockExchange.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockExchange.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockExchangeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StockExchange result = stockExchangeService.save(stockExchange);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockExchange.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stock-exchanges/:id} : Partial updates given fields of an existing stockExchange, field will ignore if it is null
     *
     * @param id the id of the stockExchange to save.
     * @param stockExchange the stockExchange to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockExchange,
     * or with status {@code 400 (Bad Request)} if the stockExchange is not valid,
     * or with status {@code 404 (Not Found)} if the stockExchange is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockExchange couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stock-exchanges/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockExchange> partialUpdateStockExchange(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StockExchange stockExchange
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockExchange partially : {}, {}", id, stockExchange);
        if (stockExchange.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockExchange.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockExchangeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockExchange> result = stockExchangeService.partialUpdate(stockExchange);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockExchange.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-exchanges} : get all the stockExchanges.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockExchanges in body.
     */
    @GetMapping("/stock-exchanges")
    public List<StockExchange> getAllStockExchanges() {
        log.debug("REST request to get all StockExchanges");
        return stockExchangeService.findAll();
    }

    /**
     * {@code GET  /stock-exchanges/:id} : get the "id" stockExchange.
     *
     * @param id the id of the stockExchange to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockExchange, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stock-exchanges/{id}")
    public ResponseEntity<StockExchange> getStockExchange(@PathVariable Long id) {
        log.debug("REST request to get StockExchange : {}", id);
        Optional<StockExchange> stockExchange = stockExchangeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockExchange);
    }

    /**
     * {@code DELETE  /stock-exchanges/:id} : delete the "id" stockExchange.
     *
     * @param id the id of the stockExchange to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stock-exchanges/{id}")
    public ResponseEntity<Void> deleteStockExchange(@PathVariable Long id) {
        log.debug("REST request to delete StockExchange : {}", id);
        stockExchangeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/stock-exchanges?query=:query} : search for the stockExchange corresponding
     * to the query.
     *
     * @param query the query of the stockExchange search.
     * @return the result of the search.
     */
    @GetMapping("/_search/stock-exchanges")
    public List<StockExchange> searchStockExchanges(@RequestParam String query) {
        log.debug("REST request to search StockExchanges for query {}", query);
        return stockExchangeService.search(query);
    }
}
