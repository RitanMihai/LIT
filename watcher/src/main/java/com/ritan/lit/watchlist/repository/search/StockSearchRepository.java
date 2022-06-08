package com.ritan.lit.watchlist.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ritan.lit.watchlist.domain.Stock;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Stock} entity.
 */
public interface StockSearchRepository extends ElasticsearchRepository<Stock, Long>, StockSearchRepositoryInternal {}

interface StockSearchRepositoryInternal {
    Stream<Stock> search(String query);
}

class StockSearchRepositoryInternalImpl implements StockSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    StockSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Stock> search(String query) {
        System.out.println("QUERY " +  query);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));

        return elasticsearchTemplate.search(nativeSearchQuery, Stock.class).map(SearchHit::getContent).stream();
    }
}
