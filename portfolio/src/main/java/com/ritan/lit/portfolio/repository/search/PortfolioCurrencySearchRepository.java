package com.ritan.lit.portfolio.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ritan.lit.portfolio.domain.PortfolioCurrency;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link PortfolioCurrency} entity.
 */
public interface PortfolioCurrencySearchRepository
    extends ElasticsearchRepository<PortfolioCurrency, Long>, PortfolioCurrencySearchRepositoryInternal {}

interface PortfolioCurrencySearchRepositoryInternal {
    Stream<PortfolioCurrency> search(String query);
}

class PortfolioCurrencySearchRepositoryInternalImpl implements PortfolioCurrencySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    PortfolioCurrencySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<PortfolioCurrency> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, PortfolioCurrency.class).map(SearchHit::getContent).stream();
    }
}
