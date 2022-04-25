package com.ritan.lit.portfolio.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ritan.lit.portfolio.domain.Dividend;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Dividend} entity.
 */
public interface DividendSearchRepository extends ElasticsearchRepository<Dividend, Long>, DividendSearchRepositoryInternal {}

interface DividendSearchRepositoryInternal {
    Page<Dividend> search(String query, Pageable pageable);
}

class DividendSearchRepositoryInternalImpl implements DividendSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    DividendSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Dividend> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Dividend> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Dividend.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
