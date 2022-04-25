package com.ritan.lit.watchlist.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link CapitalGainHistorySearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CapitalGainHistorySearchRepositoryMockConfiguration {

    @MockBean
    private CapitalGainHistorySearchRepository mockCapitalGainHistorySearchRepository;
}
