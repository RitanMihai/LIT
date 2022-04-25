package com.ritan.lit.social.repository;

import com.ritan.lit.social.domain.Tag;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.hibernate.annotations.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class TagRepositoryWithBagRelationshipsImpl implements TagRepositoryWithBagRelationships {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<Tag> fetchBagRelationships(Optional<Tag> tag) {
        return tag.map(this::fetchPosts).map(this::fetchComments).map(this::fetchReplies);
    }

    @Override
    public Page<Tag> fetchBagRelationships(Page<Tag> tags) {
        return new PageImpl<>(fetchBagRelationships(tags.getContent()), tags.getPageable(), tags.getTotalElements());
    }

    @Override
    public List<Tag> fetchBagRelationships(List<Tag> tags) {
        return Optional.of(tags).map(this::fetchPosts).map(this::fetchComments).map(this::fetchReplies).get();
    }

    Tag fetchPosts(Tag result) {
        return entityManager
            .createQuery("select tag from Tag tag left join fetch tag.posts where tag is :tag", Tag.class)
            .setParameter("tag", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Tag> fetchPosts(List<Tag> tags) {
        return entityManager
            .createQuery("select distinct tag from Tag tag left join fetch tag.posts where tag in :tags", Tag.class)
            .setParameter("tags", tags)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }

    Tag fetchComments(Tag result) {
        return entityManager
            .createQuery("select tag from Tag tag left join fetch tag.comments where tag is :tag", Tag.class)
            .setParameter("tag", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Tag> fetchComments(List<Tag> tags) {
        return entityManager
            .createQuery("select distinct tag from Tag tag left join fetch tag.comments where tag in :tags", Tag.class)
            .setParameter("tags", tags)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }

    Tag fetchReplies(Tag result) {
        return entityManager
            .createQuery("select tag from Tag tag left join fetch tag.replies where tag is :tag", Tag.class)
            .setParameter("tag", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Tag> fetchReplies(List<Tag> tags) {
        return entityManager
            .createQuery("select distinct tag from Tag tag left join fetch tag.replies where tag in :tags", Tag.class)
            .setParameter("tags", tags)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
