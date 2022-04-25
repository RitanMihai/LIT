package com.ritan.lit.portfolio.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.portfolio.IntegrationTest;
import com.ritan.lit.portfolio.domain.Order;
import com.ritan.lit.portfolio.domain.enumeration.OrderType;
import com.ritan.lit.portfolio.domain.enumeration.PositionType;
import com.ritan.lit.portfolio.repository.OrderRepository;
import com.ritan.lit.portfolio.repository.search.OrderSearchRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrderResourceIT {

    private static final Double DEFAULT_QUANTITY = 1D;
    private static final Double UPDATED_QUANTITY = 2D;

    private static final Double DEFAULT_SHARE_PRICE = 1D;
    private static final Double UPDATED_SHARE_PRICE = 2D;

    private static final OrderType DEFAULT_TYPE = OrderType.BUY;
    private static final OrderType UPDATED_TYPE = OrderType.SELL;

    private static final PositionType DEFAULT_POSITION = PositionType.OPEN;
    private static final PositionType UPDATED_POSITION = PositionType.CLOSED;

    private static final Instant DEFAULT_SUBBMITED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBBMITED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FILLED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FILLED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Double DEFAULT_TOTAL = 1D;
    private static final Double UPDATED_TOTAL = 2D;

    private static final Double DEFAULT_TAXES = 1D;
    private static final Double UPDATED_TAXES = 2D;

    private static final Double DEFAULT_STOP_LOSS = 1D;
    private static final Double UPDATED_STOP_LOSS = 2D;

    private static final Double DEFAULT_TAKE_PROFIT = 1D;
    private static final Double UPDATED_TAKE_PROFIT = 2D;

    private static final Integer DEFAULT_LEVERAGE = 1;
    private static final Integer UPDATED_LEVERAGE = 2;

    private static final Double DEFAULT_EXCHANGE_RATE = 1D;
    private static final Double UPDATED_EXCHANGE_RATE = 2D;

    private static final Boolean DEFAULT_IS_CFD = false;
    private static final Boolean UPDATED_IS_CFD = true;

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/orders";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderRepository orderRepository;

    /**
     * This repository is mocked in the com.ritan.lit.portfolio.repository.search test package.
     *
     * @see com.ritan.lit.portfolio.repository.search.OrderSearchRepositoryMockConfiguration
     */
    @Autowired
    private OrderSearchRepository mockOrderSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderMockMvc;

    private Order order;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createEntity(EntityManager em) {
        Order order = new Order()
            .quantity(DEFAULT_QUANTITY)
            .sharePrice(DEFAULT_SHARE_PRICE)
            .type(DEFAULT_TYPE)
            .position(DEFAULT_POSITION)
            .subbmitedDate(DEFAULT_SUBBMITED_DATE)
            .filledDate(DEFAULT_FILLED_DATE)
            .notes(DEFAULT_NOTES)
            .total(DEFAULT_TOTAL)
            .taxes(DEFAULT_TAXES)
            .stopLoss(DEFAULT_STOP_LOSS)
            .takeProfit(DEFAULT_TAKE_PROFIT)
            .leverage(DEFAULT_LEVERAGE)
            .exchangeRate(DEFAULT_EXCHANGE_RATE)
            .isCFD(DEFAULT_IS_CFD);
        return order;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createUpdatedEntity(EntityManager em) {
        Order order = new Order()
            .quantity(UPDATED_QUANTITY)
            .sharePrice(UPDATED_SHARE_PRICE)
            .type(UPDATED_TYPE)
            .position(UPDATED_POSITION)
            .subbmitedDate(UPDATED_SUBBMITED_DATE)
            .filledDate(UPDATED_FILLED_DATE)
            .notes(UPDATED_NOTES)
            .total(UPDATED_TOTAL)
            .taxes(UPDATED_TAXES)
            .stopLoss(UPDATED_STOP_LOSS)
            .takeProfit(UPDATED_TAKE_PROFIT)
            .leverage(UPDATED_LEVERAGE)
            .exchangeRate(UPDATED_EXCHANGE_RATE)
            .isCFD(UPDATED_IS_CFD);
        return order;
    }

    @BeforeEach
    public void initTest() {
        order = createEntity(em);
    }

    @Test
    @Transactional
    void createOrder() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();
        // Create the Order
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testOrder.getSharePrice()).isEqualTo(DEFAULT_SHARE_PRICE);
        assertThat(testOrder.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testOrder.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testOrder.getSubbmitedDate()).isEqualTo(DEFAULT_SUBBMITED_DATE);
        assertThat(testOrder.getFilledDate()).isEqualTo(DEFAULT_FILLED_DATE);
        assertThat(testOrder.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testOrder.getTotal()).isEqualTo(DEFAULT_TOTAL);
        assertThat(testOrder.getTaxes()).isEqualTo(DEFAULT_TAXES);
        assertThat(testOrder.getStopLoss()).isEqualTo(DEFAULT_STOP_LOSS);
        assertThat(testOrder.getTakeProfit()).isEqualTo(DEFAULT_TAKE_PROFIT);
        assertThat(testOrder.getLeverage()).isEqualTo(DEFAULT_LEVERAGE);
        assertThat(testOrder.getExchangeRate()).isEqualTo(DEFAULT_EXCHANGE_RATE);
        assertThat(testOrder.getIsCFD()).isEqualTo(DEFAULT_IS_CFD);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(1)).save(testOrder);
    }

    @Test
    @Transactional
    void createOrderWithExistingId() throws Exception {
        // Create the Order with an existing ID
        order.setId(1L);

        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(0)).save(order);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setQuantity(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSharePriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setSharePrice(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setType(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPositionIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setPosition(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.doubleValue())))
            .andExpect(jsonPath("$.[*].sharePrice").value(hasItem(DEFAULT_SHARE_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION.toString())))
            .andExpect(jsonPath("$.[*].subbmitedDate").value(hasItem(DEFAULT_SUBBMITED_DATE.toString())))
            .andExpect(jsonPath("$.[*].filledDate").value(hasItem(DEFAULT_FILLED_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].taxes").value(hasItem(DEFAULT_TAXES.doubleValue())))
            .andExpect(jsonPath("$.[*].stopLoss").value(hasItem(DEFAULT_STOP_LOSS.doubleValue())))
            .andExpect(jsonPath("$.[*].takeProfit").value(hasItem(DEFAULT_TAKE_PROFIT.doubleValue())))
            .andExpect(jsonPath("$.[*].leverage").value(hasItem(DEFAULT_LEVERAGE)))
            .andExpect(jsonPath("$.[*].exchangeRate").value(hasItem(DEFAULT_EXCHANGE_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].isCFD").value(hasItem(DEFAULT_IS_CFD.booleanValue())));
    }

    @Test
    @Transactional
    void getOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.doubleValue()))
            .andExpect(jsonPath("$.sharePrice").value(DEFAULT_SHARE_PRICE.doubleValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION.toString()))
            .andExpect(jsonPath("$.subbmitedDate").value(DEFAULT_SUBBMITED_DATE.toString()))
            .andExpect(jsonPath("$.filledDate").value(DEFAULT_FILLED_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.taxes").value(DEFAULT_TAXES.doubleValue()))
            .andExpect(jsonPath("$.stopLoss").value(DEFAULT_STOP_LOSS.doubleValue()))
            .andExpect(jsonPath("$.takeProfit").value(DEFAULT_TAKE_PROFIT.doubleValue()))
            .andExpect(jsonPath("$.leverage").value(DEFAULT_LEVERAGE))
            .andExpect(jsonPath("$.exchangeRate").value(DEFAULT_EXCHANGE_RATE.doubleValue()))
            .andExpect(jsonPath("$.isCFD").value(DEFAULT_IS_CFD.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingOrder() throws Exception {
        // Get the order
        restOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order
        Order updatedOrder = orderRepository.findById(order.getId()).get();
        // Disconnect from session so that the updates on updatedOrder are not directly saved in db
        em.detach(updatedOrder);
        updatedOrder
            .quantity(UPDATED_QUANTITY)
            .sharePrice(UPDATED_SHARE_PRICE)
            .type(UPDATED_TYPE)
            .position(UPDATED_POSITION)
            .subbmitedDate(UPDATED_SUBBMITED_DATE)
            .filledDate(UPDATED_FILLED_DATE)
            .notes(UPDATED_NOTES)
            .total(UPDATED_TOTAL)
            .taxes(UPDATED_TAXES)
            .stopLoss(UPDATED_STOP_LOSS)
            .takeProfit(UPDATED_TAKE_PROFIT)
            .leverage(UPDATED_LEVERAGE)
            .exchangeRate(UPDATED_EXCHANGE_RATE)
            .isCFD(UPDATED_IS_CFD);

        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrder.getSharePrice()).isEqualTo(UPDATED_SHARE_PRICE);
        assertThat(testOrder.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testOrder.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testOrder.getSubbmitedDate()).isEqualTo(UPDATED_SUBBMITED_DATE);
        assertThat(testOrder.getFilledDate()).isEqualTo(UPDATED_FILLED_DATE);
        assertThat(testOrder.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testOrder.getTotal()).isEqualTo(UPDATED_TOTAL);
        assertThat(testOrder.getTaxes()).isEqualTo(UPDATED_TAXES);
        assertThat(testOrder.getStopLoss()).isEqualTo(UPDATED_STOP_LOSS);
        assertThat(testOrder.getTakeProfit()).isEqualTo(UPDATED_TAKE_PROFIT);
        assertThat(testOrder.getLeverage()).isEqualTo(UPDATED_LEVERAGE);
        assertThat(testOrder.getExchangeRate()).isEqualTo(UPDATED_EXCHANGE_RATE);
        assertThat(testOrder.getIsCFD()).isEqualTo(UPDATED_IS_CFD);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository).save(testOrder);
    }

    @Test
    @Transactional
    void putNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, order.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(0)).save(order);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(0)).save(order);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(0)).save(order);
    }

    @Test
    @Transactional
    void partialUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder
            .quantity(UPDATED_QUANTITY)
            .type(UPDATED_TYPE)
            .position(UPDATED_POSITION)
            .subbmitedDate(UPDATED_SUBBMITED_DATE)
            .filledDate(UPDATED_FILLED_DATE)
            .notes(UPDATED_NOTES)
            .total(UPDATED_TOTAL)
            .taxes(UPDATED_TAXES)
            .stopLoss(UPDATED_STOP_LOSS)
            .leverage(UPDATED_LEVERAGE)
            .isCFD(UPDATED_IS_CFD);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrder.getSharePrice()).isEqualTo(DEFAULT_SHARE_PRICE);
        assertThat(testOrder.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testOrder.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testOrder.getSubbmitedDate()).isEqualTo(UPDATED_SUBBMITED_DATE);
        assertThat(testOrder.getFilledDate()).isEqualTo(UPDATED_FILLED_DATE);
        assertThat(testOrder.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testOrder.getTotal()).isEqualTo(UPDATED_TOTAL);
        assertThat(testOrder.getTaxes()).isEqualTo(UPDATED_TAXES);
        assertThat(testOrder.getStopLoss()).isEqualTo(UPDATED_STOP_LOSS);
        assertThat(testOrder.getTakeProfit()).isEqualTo(DEFAULT_TAKE_PROFIT);
        assertThat(testOrder.getLeverage()).isEqualTo(UPDATED_LEVERAGE);
        assertThat(testOrder.getExchangeRate()).isEqualTo(DEFAULT_EXCHANGE_RATE);
        assertThat(testOrder.getIsCFD()).isEqualTo(UPDATED_IS_CFD);
    }

    @Test
    @Transactional
    void fullUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder
            .quantity(UPDATED_QUANTITY)
            .sharePrice(UPDATED_SHARE_PRICE)
            .type(UPDATED_TYPE)
            .position(UPDATED_POSITION)
            .subbmitedDate(UPDATED_SUBBMITED_DATE)
            .filledDate(UPDATED_FILLED_DATE)
            .notes(UPDATED_NOTES)
            .total(UPDATED_TOTAL)
            .taxes(UPDATED_TAXES)
            .stopLoss(UPDATED_STOP_LOSS)
            .takeProfit(UPDATED_TAKE_PROFIT)
            .leverage(UPDATED_LEVERAGE)
            .exchangeRate(UPDATED_EXCHANGE_RATE)
            .isCFD(UPDATED_IS_CFD);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrder.getSharePrice()).isEqualTo(UPDATED_SHARE_PRICE);
        assertThat(testOrder.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testOrder.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testOrder.getSubbmitedDate()).isEqualTo(UPDATED_SUBBMITED_DATE);
        assertThat(testOrder.getFilledDate()).isEqualTo(UPDATED_FILLED_DATE);
        assertThat(testOrder.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testOrder.getTotal()).isEqualTo(UPDATED_TOTAL);
        assertThat(testOrder.getTaxes()).isEqualTo(UPDATED_TAXES);
        assertThat(testOrder.getStopLoss()).isEqualTo(UPDATED_STOP_LOSS);
        assertThat(testOrder.getTakeProfit()).isEqualTo(UPDATED_TAKE_PROFIT);
        assertThat(testOrder.getLeverage()).isEqualTo(UPDATED_LEVERAGE);
        assertThat(testOrder.getExchangeRate()).isEqualTo(UPDATED_EXCHANGE_RATE);
        assertThat(testOrder.getIsCFD()).isEqualTo(UPDATED_IS_CFD);
    }

    @Test
    @Transactional
    void patchNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, order.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(0)).save(order);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(0)).save(order);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(0)).save(order);
    }

    @Test
    @Transactional
    void deleteOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Delete the order
        restOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, order.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(1)).deleteById(order.getId());
    }

    @Test
    @Transactional
    void searchOrder() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        orderRepository.saveAndFlush(order);
        when(mockOrderSearchRepository.search("id:" + order.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(order), PageRequest.of(0, 1), 1));

        // Search the order
        restOrderMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.doubleValue())))
            .andExpect(jsonPath("$.[*].sharePrice").value(hasItem(DEFAULT_SHARE_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION.toString())))
            .andExpect(jsonPath("$.[*].subbmitedDate").value(hasItem(DEFAULT_SUBBMITED_DATE.toString())))
            .andExpect(jsonPath("$.[*].filledDate").value(hasItem(DEFAULT_FILLED_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].taxes").value(hasItem(DEFAULT_TAXES.doubleValue())))
            .andExpect(jsonPath("$.[*].stopLoss").value(hasItem(DEFAULT_STOP_LOSS.doubleValue())))
            .andExpect(jsonPath("$.[*].takeProfit").value(hasItem(DEFAULT_TAKE_PROFIT.doubleValue())))
            .andExpect(jsonPath("$.[*].leverage").value(hasItem(DEFAULT_LEVERAGE)))
            .andExpect(jsonPath("$.[*].exchangeRate").value(hasItem(DEFAULT_EXCHANGE_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].isCFD").value(hasItem(DEFAULT_IS_CFD.booleanValue())));
    }
}
