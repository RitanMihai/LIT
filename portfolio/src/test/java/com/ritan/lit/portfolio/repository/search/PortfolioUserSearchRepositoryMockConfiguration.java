package com.ritan.lit.portfolio.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PortfolioUserSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PortfolioUserSearchRepositoryMockConfiguration {

    @MockBean
    private PortfolioUserSearchRepository mockPortfolioUserSearchRepository;
}
