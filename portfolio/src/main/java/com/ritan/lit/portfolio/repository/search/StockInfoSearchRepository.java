package com.ritan.lit.portfolio.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ritan.lit.portfolio.domain.StockInfo;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link StockInfo} entity.
 */
public interface StockInfoSearchRepository extends ElasticsearchRepository<StockInfo, Long>, StockInfoSearchRepositoryInternal {}

interface StockInfoSearchRepositoryInternal {
    Stream<StockInfo> search(String query);
}

class StockInfoSearchRepositoryInternalImpl implements StockInfoSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    StockInfoSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<StockInfo> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, StockInfo.class).map(SearchHit::getContent).stream();
    }
}
