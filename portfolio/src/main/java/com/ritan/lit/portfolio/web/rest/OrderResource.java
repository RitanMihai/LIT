package com.ritan.lit.portfolio.web.rest;

import com.ritan.lit.portfolio.domain.Order;
import com.ritan.lit.portfolio.domain.StockInfo;
import com.ritan.lit.portfolio.domain.services.DateDoublePair;
import com.ritan.lit.portfolio.domain.util.UserOrders;
import com.ritan.lit.portfolio.repository.OrderRepository;
import com.ritan.lit.portfolio.security.jwt.TokenProvider;
import com.ritan.lit.portfolio.service.OrderService;
import com.ritan.lit.portfolio.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.ritan.lit.portfolio.web.rest.serviceClient.WatcherClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ritan.lit.portfolio.domain.Order}.
 */
@RestController
@RequestMapping("/api")
public class OrderResource {

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);

    private static final String ENTITY_NAME = "portfolioOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderService orderService;

    private final OrderRepository orderRepository;
    private final TokenProvider tokenProvider;

    public OrderResource(OrderService orderService, OrderRepository orderRepository, TokenProvider tokenProvider) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.tokenProvider = tokenProvider;
    }

    /**
     * {@code POST  /orders} : Create a new order.
     *
     * @param order the order to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new order, or with status {@code 400 (Bad Request)} if the order has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to save Order : {}", order);
        if (order.getId() != null) {
            throw new BadRequestAlertException("A new order cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Order result = orderService.save(order);
        return ResponseEntity
            .created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @GetMapping("/orders-details/user/{user}/pl")
    public ResponseEntity<?> getAllOrdersByUserPL (
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @PathVariable String user) throws ExecutionException, InterruptedException {

        WatcherClient watcherClient = new WatcherClient(this.tokenProvider);
        Page<Order> page = orderService.findAllByUser(pageable, user);

        Map<LocalDate, Double> overallProfitLose = new HashMap<>();

        for (Order order : page) {
            StockInfo stockInfo = order.getStockInfo();
            Instant filledDate = order.getFilledDate();
            Double nrOfShare = order.getQuantity();
            DateDoublePair[] history = watcherClient.getHistory(stockInfo.getTicker(), filledDate);

            for (DateDoublePair dateDoublePair : history) {
                LocalDate date = dateDoublePair.getDate();
                Double value = dateDoublePair.getValue() * nrOfShare;

                if(overallProfitLose.containsKey(date)) {
                    Double addedValue = overallProfitLose.get(date) + value;
                    overallProfitLose.put(date, addedValue);
                } else overallProfitLose.put(date,value);
            }
        }

        /* */
        List<DateDoublePair> collect = overallProfitLose.entrySet()
            .stream()
            .map(e -> new DateDoublePair(e.getKey(), e.getValue()))
            .sorted()
            .collect(Collectors.toList());

        return ResponseEntity.ok(collect);
    }
    /**
     * {@code PUT  /orders/:id} : Updates an existing order.
     *
     * @param id the id of the order to save.
     * @param order the order to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated order,
     * or with status {@code 400 (Bad Request)} if the order is not valid,
     * or with status {@code 500 (Internal Server Error)} if the order couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Order order)
        throws URISyntaxException {
        log.debug("REST request to update Order : {}, {}", id, order);
        if (order.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, order.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Order result = orderService.save(order);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, order.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /orders/:id} : Partial updates given fields of an existing order, field will ignore if it is null
     *
     * @param id the id of the order to save.
     * @param order the order to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated order,
     * or with status {@code 400 (Bad Request)} if the order is not valid,
     * or with status {@code 404 (Not Found)} if the order is not found,
     * or with status {@code 500 (Internal Server Error)} if the order couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Order> partialUpdateOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Order order
    ) throws URISyntaxException {
        log.debug("REST request to partial update Order partially : {}, {}", id, order);
        if (order.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, order.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Order> result = orderService.partialUpdate(order);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, order.getId().toString())
        );
    }

    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Orders");
        Page<Order> page = orderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/orders/user/{user}")
    public ResponseEntity<List<Order>> getAllOrdersByUser(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @PathVariable String user) {
        log.debug("REST request to get a page of Orders");
        Page<Order> page = orderService.findAllByUser(pageable, user);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/orders/user/{user}/portfolio/{portfolio}")
    public ResponseEntity<List<Order>> getAllOrdersByUserAndPortfolio(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @PathVariable String user,
        @PathVariable String portfolio) {
        log.debug("REST request to get a page of Orders");
        Page<Order> page = orderService.findAllByUserAndPortfolio(pageable, user, portfolio);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/orders-details/user/{user}")
    public ResponseEntity<List<UserOrders>> getAllOrdersDetailsByUser(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @PathVariable String user) {
        log.debug("REST request to get a page of Orders");
        Page<Object[]> page = orderService.findAllOrderDetailsByUser(pageable, user);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        List<UserOrders> userOrdersList = new ArrayList<>();
        for (Object[] objects : page) {
            UserOrders userOrders = new UserOrders();
            userOrders.setDate((Instant) objects[0]);
            userOrders.setSum((Double) objects[1]);
            userOrdersList.add(userOrders);
        }

        return ResponseEntity.ok().headers(headers).body(userOrdersList);
    }

    /**
     * {@code GET  /orders/:id} : get the "id" order.
     *
     * @param id the id of the order to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the order, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        log.debug("REST request to get Order : {}", id);
        Optional<Order> order = orderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(order);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" order.
     *
     * @param id the id of the order to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.debug("REST request to delete Order : {}", id);
        orderService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/orders?query=:query} : search for the order corresponding
     * to the query.
     *
     * @param query the query of the order search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
/*    @GetMapping("/_search/orders")
    public ResponseEntity<List<Order>> searchOrders(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Orders for query {}", query);
        Page<Order> page = orderService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }*/
}
