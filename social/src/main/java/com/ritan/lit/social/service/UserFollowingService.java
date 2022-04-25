package com.ritan.lit.social.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.social.domain.UserFollowing;
import com.ritan.lit.social.repository.UserFollowingRepository;
import com.ritan.lit.social.repository.search.UserFollowingSearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link UserFollowing}.
 */
@Service
@Transactional
public class UserFollowingService {

    private final Logger log = LoggerFactory.getLogger(UserFollowingService.class);

    private final UserFollowingRepository userFollowingRepository;

    private final UserFollowingSearchRepository userFollowingSearchRepository;

    public UserFollowingService(
        UserFollowingRepository userFollowingRepository,
        UserFollowingSearchRepository userFollowingSearchRepository
    ) {
        this.userFollowingRepository = userFollowingRepository;
        this.userFollowingSearchRepository = userFollowingSearchRepository;
    }

    /**
     * Save a userFollowing.
     *
     * @param userFollowing the entity to save.
     * @return the persisted entity.
     */
    public UserFollowing save(UserFollowing userFollowing) {
        log.debug("Request to save UserFollowing : {}", userFollowing);
        UserFollowing result = userFollowingRepository.save(userFollowing);
        userFollowingSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a userFollowing.
     *
     * @param userFollowing the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserFollowing> partialUpdate(UserFollowing userFollowing) {
        log.debug("Request to partially update UserFollowing : {}", userFollowing);

        return userFollowingRepository
            .findById(userFollowing.getId())
            .map(existingUserFollowing -> {
                if (userFollowing.getStock() != null) {
                    existingUserFollowing.setStock(userFollowing.getStock());
                }

                return existingUserFollowing;
            })
            .map(userFollowingRepository::save)
            .map(savedUserFollowing -> {
                userFollowingSearchRepository.save(savedUserFollowing);

                return savedUserFollowing;
            });
    }

    /**
     * Get all the userFollowings.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserFollowing> findAll() {
        log.debug("Request to get all UserFollowings");
        return userFollowingRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the userFollowings with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<UserFollowing> findAllWithEagerRelationships(Pageable pageable) {
        return userFollowingRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one userFollowing by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserFollowing> findOne(Long id) {
        log.debug("Request to get UserFollowing : {}", id);
        return userFollowingRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the userFollowing by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UserFollowing : {}", id);
        userFollowingRepository.deleteById(id);
        userFollowingSearchRepository.deleteById(id);
    }

    /**
     * Search for the userFollowing corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserFollowing> search(String query) {
        log.debug("Request to search UserFollowings for query {}", query);
        return StreamSupport.stream(userFollowingSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
