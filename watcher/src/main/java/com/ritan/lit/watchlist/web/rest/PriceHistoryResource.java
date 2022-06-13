package com.ritan.lit.watchlist.web.rest;

import com.ritan.lit.watchlist.domain.Currency;
import com.ritan.lit.watchlist.domain.PriceHistory;
import com.ritan.lit.watchlist.domain.Stock;
import com.ritan.lit.watchlist.domain.util.DateDoublePair;
import com.ritan.lit.watchlist.repository.PriceHistoryRepository;
import com.ritan.lit.watchlist.service.CurrencyService;
import com.ritan.lit.watchlist.service.PriceHistoryService;
import com.ritan.lit.watchlist.service.StockExchangeService;
import com.ritan.lit.watchlist.service.StockService;
import com.ritan.lit.watchlist.web.rest.errors.BadRequestAlertException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

/**
 * REST controller for managing {@link com.ritan.lit.watchlist.domain.PriceHistory}.
 */
@RestController
@RequestMapping("/api")
public class PriceHistoryResource {

    private final Logger log = LoggerFactory.getLogger(PriceHistoryResource.class);

    private static final String ENTITY_NAME = "watcherPriceHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PriceHistoryService priceHistoryService;

    private final StockService stockService;
    private final StockExchangeService stockExchangeService;
    private final PriceHistoryRepository priceHistoryRepository;

