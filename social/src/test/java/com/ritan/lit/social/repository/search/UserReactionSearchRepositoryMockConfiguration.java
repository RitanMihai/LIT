package com.ritan.lit.social.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link UserReactionSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class UserReactionSearchRepositoryMockConfiguration {

    @MockBean
    private UserReactionSearchRepository mockUserReactionSearchRepository;
}
