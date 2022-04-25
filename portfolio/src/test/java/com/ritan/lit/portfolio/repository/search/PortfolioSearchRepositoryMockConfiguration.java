package com.ritan.lit.portfolio.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PortfolioSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PortfolioSearchRepositoryMockConfiguration {

    @MockBean
    private PortfolioSearchRepository mockPortfolioSearchRepository;
}
