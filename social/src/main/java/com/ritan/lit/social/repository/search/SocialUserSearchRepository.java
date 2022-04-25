package com.ritan.lit.social.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ritan.lit.social.domain.SocialUser;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link SocialUser} entity.
 */
public interface SocialUserSearchRepository extends ElasticsearchRepository<SocialUser, Long>, SocialUserSearchRepositoryInternal {}

interface SocialUserSearchRepositoryInternal {
    Stream<SocialUser> search(String query);
}

class SocialUserSearchRepositoryInternalImpl implements SocialUserSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    SocialUserSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<SocialUser> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, SocialUser.class).map(SearchHit::getContent).stream();
    }
}