    public PriceHistoryResource(PriceHistoryService priceHistoryService, StockService stockService, StockExchangeService stockExchangeService, PriceHistoryRepository priceHistoryRepository) {
        this.priceHistoryService = priceHistoryService;
        this.stockService = stockService;
        this.stockExchangeService = stockExchangeService;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    /**
     * {@code POST  /price-histories} : Create a new priceHistory.
     *
     * @param priceHistory the priceHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new priceHistory, or with status {@code 400 (Bad Request)} if the priceHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/price-histories")
    public ResponseEntity<PriceHistory> createPriceHistory(@RequestBody PriceHistory priceHistory) throws URISyntaxException {
        log.debug("REST request to save PriceHistory : {}", priceHistory);
        if (priceHistory.getId() != null) {
            throw new BadRequestAlertException("A new priceHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PriceHistory result = priceHistoryService.save(priceHistory);
        return ResponseEntity
            .created(new URI("/api/price-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PostMapping("/price-histories/stock/{symbol}")
    public ResponseEntity<?> setPriceHistory(@PathVariable String symbol,
                                             @RequestParam Optional<String> start_date,
                                             @RequestParam Optional<String> end_date,
                                             @RequestParam Optional<String> period,
                                             @RequestParam Optional<String> period_type) throws IOException {
        // Unsure received string is upper case
        symbol.toUpperCase(Locale.ROOT);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        yahoofinance.Stock stock = null;

        Optional<Stock> pgStock = stockService.findByTicker(symbol);
        if (!pgStock.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Stock did not found in database");

        if (start_date.isPresent() && end_date.isPresent()) {
            Date startDate;
            Object endDate = null;

            try {
                startDate = sdf.parse(start_date.get());
                if (!end_date.get().equals("TODAY"))
                    endDate = sdf.parse(end_date.get());
            } catch (ParseException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Date format, " +
                    "accepted format is dd-MM-yyyy");
            }

            Calendar from = Calendar.getInstance();
            from.setTime(startDate);
            Calendar to = Calendar.getInstance();

            if (endDate instanceof Date)
                to.setTime((Date) endDate);

            try {
                stock = YahooFinance.get(symbol, from, to, Interval.DAILY);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } else if (period.isPresent() && period_type.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not implemented yet");
        }

        stock.getHistory().forEach(
            historicalQuote -> {
                PriceHistory priceHistory = new PriceHistory();
                priceHistory.setStock(pgStock.get());

                Calendar calendar = historicalQuote.getDate();
                Date input = calendar.getTime();
                LocalDate localDate = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                priceHistory.setDate(localDate);
                priceHistory.setOpen(historicalQuote.getOpen().doubleValue());
                priceHistory.setLow(historicalQuote.getLow().doubleValue());
                priceHistory.setHigh(historicalQuote.getHigh().doubleValue());
                priceHistory.setAdjClose(historicalQuote.getAdjClose().doubleValue());
                priceHistory.setVolume(historicalQuote.getVolume().doubleValue());
                priceHistory.setClose(historicalQuote.getClose().doubleValue());

                //Stock stockUpdated = pgStock.get().addPriceHistory(priceHistory);

                // Ignore elastic search
                priceHistoryRepository.save(priceHistory);
                //stockService.partialUpdate(stockUpdated);
            }
        );

        return ResponseEntity.ok(stock);
    }

    /* TODO: change closePrice with Filter */
    @GetMapping("/price-histories/symbol/{symbol}")
    public ResponseEntity<?> getPriceHistoryBySymbol(@PathVariable String symbol,
                                                     @RequestParam Optional<String> closePrice){
        Optional<Stock> stock = stockService.findByTicker(symbol);
        if (!stock.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Stock did not found in database");

        if(closePrice.isPresent()) {

            List<DateDoublePair> allByStock = new ArrayList<>();
            for (PriceHistory priceHistory : priceHistoryService.findAllByStock(stock.get())) {
                LocalDate date = priceHistory.getDate();
                Double close = priceHistory.getClose();
                DateDoublePair dateDoublePair = new DateDoublePair(date, close);
                allByStock.add(dateDoublePair);
            }
            return ResponseEntity.ok(allByStock);
        }
        return  ResponseEntity.ok(priceHistoryService.findAllByStock(stock.get()));
    }
    /**
     * {@code PUT  /price-histories/:id} : Updates an existing priceHistory.
     *
     * @param id           the id of the priceHistory to save.
     * @param priceHistory the priceHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated priceHistory,
     * or with status {@code 400 (Bad Request)} if the priceHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the priceHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/price-histories/{id}")
    public ResponseEntity<PriceHistory> updatePriceHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PriceHistory priceHistory
    ) throws URISyntaxException {
        log.debug("REST request to update PriceHistory : {}, {}", id, priceHistory);
        if (priceHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, priceHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!priceHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PriceHistory result = priceHistoryService.save(priceHistory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, priceHistory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /price-histories/:id} : Partial updates given fields of an existing priceHistory, field will ignore if it is null
     *
     * @param id           the id of the priceHistory to save.
     * @param priceHistory the priceHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated priceHistory,
     * or with status {@code 400 (Bad Request)} if the priceHistory is not valid,
     * or with status {@code 404 (Not Found)} if the priceHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the priceHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/price-histories/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<PriceHistory> partialUpdatePriceHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PriceHistory priceHistory
    ) throws URISyntaxException {
        log.debug("REST request to partial update PriceHistory partially : {}, {}", id, priceHistory);
        if (priceHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, priceHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!priceHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PriceHistory> result = priceHistoryService.partialUpdate(priceHistory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, priceHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /price-histories} : get all the priceHistories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of priceHistories in body.
     */
    @GetMapping("/price-histories")
    public ResponseEntity<List<PriceHistory>> getAllPriceHistories(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of PriceHistories");
        Page<PriceHistory> page = priceHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /price-histories/:id} : get the "id" priceHistory.
     *
     * @param id the id of the priceHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the priceHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/price-histories/{id}")
    public ResponseEntity<PriceHistory> getPriceHistory(@PathVariable Long id) {
        log.debug("REST request to get PriceHistory : {}", id);
        Optional<PriceHistory> priceHistory = priceHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(priceHistory);
    }

    /**
     * {@code DELETE  /price-histories/:id} : delete the "id" priceHistory.
     *
     * @param id the id of the priceHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/price-histories/{id}")
    public ResponseEntity<Void> deletePriceHistory(@PathVariable Long id) {
        log.debug("REST request to delete PriceHistory : {}", id);
        priceHistoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/price-histories?query=:query} : search for the priceHistory corresponding
     * to the query.
     *
     * @param query    the query of the priceHistory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/price-histories")
    public ResponseEntity<List<PriceHistory>> searchPriceHistories(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of PriceHistories for query {}", query);
        Page<PriceHistory> page = priceHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
