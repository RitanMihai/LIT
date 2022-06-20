package com.ritan.lit.portfolio.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.portfolio.domain.Order;
import com.ritan.lit.portfolio.domain.util.PortfolioOrderGroup;
import com.ritan.lit.portfolio.repository.OrderRepository;
import com.ritan.lit.portfolio.repository.search.OrderSearchRepository;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Order}.
 */
@Service
@Transactional
public class OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    //private final OrderSearchRepository orderSearchRepository;

    public OrderService(OrderRepository orderRepository/*, OrderSearchRepository orderSearchRepository*/) {
        this.orderRepository = orderRepository;
        //this.orderSearchRepository = orderSearchRepository;
    }

    /**
     * Save a order.
     *
     * @param order the entity to save.
     * @return the persisted entity.
     */
    public Order save(Order order) {
        log.debug("Request to save Order : {}", order);
        Order result = orderRepository.save(order);
        //orderSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a order.
     *
     * @param order the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Order> partialUpdate(Order order) {
        log.debug("Request to partially update Order : {}", order);

        return orderRepository
            .findById(order.getId())
            .map(existingOrder -> {
                if (order.getQuantity() != null) {
                    existingOrder.setQuantity(order.getQuantity());
                }
                if (order.getSharePrice() != null) {
                    existingOrder.setSharePrice(order.getSharePrice());
                }
                if (order.getType() != null) {
                    existingOrder.setType(order.getType());
                }
                if (order.getPosition() != null) {
                    existingOrder.setPosition(order.getPosition());
                }
                if (order.getSubbmitedDate() != null) {
                    existingOrder.setSubbmitedDate(order.getSubbmitedDate());
                }
                if (order.getFilledDate() != null) {
                    existingOrder.setFilledDate(order.getFilledDate());
                }
                if (order.getNotes() != null) {
                    existingOrder.setNotes(order.getNotes());
                }
                if (order.getTotal() != null) {
                    existingOrder.setTotal(order.getTotal());
                }
                if (order.getTaxes() != null) {
                    existingOrder.setTaxes(order.getTaxes());
                }
                if (order.getStopLoss() != null) {
                    existingOrder.setStopLoss(order.getStopLoss());
                }
                if (order.getTakeProfit() != null) {
                    existingOrder.setTakeProfit(order.getTakeProfit());
                }
                if (order.getLeverage() != null) {
                    existingOrder.setLeverage(order.getLeverage());
                }
                if (order.getExchangeRate() != null) {
                    existingOrder.setExchangeRate(order.getExchangeRate());
                }
                if (order.getIsCFD() != null) {
                    existingOrder.setIsCFD(order.getIsCFD());
                }

                return existingOrder;
            })
            .map(orderRepository::save);/*
            .map(savedOrder -> {
                orderSearchRepository.save(savedOrder);

                return savedOrder;
            });*/
    }

    /**
     * Get all the orders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Order> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return orderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Order> findAllByUser(Pageable pageable, String user) {
        log.debug("Request to get all Orders");
        return orderRepository.findAllByPortfolioUser(pageable, user);
    }

    public Page<Order> findAllByUserAndPortfolio(Pageable pageable, String user, String portfolio) {
        return orderRepository.findAllByPortfolioPortfolioUserAndPortfolio(pageable, user, portfolio);
    }

    public Page<Object[]> findAllOrderDetailsByUser(Pageable pageable, String user) {
        return orderRepository.findAllByPortfolioPortfolioUser(pageable, user);
    }

    /**
     * Get one order by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Order> findOne(Long id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findById(id);
    }

    /**
     * Delete the order by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
        //orderSearchRepository.deleteById(id);
    }

    public Optional<List<Object[]>> getAllPortfoliosWithDetails(String user, String portfolioName){
        return orderRepository.getAllPortfoliosWithDetails(user, portfolioName);
    }

    /**
     * Search for the order corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
 /*   @Transactional(readOnly = true)
    public Page<Order> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Orders for query {}", query);
        return orderSearchRepository.search(query, pageable);
    }*/

}
