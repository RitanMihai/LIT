package com.ritan.lit.watchlist.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link StockExchangeSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class StockExchangeSearchRepositoryMockConfiguration {

    @MockBean
    private StockExchangeSearchRepository mockStockExchangeSearchRepository;
}
