package com.ritan.lit.watchlist.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ritan.lit.watchlist.domain.StockExchange;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link StockExchange} entity.
 */
public interface StockExchangeSearchRepository
    extends ElasticsearchRepository<StockExchange, Long>, StockExchangeSearchRepositoryInternal {}

interface StockExchangeSearchRepositoryInternal {
    Stream<StockExchange> search(String query);
}

class StockExchangeSearchRepositoryInternalImpl implements StockExchangeSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    StockExchangeSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<StockExchange> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, StockExchange.class).map(SearchHit::getContent).stream();
    }
}
