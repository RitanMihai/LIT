package com.ritan.lit.watchlist.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link StockSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class StockSearchRepositoryMockConfiguration {

    @MockBean
    private StockSearchRepository mockStockSearchRepository;
}
