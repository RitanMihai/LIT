package com.ritan.lit.social.service;

import com.ritan.lit.social.domain.Post;
import com.ritan.lit.social.domain.SocialUser;
import com.ritan.lit.social.repository.PostRepository;
import com.ritan.lit.social.repository.SocialUserRepository;
import com.ritan.lit.social.repository.search.PostSearchRepository;

import java.util.Optional;

import com.ritan.lit.social.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Post}.
 */
@Service
@Transactional
public class PostService {

    private final Logger log = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final PostSearchRepository postSearchRepository;
    private final SocialUserRepository socialUserRepository;

    public PostService(PostRepository postRepository, PostSearchRepository postSearchRepository, SocialUserRepository socialUserRepository) {
        this.postRepository = postRepository;
        this.postSearchRepository = postSearchRepository;
        this.socialUserRepository = socialUserRepository;
    }

    /**
     * Save a post.
     *
     * @param post the entity to save.
     * @return the persisted entity.
     */
    public Post save(Post post) {
        log.debug("Request to save Post : {}", post);
        Post result = postRepository.save(post);
        postSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a post.
     *
     * @param post the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Post> partialUpdate(Post post) {
        log.debug("Request to partially update Post : {}", post);

        return postRepository
            .findById(post.getId())
            .map(existingPost -> {
                if (post.getContent() != null) {
                    existingPost.setContent(post.getContent());
                }
                if (post.getImage() != null) {
                    existingPost.setImage(post.getImage());
                }
                if (post.getImageContentType() != null) {
                    existingPost.setImageContentType(post.getImageContentType());
                }
                if (post.getDate() != null) {
                    existingPost.setDate(post.getDate());
                }
                if (post.getLanguage() != null) {
                    existingPost.setLanguage(post.getLanguage());
                }
                if (post.getIsPayedPromotion() != null) {
                    existingPost.setIsPayedPromotion(post.getIsPayedPromotion());
                }

                return existingPost;
            })
            .map(postRepository::save)
            .map(savedPost -> {
                postSearchRepository.save(savedPost);

                return savedPost;
            });
    }

    /**
     * Get all the posts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Post> findAll(Pageable pageable) {
        log.debug("Request to get all Posts");
        //String currentUserName = SecurityUtils.getCurrentUserLogin().get();
        //SocialUser socialUser = socialUserRepository.findByUserIs(currentUserName);

        //return postRepository.findAllBySocialUser(socialUser, pageable);
        Page<Post> all = postRepository.findAll(pageable);
        System.out.println("SHOW ALL POSTS GOT : " + all);
        //return postRepository.findAll(pageable);
        return all;
    }

    /**
     * Get one post by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Post> findOne(Long id) {
        log.debug("Request to get Post : {}", id);
        return postRepository.findById(id);
    }

    /**
     * Delete the post by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Post : {}", id);
        postRepository.deleteById(id);
        postSearchRepository.deleteById(id);
    }

    /**
     * Search for the post corresponding to the query.
     *
     * @param query    the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Post> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Posts for query {}", query);
        return postSearchRepository.search(query, pageable);
    }
}
