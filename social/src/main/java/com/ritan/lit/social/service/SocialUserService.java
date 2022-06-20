package com.ritan.lit.social.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.social.domain.SocialUser;
import com.ritan.lit.social.repository.SocialUserRepository;
import com.ritan.lit.social.repository.search.SocialUserSearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SocialUser}.
 */
@Service
@Transactional
public class SocialUserService {

    private final Logger log = LoggerFactory.getLogger(SocialUserService.class);

    private final SocialUserRepository socialUserRepository;

    private final SocialUserSearchRepository socialUserSearchRepository;

    public SocialUserService(SocialUserRepository socialUserRepository, SocialUserSearchRepository socialUserSearchRepository) {
        this.socialUserRepository = socialUserRepository;
        this.socialUserSearchRepository = socialUserSearchRepository;
    }

    /**
     * Save a socialUser.
     *
     * @param socialUser the entity to save.
     * @return the persisted entity.
     */
    public SocialUser save(SocialUser socialUser) {
        log.debug("Request to save SocialUser : {}", socialUser);
        SocialUser result = socialUserRepository.save(socialUser);
        socialUserSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a socialUser.
     *
     * @param socialUser the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SocialUser> partialUpdate(SocialUser socialUser) {
        log.debug("Request to partially update SocialUser : {}", socialUser);

        return socialUserRepository
            .findById(socialUser.getId())
            .map(existingSocialUser -> {
                if (socialUser.getUser() != null) {
                    existingSocialUser.setUser(socialUser.getUser());
                }

                return existingSocialUser;
            })
            .map(socialUserRepository::save)
            .map(savedSocialUser -> {
                socialUserSearchRepository.save(savedSocialUser);

                return savedSocialUser;
            });
    }

    /**
     * Get all the socialUsers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SocialUser> findAll() {
        log.debug("Request to get all SocialUsers");
        return socialUserRepository.findAll();
    }

    /**
     * Get one socialUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SocialUser> findOne(Long id) {
        log.debug("Request to get SocialUser : {}", id);
        return socialUserRepository.findById(id);
    }

    /**
     * Delete the socialUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SocialUser : {}", id);
        socialUserRepository.deleteById(id);
        socialUserSearchRepository.deleteById(id);
    }

    /**
     * Search for the socialUser corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SocialUser> search(String query) {
        log.debug("Request to search SocialUsers for query {}", query);
        return StreamSupport.stream(socialUserSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }

    public SocialUser findOneByUser(String user) {
        return socialUserRepository.findByUserIs(user);
    }
}
