package com.ritan.lit.portfolio.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link DividendSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class DividendSearchRepositoryMockConfiguration {

    @MockBean
    private DividendSearchRepository mockDividendSearchRepository;
}
