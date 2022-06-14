package com.ritan.lit.watchlist.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.watchlist.IntegrationTest;
import com.ritan.lit.watchlist.domain.Stock;
import com.ritan.lit.watchlist.domain.enumeration.StockType;
import com.ritan.lit.watchlist.repository.StockRepository;
import com.ritan.lit.watchlist.repository.search.StockSearchRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link StockResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockResourceIT {

    private static final String DEFAULT_TICKER = "AAAAAAAAAA";
    private static final String UPDATED_TICKER = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final Long DEFAULT_MARKET_CAP = 0L;
    private static final Long UPDATED_MARKET_CAP = 0L;

    private static final Integer DEFAULT_VOLUME = 1;
    private static final Integer UPDATED_VOLUME = 2;

    private static final Double DEFAULT_PE_RATION = 1D;
    private static final Double UPDATED_PE_RATION = 2D;

    private static final LocalDate DEFAULT_IPO_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_IPO_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_ISIN = "AAAAAAAAAA";
    private static final String UPDATED_ISIN = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DELISTED = false;
    private static final Boolean UPDATED_IS_DELISTED = true;

    private static final Boolean DEFAULT_HAS_DIVIDEND = false;
    private static final Boolean UPDATED_HAS_DIVIDEND = true;

    private static final StockType DEFAULT_TYPE = StockType.COMMON;
    private static final StockType UPDATED_TYPE = StockType.PREFERRED;

    private static final Double DEFAULT_DIVIDEND_YIELD = 1D;
    private static final Double UPDATED_DIVIDEND_YIELD = 2D;

    private static final String ENTITY_API_URL = "/api/stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/stocks";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockRepository stockRepository;

    /**
     * This repository is mocked in the com.ritan.lit.watchlist.repository.search test package.
     *
     * @see com.ritan.lit.watchlist.repository.search.StockSearchRepositoryMockConfiguration
     */
    @Autowired
    private StockSearchRepository mockStockSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockMockMvc;

    private Stock stock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createEntity(EntityManager em) {
        Stock stock = new Stock()
            .ticker(DEFAULT_TICKER)
            .name(DEFAULT_NAME)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .marketCap(DEFAULT_MARKET_CAP)
            .volume(DEFAULT_VOLUME)
            .peRation(DEFAULT_PE_RATION)
            .ipoDate(DEFAULT_IPO_DATE)
            .isin(DEFAULT_ISIN)
            .isDelisted(DEFAULT_IS_DELISTED)
            .hasDividend(DEFAULT_HAS_DIVIDEND)
            .type(DEFAULT_TYPE)
            .dividendYield(DEFAULT_DIVIDEND_YIELD);
        return stock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createUpdatedEntity(EntityManager em) {
        Stock stock = new Stock()
            .ticker(UPDATED_TICKER)
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .marketCap(UPDATED_MARKET_CAP)
            .volume(UPDATED_VOLUME)
            .peRation(UPDATED_PE_RATION)
            .ipoDate(UPDATED_IPO_DATE)
            .isin(UPDATED_ISIN)
            .isDelisted(UPDATED_IS_DELISTED)
            .hasDividend(UPDATED_HAS_DIVIDEND)
            .type(UPDATED_TYPE)
            .dividendYield(UPDATED_DIVIDEND_YIELD);
        return stock;
    }

    @BeforeEach
    public void initTest() {
        stock = createEntity(em);
    }

    @Test
    @Transactional
    void createStock() throws Exception {
        int databaseSizeBeforeCreate = stockRepository.findAll().size();
        // Create the Stock
        restStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isCreated());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeCreate + 1);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getTicker()).isEqualTo(DEFAULT_TICKER);
        assertThat(testStock.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStock.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testStock.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testStock.getMarketCap()).isEqualTo(DEFAULT_MARKET_CAP);
        assertThat(testStock.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testStock.getPeRation()).isEqualTo(DEFAULT_PE_RATION);
        assertThat(testStock.getIpoDate()).isEqualTo(DEFAULT_IPO_DATE);
        assertThat(testStock.getIsin()).isEqualTo(DEFAULT_ISIN);
        assertThat(testStock.getIsDelisted()).isEqualTo(DEFAULT_IS_DELISTED);
        assertThat(testStock.getHasDividend()).isEqualTo(DEFAULT_HAS_DIVIDEND);
        assertThat(testStock.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testStock.getDividendYield()).isEqualTo(DEFAULT_DIVIDEND_YIELD);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(1)).save(testStock);
    }

    @Test
    @Transactional
    void createStockWithExistingId() throws Exception {
        // Create the Stock with an existing ID
        stock.setId(1L);

        int databaseSizeBeforeCreate = stockRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeCreate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void getAllStocks() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stock.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticker").value(hasItem(DEFAULT_TICKER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].marketCap").value(hasItem(DEFAULT_MARKET_CAP)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME)))
            .andExpect(jsonPath("$.[*].peRation").value(hasItem(DEFAULT_PE_RATION.doubleValue())))
            .andExpect(jsonPath("$.[*].ipoDate").value(hasItem(DEFAULT_IPO_DATE.toString())))
            .andExpect(jsonPath("$.[*].isin").value(hasItem(DEFAULT_ISIN)))
            .andExpect(jsonPath("$.[*].isDelisted").value(hasItem(DEFAULT_IS_DELISTED.booleanValue())))
            .andExpect(jsonPath("$.[*].hasDividend").value(hasItem(DEFAULT_HAS_DIVIDEND.booleanValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].dividendYield").value(hasItem(DEFAULT_DIVIDEND_YIELD.doubleValue())));
    }

    @Test
    @Transactional
    void getStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get the stock
        restStockMockMvc
            .perform(get(ENTITY_API_URL_ID, stock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stock.getId().intValue()))
            .andExpect(jsonPath("$.ticker").value(DEFAULT_TICKER))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.marketCap").value(DEFAULT_MARKET_CAP))
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME))
            .andExpect(jsonPath("$.peRation").value(DEFAULT_PE_RATION.doubleValue()))
            .andExpect(jsonPath("$.ipoDate").value(DEFAULT_IPO_DATE.toString()))
            .andExpect(jsonPath("$.isin").value(DEFAULT_ISIN))
            .andExpect(jsonPath("$.isDelisted").value(DEFAULT_IS_DELISTED.booleanValue()))
            .andExpect(jsonPath("$.hasDividend").value(DEFAULT_HAS_DIVIDEND.booleanValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.dividendYield").value(DEFAULT_DIVIDEND_YIELD.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingStock() throws Exception {
        // Get the stock
        restStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock
        Stock updatedStock = stockRepository.findById(stock.getId()).get();
        // Disconnect from session so that the updates on updatedStock are not directly saved in db
        em.detach(updatedStock);
        updatedStock
            .ticker(UPDATED_TICKER)
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .marketCap(UPDATED_MARKET_CAP)
            .volume(UPDATED_VOLUME)
            .peRation(UPDATED_PE_RATION)
            .ipoDate(UPDATED_IPO_DATE)
            .isin(UPDATED_ISIN)
            .isDelisted(UPDATED_IS_DELISTED)
            .hasDividend(UPDATED_HAS_DIVIDEND)
            .type(UPDATED_TYPE)
            .dividendYield(UPDATED_DIVIDEND_YIELD);

        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStock))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getTicker()).isEqualTo(UPDATED_TICKER);
        assertThat(testStock.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStock.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testStock.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testStock.getMarketCap()).isEqualTo(UPDATED_MARKET_CAP);
        assertThat(testStock.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testStock.getPeRation()).isEqualTo(UPDATED_PE_RATION);
        assertThat(testStock.getIpoDate()).isEqualTo(UPDATED_IPO_DATE);
        assertThat(testStock.getIsin()).isEqualTo(UPDATED_ISIN);
        assertThat(testStock.getIsDelisted()).isEqualTo(UPDATED_IS_DELISTED);
        assertThat(testStock.getHasDividend()).isEqualTo(UPDATED_HAS_DIVIDEND);
        assertThat(testStock.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testStock.getDividendYield()).isEqualTo(UPDATED_DIVIDEND_YIELD);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository).save(testStock);
    }

    @Test
    @Transactional
    void putNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void putWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void partialUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock
            .ticker(UPDATED_TICKER)
            .name(UPDATED_NAME)
            .ipoDate(UPDATED_IPO_DATE)
            .hasDividend(UPDATED_HAS_DIVIDEND)
            .type(UPDATED_TYPE);

        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getTicker()).isEqualTo(UPDATED_TICKER);
        assertThat(testStock.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStock.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testStock.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testStock.getMarketCap()).isEqualTo(DEFAULT_MARKET_CAP);
        assertThat(testStock.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testStock.getPeRation()).isEqualTo(DEFAULT_PE_RATION);
        assertThat(testStock.getIpoDate()).isEqualTo(UPDATED_IPO_DATE);
        assertThat(testStock.getIsin()).isEqualTo(DEFAULT_ISIN);
        assertThat(testStock.getIsDelisted()).isEqualTo(DEFAULT_IS_DELISTED);
        assertThat(testStock.getHasDividend()).isEqualTo(UPDATED_HAS_DIVIDEND);
        assertThat(testStock.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testStock.getDividendYield()).isEqualTo(DEFAULT_DIVIDEND_YIELD);
    }

    @Test
    @Transactional
    void fullUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock
            .ticker(UPDATED_TICKER)
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .marketCap(UPDATED_MARKET_CAP)
            .volume(UPDATED_VOLUME)
            .peRation(UPDATED_PE_RATION)
            .ipoDate(UPDATED_IPO_DATE)
            .isin(UPDATED_ISIN)
            .isDelisted(UPDATED_IS_DELISTED)
            .hasDividend(UPDATED_HAS_DIVIDEND)
            .type(UPDATED_TYPE)
            .dividendYield(UPDATED_DIVIDEND_YIELD);

        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getTicker()).isEqualTo(UPDATED_TICKER);
        assertThat(testStock.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStock.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testStock.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testStock.getMarketCap()).isEqualTo(UPDATED_MARKET_CAP);
        assertThat(testStock.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testStock.getPeRation()).isEqualTo(UPDATED_PE_RATION);
        assertThat(testStock.getIpoDate()).isEqualTo(UPDATED_IPO_DATE);
        assertThat(testStock.getIsin()).isEqualTo(UPDATED_ISIN);
        assertThat(testStock.getIsDelisted()).isEqualTo(UPDATED_IS_DELISTED);
        assertThat(testStock.getHasDividend()).isEqualTo(UPDATED_HAS_DIVIDEND);
        assertThat(testStock.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testStock.getDividendYield()).isEqualTo(UPDATED_DIVIDEND_YIELD);
    }

    @Test
    @Transactional
    void patchNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stock.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void deleteStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeDelete = stockRepository.findAll().size();

        // Delete the stock
        restStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, stock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(1)).deleteById(stock.getId());
    }

    @Test
    @Transactional
    void searchStock() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        stockRepository.saveAndFlush(stock);
        when(mockStockSearchRepository.search("id:" + stock.getId())).thenReturn(Stream.of(stock));

        // Search the stock
        restStockMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + stock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stock.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticker").value(hasItem(DEFAULT_TICKER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].marketCap").value(hasItem(DEFAULT_MARKET_CAP)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME)))
            .andExpect(jsonPath("$.[*].peRation").value(hasItem(DEFAULT_PE_RATION.doubleValue())))
            .andExpect(jsonPath("$.[*].ipoDate").value(hasItem(DEFAULT_IPO_DATE.toString())))
            .andExpect(jsonPath("$.[*].isin").value(hasItem(DEFAULT_ISIN)))
            .andExpect(jsonPath("$.[*].isDelisted").value(hasItem(DEFAULT_IS_DELISTED.booleanValue())))
            .andExpect(jsonPath("$.[*].hasDividend").value(hasItem(DEFAULT_HAS_DIVIDEND.booleanValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].dividendYield").value(hasItem(DEFAULT_DIVIDEND_YIELD.doubleValue())));
    }
}
