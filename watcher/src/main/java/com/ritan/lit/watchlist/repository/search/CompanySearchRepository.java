package com.ritan.lit.watchlist.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ritan.lit.watchlist.domain.Company;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Company} entity.
 */
public interface CompanySearchRepository extends ElasticsearchRepository<Company, Long>, CompanySearchRepositoryInternal {}

interface CompanySearchRepositoryInternal {
    Stream<Company> search(String query);
}

class CompanySearchRepositoryInternalImpl implements CompanySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    CompanySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Company> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Company.class).map(SearchHit::getContent).stream();
    }
}
