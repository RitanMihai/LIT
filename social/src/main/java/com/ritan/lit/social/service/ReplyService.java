package com.ritan.lit.social.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.social.domain.Reply;
import com.ritan.lit.social.repository.ReplyRepository;
import com.ritan.lit.social.repository.search.ReplySearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Reply}.
 */
@Service
@Transactional
public class ReplyService {

    private final Logger log = LoggerFactory.getLogger(ReplyService.class);

    private final ReplyRepository replyRepository;

    private final ReplySearchRepository replySearchRepository;

    public ReplyService(ReplyRepository replyRepository, ReplySearchRepository replySearchRepository) {
        this.replyRepository = replyRepository;
        this.replySearchRepository = replySearchRepository;
    }

    /**
     * Save a reply.
     *
     * @param reply the entity to save.
     * @return the persisted entity.
     */
    public Reply save(Reply reply) {
        log.debug("Request to save Reply : {}", reply);
        Reply result = replyRepository.save(reply);
        replySearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a reply.
     *
     * @param reply the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Reply> partialUpdate(Reply reply) {
        log.debug("Request to partially update Reply : {}", reply);

        return replyRepository
            .findById(reply.getId())
            .map(existingReply -> {
                if (reply.getContent() != null) {
                    existingReply.setContent(reply.getContent());
                }
                if (reply.getDate() != null) {
                    existingReply.setDate(reply.getDate());
                }
                if (reply.getLanguage() != null) {
                    existingReply.setLanguage(reply.getLanguage());
                }

                return existingReply;
            })
            .map(replyRepository::save)
            .map(savedReply -> {
                replySearchRepository.save(savedReply);

                return savedReply;
            });
    }

    /**
     * Get all the replies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Reply> findAll(Pageable pageable) {
        log.debug("Request to get all Replies");
        return replyRepository.findAll(pageable);
    }

    /**
     * Get one reply by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Reply> findOne(Long id) {
        log.debug("Request to get Reply : {}", id);
        return replyRepository.findById(id);
    }

    /**
     * Delete the reply by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Reply : {}", id);
        replyRepository.deleteById(id);
        replySearchRepository.deleteById(id);
    }

    /**
     * Search for the reply corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Reply> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Replies for query {}", query);
        return replySearchRepository.search(query, pageable);
    }
}
