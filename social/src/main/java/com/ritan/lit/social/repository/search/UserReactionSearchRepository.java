package com.ritan.lit.social.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ritan.lit.social.domain.UserReaction;
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
 * Spring Data Elasticsearch repository for the {@link UserReaction} entity.
 */
public interface UserReactionSearchRepository extends ElasticsearchRepository<UserReaction, Long>, UserReactionSearchRepositoryInternal {}

interface UserReactionSearchRepositoryInternal {
    Page<UserReaction> search(String query, Pageable pageable);
}

class UserReactionSearchRepositoryInternalImpl implements UserReactionSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    UserReactionSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<UserReaction> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<UserReaction> hits = elasticsearchTemplate
            .search(nativeSearchQuery, UserReaction.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
