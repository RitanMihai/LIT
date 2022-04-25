package com.ritan.lit.portfolio.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.portfolio.domain.StockInfo;
import com.ritan.lit.portfolio.repository.StockInfoRepository;
import com.ritan.lit.portfolio.service.StockInfoService;
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
 * REST controller for managing {@link com.ritan.lit.portfolio.domain.StockInfo}.
 */
@RestController
@RequestMapping("/api")
public class StockInfoResource {

    private final Logger log = LoggerFactory.getLogger(StockInfoResource.class);

    private static final String ENTITY_NAME = "portfolioStockInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockInfoService stockInfoService;

    private final StockInfoRepository stockInfoRepository;

    public StockInfoResource(StockInfoService stockInfoService, StockInfoRepository stockInfoRepository) {
        this.stockInfoService = stockInfoService;
        this.stockInfoRepository = stockInfoRepository;
    }

    /**
     * {@code POST  /stock-infos} : Create a new stockInfo.
     *
     * @param stockInfo the stockInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockInfo, or with status {@code 400 (Bad Request)} if the stockInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stock-infos")
    public ResponseEntity<StockInfo> createStockInfo(@RequestBody StockInfo stockInfo) throws URISyntaxException {
        log.debug("REST request to save StockInfo : {}", stockInfo);
        if (stockInfo.getId() != null) {
            throw new BadRequestAlertException("A new stockInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StockInfo result = stockInfoService.save(stockInfo);
        return ResponseEntity
            .created(new URI("/api/stock-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stock-infos/:id} : Updates an existing stockInfo.
     *
     * @param id the id of the stockInfo to save.
     * @param stockInfo the stockInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockInfo,
     * or with status {@code 400 (Bad Request)} if the stockInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stock-infos/{id}")
    public ResponseEntity<StockInfo> updateStockInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StockInfo stockInfo
    ) throws URISyntaxException {
        log.debug("REST request to update StockInfo : {}, {}", id, stockInfo);
        if (stockInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StockInfo result = stockInfoService.save(stockInfo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stock-infos/:id} : Partial updates given fields of an existing stockInfo, field will ignore if it is null
     *
     * @param id the id of the stockInfo to save.
     * @param stockInfo the stockInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockInfo,
     * or with status {@code 400 (Bad Request)} if the stockInfo is not valid,
     * or with status {@code 404 (Not Found)} if the stockInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stock-infos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockInfo> partialUpdateStockInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StockInfo stockInfo
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockInfo partially : {}, {}", id, stockInfo);
        if (stockInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockInfo> result = stockInfoService.partialUpdate(stockInfo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockInfo.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-infos} : get all the stockInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockInfos in body.
     */
    @GetMapping("/stock-infos")
    public List<StockInfo> getAllStockInfos() {
        log.debug("REST request to get all StockInfos");
        return stockInfoService.findAll();
    }

    /**
     * {@code GET  /stock-infos/:id} : get the "id" stockInfo.
     *
     * @param id the id of the stockInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stock-infos/{id}")
    public ResponseEntity<StockInfo> getStockInfo(@PathVariable Long id) {
        log.debug("REST request to get StockInfo : {}", id);
        Optional<StockInfo> stockInfo = stockInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockInfo);
    }

    /**
     * {@code DELETE  /stock-infos/:id} : delete the "id" stockInfo.
     *
     * @param id the id of the stockInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stock-infos/{id}")
    public ResponseEntity<Void> deleteStockInfo(@PathVariable Long id) {
        log.debug("REST request to delete StockInfo : {}", id);
        stockInfoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/stock-infos?query=:query} : search for the stockInfo corresponding
     * to the query.
     *
     * @param query the query of the stockInfo search.
     * @return the result of the search.
     */
    @GetMapping("/_search/stock-infos")
    public List<StockInfo> searchStockInfos(@RequestParam String query) {
        log.debug("REST request to search StockInfos for query {}", query);
        return stockInfoService.search(query);
    }
}
