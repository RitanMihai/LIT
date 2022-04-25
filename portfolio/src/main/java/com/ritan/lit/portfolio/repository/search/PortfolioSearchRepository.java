package com.ritan.lit.portfolio.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ritan.lit.portfolio.domain.Portfolio;
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
 * Spring Data Elasticsearch repository for the {@link Portfolio} entity.
 */
public interface PortfolioSearchRepository extends ElasticsearchRepository<Portfolio, Long>, PortfolioSearchRepositoryInternal {}

interface PortfolioSearchRepositoryInternal {
    Page<Portfolio> search(String query, Pageable pageable);
}

class PortfolioSearchRepositoryInternalImpl implements PortfolioSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    PortfolioSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Portfolio> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Portfolio> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Portfolio.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
