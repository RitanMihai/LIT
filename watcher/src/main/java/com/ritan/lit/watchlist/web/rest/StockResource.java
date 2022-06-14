package com.ritan.lit.watchlist.web.rest;

import com.ritan.lit.watchlist.domain.PageableStock;
import com.ritan.lit.watchlist.domain.Stock;
import com.ritan.lit.watchlist.domain.StockGroupByType;
import com.ritan.lit.watchlist.repository.StockRepository;
import com.ritan.lit.watchlist.service.StockService;
import com.ritan.lit.watchlist.web.rest.errors.BadRequestAlertException;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import javax.imageio.ImageIO;

/**
 * REST controller for managing {@link com.ritan.lit.watchlist.domain.Stock}.
 */
@RestController
@RequestMapping("/api")
public class StockResource {

    private final Logger log = LoggerFactory.getLogger(StockResource.class);

    private static final String ENTITY_NAME = "watcherStock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockService stockService;

    private final StockRepository stockRepository;

    public StockResource(StockService stockService, StockRepository stockRepository) {
        this.stockService = stockService;
        this.stockRepository = stockRepository;
    }

    /**
     * {@code POST  /stocks} : Create a new stock.
     *
     * @param stock the stock to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stock, or with status {@code 400 (Bad Request)} if the stock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stocks")
    public ResponseEntity<Stock> createStock(@RequestBody Stock stock) throws URISyntaxException {
        log.debug("REST request to save Stock : {}", stock);
        if (stock.getId() != null) {
            throw new BadRequestAlertException("A new stock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Stock result = stockService.save(stock);
        return ResponseEntity
            .created(new URI("/api/stocks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PostMapping("/stocks/list")
    public ResponseEntity<List<Stock>> createStocks(@RequestBody List<Stock> stocks) throws URISyntaxException {
        log.debug("REST request to save Stock : {}", stocks);

        stocks.forEach(stock -> {
            if (stock.getId() != null)
                throw new BadRequestAlertException("A new stock cannot already have an ID", ENTITY_NAME, "idexists");
        });

        List<Stock> result = stockService.saveAll(stocks);

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.toString()))
            .build();
    }

    @GetMapping("/stocks/types/{type}")
    public ResponseEntity<?> countStocksBySector(@PathVariable Optional<String> type) {
        if (!type.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You should provide a type");

        switch (type.get()) {
            case "sector": {
                List<Object[]> allStocksBySector = stockService.countStocksBySector();
                List<StockGroupByType> stockSectors = new ArrayList<>();
                allStocksBySector.forEach(objects -> {
                    StockGroupByType stockGroupByType = new StockGroupByType();
                    Optional<Object> category = Optional.ofNullable(objects[0]);
                    Optional<Object> number = Optional.ofNullable(objects[1]);

                    if (category.isPresent() && number.isPresent()) {
                        if (StringUtils.isNotBlank(category.get().toString())) {
                            stockGroupByType.setCategory(objects[0].toString());
                            stockGroupByType.setNumber(Long.valueOf(objects[1].toString()));
                            stockSectors.add(stockGroupByType);
                        }
                    }
                });
                return ResponseEntity.ok(stockSectors);
            }
            case "industry": {
                List<Object[]> allStocksByIndustry = stockService.countStocksByIndustry();
                List<StockGroupByType> stockIndustry = new ArrayList<>();

                allStocksByIndustry.forEach(objects -> {
                    StockGroupByType stockGroupByType = new StockGroupByType();
                    Optional<Object> category = Optional.ofNullable(objects[0]);
                    Optional<Object> number = Optional.ofNullable(objects[1]);

                    if (category.isPresent() && number.isPresent()) {
                        if (StringUtils.isNotBlank(category.get().toString())) {
                            stockGroupByType.setCategory(objects[0].toString());
                            stockGroupByType.setNumber(Long.valueOf(objects[1].toString()));
                            stockIndustry.add(stockGroupByType);
                        }
                    }
                });
                return ResponseEntity.ok(stockIndustry);
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Type not found");
    }

    @GetMapping("/stocks/sector/{sector}")
    public ResponseEntity<?> getAllBySector(@PathVariable String sector, @RequestParam("page") Optional<Integer> page,
                                            @RequestParam("limit") Optional<Integer> limit,
                                            @RequestParam("sortBy") Optional<String> sortBy) {
        if (page.isPresent() && limit.isPresent()) {
            PageableStock stocks = new PageableStock();
            stocks.setStocks(stockService.findAllBySector(sector, page.get(), limit.get(), sortBy));
            stocks.setSize(stockService.stockRowsNumberBySector(sector));
            return ResponseEntity.ok(stocks);
        }
        return ResponseEntity.ok(stockService.findAllBySector(sector));
    }

    @GetMapping("/stocks/industry/{industry}")
    public ResponseEntity<?> getAllByIndustry(@PathVariable String industry, @RequestParam("page") Optional<Integer> page,
                                            @RequestParam("limit") Optional<Integer> limit,
                                            @RequestParam("sortBy") Optional<String> sortBy) {
        if (page.isPresent() && limit.isPresent()) {
            PageableStock stocks = new PageableStock();
            stocks.setStocks(stockService.findAllByIndustry(industry, page.get(), limit.get(), sortBy));
            stocks.setSize(stockService.stockRowsNumberByIndustry(industry));
            return ResponseEntity.ok(stocks);
        }
        return ResponseEntity.ok(stockService.findAllByIndustry(industry));
    }

    /**
     * {@code PUT  /stocks/:id} : Updates an existing stock.
     *
     * @param id    the id of the stock to save.
     * @param stock the stock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stock,
     * or with status {@code 400 (Bad Request)} if the stock is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stocks/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable(value = "id", required = false) final Long id, @RequestBody Stock stock)
        throws URISyntaxException {
        log.debug("REST request to update Stock : {}, {}", id, stock);
        if (stock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Stock result = stockService.save(stock);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stock.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stocks/:id} : Partial updates given fields of an existing stock, field will ignore if it is null
     *
     * @param id    the id of the stock to save.
     * @param stock the stock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stock,
     * or with status {@code 400 (Bad Request)} if the stock is not valid,
     * or with status {@code 404 (Not Found)} if the stock is not found,
     * or with status {@code 500 (Internal Server Error)} if the stock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stocks/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<Stock> partialUpdateStock(@PathVariable(value = "id", required = false) final Long id, @RequestBody Stock stock)
        throws URISyntaxException {
        log.debug("REST request to partial update Stock partially : {}, {}", id, stock);
        if (stock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Stock> result = stockService.partialUpdate(stock);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stock.getId().toString())
        );
    }

    /**
     * {@code GET  /stocks} : get all the stocks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stocks in body.
     */
    @GetMapping("/stocks")
    public List<Stock> getAllStocks() {
        log.debug("REST request to get all Stocks");
        return stockService.findAll();
    }

