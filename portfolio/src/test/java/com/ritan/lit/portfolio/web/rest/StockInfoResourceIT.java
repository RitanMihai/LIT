package com.ritan.lit.portfolio.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.portfolio.IntegrationTest;
import com.ritan.lit.portfolio.domain.StockInfo;
import com.ritan.lit.portfolio.repository.StockInfoRepository;
import com.ritan.lit.portfolio.repository.search.StockInfoSearchRepository;
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
 * Integration tests for the {@link StockInfoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockInfoResourceIT {

    private static final String DEFAULT_TICKER = "AAAAAAAAAA";
    private static final String UPDATED_TICKER = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_ISIN = "AAAAAAAAAA";
    private static final String UPDATED_ISIN = "BBBBBBBBBB";

    private static final Double DEFAULT_DIVIDEND_YIELD = 1D;
    private static final Double UPDATED_DIVIDEND_YIELD = 2D;

    private static final String DEFAULT_SECTOR = "AAAAAAAAAA";
    private static final String UPDATED_SECTOR = "BBBBBBBBBB";

    private static final String DEFAULT_INDUSTRY = "AAAAAAAAAA";
    private static final String UPDATED_INDUSTRY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stock-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/stock-infos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockInfoRepository stockInfoRepository;

    /**
     * This repository is mocked in the com.ritan.lit.portfolio.repository.search test package.
     *
     * @see com.ritan.lit.portfolio.repository.search.StockInfoSearchRepositoryMockConfiguration
     */
    @Autowired
    private StockInfoSearchRepository mockStockInfoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockInfoMockMvc;

    private StockInfo stockInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockInfo createEntity(EntityManager em) {
        StockInfo stockInfo = new StockInfo()
            .ticker(DEFAULT_TICKER)
            .name(DEFAULT_NAME)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .isin(DEFAULT_ISIN)
            .dividendYield(DEFAULT_DIVIDEND_YIELD)
            .sector(DEFAULT_SECTOR)
            .industry(DEFAULT_INDUSTRY);
        return stockInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockInfo createUpdatedEntity(EntityManager em) {
        StockInfo stockInfo = new StockInfo()
            .ticker(UPDATED_TICKER)
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .isin(UPDATED_ISIN)
            .dividendYield(UPDATED_DIVIDEND_YIELD)
            .sector(UPDATED_SECTOR)
            .industry(UPDATED_INDUSTRY);
        return stockInfo;
    }

    @BeforeEach
    public void initTest() {
        stockInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createStockInfo() throws Exception {
        int databaseSizeBeforeCreate = stockInfoRepository.findAll().size();
        // Create the StockInfo
        restStockInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockInfo)))
            .andExpect(status().isCreated());

        // Validate the StockInfo in the database
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeCreate + 1);
        StockInfo testStockInfo = stockInfoList.get(stockInfoList.size() - 1);
        assertThat(testStockInfo.getTicker()).isEqualTo(DEFAULT_TICKER);
        assertThat(testStockInfo.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStockInfo.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testStockInfo.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testStockInfo.getIsin()).isEqualTo(DEFAULT_ISIN);
        assertThat(testStockInfo.getDividendYield()).isEqualTo(DEFAULT_DIVIDEND_YIELD);
        assertThat(testStockInfo.getSector()).isEqualTo(DEFAULT_SECTOR);
        assertThat(testStockInfo.getIndustry()).isEqualTo(DEFAULT_INDUSTRY);

        // Validate the StockInfo in Elasticsearch
        verify(mockStockInfoSearchRepository, times(1)).save(testStockInfo);
    }

    @Test
    @Transactional
    void createStockInfoWithExistingId() throws Exception {
        // Create the StockInfo with an existing ID
        stockInfo.setId(1L);

        int databaseSizeBeforeCreate = stockInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockInfo)))
            .andExpect(status().isBadRequest());

        // Validate the StockInfo in the database
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeCreate);

        // Validate the StockInfo in Elasticsearch
        verify(mockStockInfoSearchRepository, times(0)).save(stockInfo);
    }

    @Test
    @Transactional
    void getAllStockInfos() throws Exception {
        // Initialize the database
        stockInfoRepository.saveAndFlush(stockInfo);

        // Get all the stockInfoList
        restStockInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticker").value(hasItem(DEFAULT_TICKER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].isin").value(hasItem(DEFAULT_ISIN)))
            .andExpect(jsonPath("$.[*].dividendYield").value(hasItem(DEFAULT_DIVIDEND_YIELD.doubleValue())))
            .andExpect(jsonPath("$.[*].sector").value(hasItem(DEFAULT_SECTOR)))
            .andExpect(jsonPath("$.[*].industry").value(hasItem(DEFAULT_INDUSTRY)));
    }

    @Test
    @Transactional
    void getStockInfo() throws Exception {
        // Initialize the database
        stockInfoRepository.saveAndFlush(stockInfo);

        // Get the stockInfo
        restStockInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, stockInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockInfo.getId().intValue()))
            .andExpect(jsonPath("$.ticker").value(DEFAULT_TICKER))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.isin").value(DEFAULT_ISIN))
            .andExpect(jsonPath("$.dividendYield").value(DEFAULT_DIVIDEND_YIELD.doubleValue()))
            .andExpect(jsonPath("$.sector").value(DEFAULT_SECTOR))
            .andExpect(jsonPath("$.industry").value(DEFAULT_INDUSTRY));
    }

    @Test
    @Transactional
    void getNonExistingStockInfo() throws Exception {
        // Get the stockInfo
        restStockInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewStockInfo() throws Exception {
        // Initialize the database
        stockInfoRepository.saveAndFlush(stockInfo);

        int databaseSizeBeforeUpdate = stockInfoRepository.findAll().size();

        // Update the stockInfo
        StockInfo updatedStockInfo = stockInfoRepository.findById(stockInfo.getId()).get();
        // Disconnect from session so that the updates on updatedStockInfo are not directly saved in db
        em.detach(updatedStockInfo);
        updatedStockInfo
            .ticker(UPDATED_TICKER)
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .isin(UPDATED_ISIN)
            .dividendYield(UPDATED_DIVIDEND_YIELD)
            .sector(UPDATED_SECTOR)
            .industry(UPDATED_INDUSTRY);

        restStockInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStockInfo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStockInfo))
            )
            .andExpect(status().isOk());

        // Validate the StockInfo in the database
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeUpdate);
        StockInfo testStockInfo = stockInfoList.get(stockInfoList.size() - 1);
        assertThat(testStockInfo.getTicker()).isEqualTo(UPDATED_TICKER);
        assertThat(testStockInfo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStockInfo.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testStockInfo.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testStockInfo.getIsin()).isEqualTo(UPDATED_ISIN);
        assertThat(testStockInfo.getDividendYield()).isEqualTo(UPDATED_DIVIDEND_YIELD);
        assertThat(testStockInfo.getSector()).isEqualTo(UPDATED_SECTOR);
        assertThat(testStockInfo.getIndustry()).isEqualTo(UPDATED_INDUSTRY);

        // Validate the StockInfo in Elasticsearch
        verify(mockStockInfoSearchRepository).save(testStockInfo);
    }

    @Test
    @Transactional
    void putNonExistingStockInfo() throws Exception {
        int databaseSizeBeforeUpdate = stockInfoRepository.findAll().size();
        stockInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockInfo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockInfo in the database
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockInfo in Elasticsearch
        verify(mockStockInfoSearchRepository, times(0)).save(stockInfo);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockInfo() throws Exception {
        int databaseSizeBeforeUpdate = stockInfoRepository.findAll().size();
        stockInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockInfo in the database
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockInfo in Elasticsearch
        verify(mockStockInfoSearchRepository, times(0)).save(stockInfo);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockInfo() throws Exception {
        int databaseSizeBeforeUpdate = stockInfoRepository.findAll().size();
        stockInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockInfoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockInfo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockInfo in the database
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockInfo in Elasticsearch
        verify(mockStockInfoSearchRepository, times(0)).save(stockInfo);
    }

    @Test
    @Transactional
    void partialUpdateStockInfoWithPatch() throws Exception {
        // Initialize the database
        stockInfoRepository.saveAndFlush(stockInfo);

        int databaseSizeBeforeUpdate = stockInfoRepository.findAll().size();

        // Update the stockInfo using partial update
        StockInfo partialUpdatedStockInfo = new StockInfo();
        partialUpdatedStockInfo.setId(stockInfo.getId());

        partialUpdatedStockInfo.ticker(UPDATED_TICKER).name(UPDATED_NAME).dividendYield(UPDATED_DIVIDEND_YIELD);

        restStockInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockInfo))
            )
            .andExpect(status().isOk());

        // Validate the StockInfo in the database
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeUpdate);
        StockInfo testStockInfo = stockInfoList.get(stockInfoList.size() - 1);
        assertThat(testStockInfo.getTicker()).isEqualTo(UPDATED_TICKER);
        assertThat(testStockInfo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStockInfo.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testStockInfo.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testStockInfo.getIsin()).isEqualTo(DEFAULT_ISIN);
        assertThat(testStockInfo.getDividendYield()).isEqualTo(UPDATED_DIVIDEND_YIELD);
        assertThat(testStockInfo.getSector()).isEqualTo(DEFAULT_SECTOR);
        assertThat(testStockInfo.getIndustry()).isEqualTo(DEFAULT_INDUSTRY);
    }

    @Test
    @Transactional
    void fullUpdateStockInfoWithPatch() throws Exception {
        // Initialize the database
        stockInfoRepository.saveAndFlush(stockInfo);

        int databaseSizeBeforeUpdate = stockInfoRepository.findAll().size();

        // Update the stockInfo using partial update
        StockInfo partialUpdatedStockInfo = new StockInfo();
        partialUpdatedStockInfo.setId(stockInfo.getId());

        partialUpdatedStockInfo
            .ticker(UPDATED_TICKER)
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .isin(UPDATED_ISIN)
            .dividendYield(UPDATED_DIVIDEND_YIELD)
            .sector(UPDATED_SECTOR)
            .industry(UPDATED_INDUSTRY);

        restStockInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockInfo))
            )
            .andExpect(status().isOk());

        // Validate the StockInfo in the database
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeUpdate);
        StockInfo testStockInfo = stockInfoList.get(stockInfoList.size() - 1);
        assertThat(testStockInfo.getTicker()).isEqualTo(UPDATED_TICKER);
        assertThat(testStockInfo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStockInfo.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testStockInfo.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testStockInfo.getIsin()).isEqualTo(UPDATED_ISIN);
        assertThat(testStockInfo.getDividendYield()).isEqualTo(UPDATED_DIVIDEND_YIELD);
        assertThat(testStockInfo.getSector()).isEqualTo(UPDATED_SECTOR);
        assertThat(testStockInfo.getIndustry()).isEqualTo(UPDATED_INDUSTRY);
    }

    @Test
    @Transactional
    void patchNonExistingStockInfo() throws Exception {
        int databaseSizeBeforeUpdate = stockInfoRepository.findAll().size();
        stockInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockInfo in the database
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockInfo in Elasticsearch
        verify(mockStockInfoSearchRepository, times(0)).save(stockInfo);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockInfo() throws Exception {
        int databaseSizeBeforeUpdate = stockInfoRepository.findAll().size();
        stockInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockInfo in the database
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockInfo in Elasticsearch
        verify(mockStockInfoSearchRepository, times(0)).save(stockInfo);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockInfo() throws Exception {
        int databaseSizeBeforeUpdate = stockInfoRepository.findAll().size();
        stockInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockInfoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(stockInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockInfo in the database
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StockInfo in Elasticsearch
        verify(mockStockInfoSearchRepository, times(0)).save(stockInfo);
    }

    @Test
    @Transactional
    void deleteStockInfo() throws Exception {
        // Initialize the database
        stockInfoRepository.saveAndFlush(stockInfo);

        int databaseSizeBeforeDelete = stockInfoRepository.findAll().size();

        // Delete the stockInfo
        restStockInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockInfo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StockInfo> stockInfoList = stockInfoRepository.findAll();
        assertThat(stockInfoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the StockInfo in Elasticsearch
        verify(mockStockInfoSearchRepository, times(1)).deleteById(stockInfo.getId());
    }

    @Test
    @Transactional
    void searchStockInfo() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        stockInfoRepository.saveAndFlush(stockInfo);
        when(mockStockInfoSearchRepository.search("id:" + stockInfo.getId())).thenReturn(Stream.of(stockInfo));

        // Search the stockInfo
        restStockInfoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + stockInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticker").value(hasItem(DEFAULT_TICKER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].isin").value(hasItem(DEFAULT_ISIN)))
            .andExpect(jsonPath("$.[*].dividendYield").value(hasItem(DEFAULT_DIVIDEND_YIELD.doubleValue())))
            .andExpect(jsonPath("$.[*].sector").value(hasItem(DEFAULT_SECTOR)))
            .andExpect(jsonPath("$.[*].industry").value(hasItem(DEFAULT_INDUSTRY)));
    }
}
