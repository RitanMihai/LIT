package com.ritan.lit.watchlist.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link DividendHistorySearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class DividendHistorySearchRepositoryMockConfiguration {

    @MockBean
    private DividendHistorySearchRepository mockDividendHistorySearchRepository;
}
