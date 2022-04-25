package com.ritan.lit.social.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ritan.lit.social.domain.UserFollowing;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link UserFollowing} entity.
 */
public interface UserFollowingSearchRepository
    extends ElasticsearchRepository<UserFollowing, Long>, UserFollowingSearchRepositoryInternal {}

interface UserFollowingSearchRepositoryInternal {
    Stream<UserFollowing> search(String query);
}

class UserFollowingSearchRepositoryInternalImpl implements UserFollowingSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    UserFollowingSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<UserFollowing> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, UserFollowing.class).map(SearchHit::getContent).stream();
    }
}
