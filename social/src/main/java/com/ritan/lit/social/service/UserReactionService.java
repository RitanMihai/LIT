package com.ritan.lit.social.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.social.domain.UserReaction;
import com.ritan.lit.social.repository.UserReactionRepository;
import com.ritan.lit.social.repository.search.UserReactionSearchRepository;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link UserReaction}.
 */
@Service
@Transactional
public class UserReactionService {

    private final Logger log = LoggerFactory.getLogger(UserReactionService.class);

    private final UserReactionRepository userReactionRepository;


    public UserReactionService(UserReactionRepository userReactionRepository) {
        this.userReactionRepository = userReactionRepository;
    }

    /**
     * Save a userReaction.
     *
     * @param userReaction the entity to save.
     * @return the persisted entity.
     */
    public UserReaction save(UserReaction userReaction) {
        log.debug("Request to save UserReaction : {}", userReaction);
        UserReaction result = userReactionRepository.save(userReaction);
        return result;
    }

    /**
     * Partially update a userReaction.
     *
     * @param userReaction the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserReaction> partialUpdate(UserReaction userReaction) {
        log.debug("Request to partially update UserReaction : {}", userReaction);

        return userReactionRepository
            .findById(userReaction.getId())
            .map(existingUserReaction -> {
                if (userReaction.getType() != null) {
                    existingUserReaction.setType(userReaction.getType());
                }

                return existingUserReaction;
            })
            .map(userReactionRepository::save);
    }

    /**
     * Get all the userReactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserReaction> findAll(Pageable pageable) {
        log.debug("Request to get all UserReactions");
        return userReactionRepository.findAll(pageable);
    }

    /**
     * Get one userReaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserReaction> findOne(Long id) {
        log.debug("Request to get UserReaction : {}", id);
        return userReactionRepository.findById(id);
    }

    /**
     * Delete the userReaction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UserReaction : {}", id);
        userReactionRepository.deleteById(id);
    }

    public Optional<UserReaction> findByPostAndUser(Long postId, String user) {
        return userReactionRepository.findByPostIdAndSocialUserUser(postId, user);
    }

    public Optional<List<UserReaction>> findAllByUsername(String username) {
        return userReactionRepository.findAllBySocialUserUser(username);
    }
    /**
     * Search for the userReaction corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
}