    /**
     * {@code GET  /stocks/:id} : get the "id" stock.
     *
     * @param id the id of the stock to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stock, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stocks/{id}")
    public ResponseEntity<Stock> getStock(@PathVariable Long id) {
        log.debug("REST request to get Stock : {}", id);
        Optional<Stock> stock = stockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stock);
    }

    @PostMapping("/__stocks/update/market_cap")
    public ResponseEntity<?> __updateStockMarketCap(@RequestBody List<String> symbols) {
        for (String symbol : symbols) {
            Optional<Stock> byTicker = stockService.findByTicker(symbol);

            if(byTicker.isPresent())
            {
                Stock pgStock = byTicker.get();
                yahoofinance.Stock stock = null;
                try {
                    stock = YahooFinance.get(symbol);
                    if(!Objects.isNull(stock)) {
                        if(!Objects.isNull(stock.getStats().getMarketCap())) {
                            Long marketCapValue = stock.getStats().getMarketCap().longValue();
                            pgStock.setMarketCap(marketCapValue);
                            stockService.partialUpdate(pgStock);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("EROARE PE STOCK " + symbol);
                }
            }

        }
        return ResponseEntity.ok("Done");
    }

    @PostMapping("/__stocks/update/images")
    public ResponseEntity<?> __updateStockImages() throws IOException {
        File[] files =
            new File("D:\\Documents\\Projects\\Master_Degree_Project\\LIT\\watcher\\src\\main\\resources\\stock\\logos")
            .listFiles();

        for (File file : files) {
            BufferedImage image = ImageIO.read(file);
            String symbol = file.getName().substring(0, file.getName().lastIndexOf('.'));
            Optional<Stock> byTicker = stockService.findByTicker(symbol);

            if(byTicker.isPresent()){
                Stock pgStock = byTicker.get();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                byte[] bytes = baos.toByteArray();
                pgStock.setImage(bytes);
                pgStock.setImageContentType("image/png");
                stockService.partialUpdate(pgStock);
            }
        }
        return ResponseEntity.ok("Done");
    }

    @GetMapping("stocks/symbol/{symbol}")
    public ResponseEntity<Stock> getStock(@PathVariable String symbol) {
        Optional<Stock> stock = stockService.findByTicker(symbol);
        return ResponseUtil.wrapOrNotFound(stock);
    }

    /**
     * {@code DELETE  /stocks/:id} : delete the "id" stock.
     *
     * @param id the id of the stock to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stocks/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        log.debug("REST request to delete Stock : {}", id);
        stockService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/stocks?query=:query} : search for the stock corresponding
     * to the query.
     *
     * @param query the query of the stock search.
     * @return the result of the search.
     */
    @GetMapping("/_search/stocks")
    public List<Stock> searchStocks(@RequestParam String query) {
        log.debug("REST request to search Stocks for query {}", query);
        return stockService.search(query);
    }
}
