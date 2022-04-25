package com.ritan.lit.portfolio.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ritan.lit.portfolio.domain.PortfolioUser;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link PortfolioUser} entity.
 */
public interface PortfolioUserSearchRepository
    extends ElasticsearchRepository<PortfolioUser, Long>, PortfolioUserSearchRepositoryInternal {}

interface PortfolioUserSearchRepositoryInternal {
    Stream<PortfolioUser> search(String query);
}

class PortfolioUserSearchRepositoryInternalImpl implements PortfolioUserSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    PortfolioUserSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<PortfolioUser> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, PortfolioUser.class).map(SearchHit::getContent).stream();
    }
}
