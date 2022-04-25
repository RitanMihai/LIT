package com.ritan.lit.social.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.social.domain.UserFollowing;
import com.ritan.lit.social.repository.UserFollowingRepository;
import com.ritan.lit.social.service.UserFollowingService;
import com.ritan.lit.social.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ritan.lit.social.domain.UserFollowing}.
 */
@RestController
@RequestMapping("/api")
public class UserFollowingResource {

    private final Logger log = LoggerFactory.getLogger(UserFollowingResource.class);

    private static final String ENTITY_NAME = "socialUserFollowing";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserFollowingService userFollowingService;

    private final UserFollowingRepository userFollowingRepository;

    public UserFollowingResource(UserFollowingService userFollowingService, UserFollowingRepository userFollowingRepository) {
        this.userFollowingService = userFollowingService;
        this.userFollowingRepository = userFollowingRepository;
    }

    /**
     * {@code POST  /user-followings} : Create a new userFollowing.
     *
     * @param userFollowing the userFollowing to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userFollowing, or with status {@code 400 (Bad Request)} if the userFollowing has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-followings")
    public ResponseEntity<UserFollowing> createUserFollowing(@RequestBody UserFollowing userFollowing) throws URISyntaxException {
        log.debug("REST request to save UserFollowing : {}", userFollowing);
        if (userFollowing.getId() != null) {
            throw new BadRequestAlertException("A new userFollowing cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserFollowing result = userFollowingService.save(userFollowing);
        return ResponseEntity
            .created(new URI("/api/user-followings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-followings/:id} : Updates an existing userFollowing.
     *
     * @param id the id of the userFollowing to save.
     * @param userFollowing the userFollowing to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userFollowing,
     * or with status {@code 400 (Bad Request)} if the userFollowing is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userFollowing couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-followings/{id}")
    public ResponseEntity<UserFollowing> updateUserFollowing(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserFollowing userFollowing
    ) throws URISyntaxException {
        log.debug("REST request to update UserFollowing : {}, {}", id, userFollowing);
        if (userFollowing.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userFollowing.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userFollowingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserFollowing result = userFollowingService.save(userFollowing);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userFollowing.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-followings/:id} : Partial updates given fields of an existing userFollowing, field will ignore if it is null
     *
     * @param id the id of the userFollowing to save.
     * @param userFollowing the userFollowing to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userFollowing,
     * or with status {@code 400 (Bad Request)} if the userFollowing is not valid,
     * or with status {@code 404 (Not Found)} if the userFollowing is not found,
     * or with status {@code 500 (Internal Server Error)} if the userFollowing couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-followings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserFollowing> partialUpdateUserFollowing(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserFollowing userFollowing
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserFollowing partially : {}, {}", id, userFollowing);
        if (userFollowing.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userFollowing.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userFollowingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserFollowing> result = userFollowingService.partialUpdate(userFollowing);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userFollowing.getId().toString())
        );
    }

    /**
     * {@code GET  /user-followings} : get all the userFollowings.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userFollowings in body.
     */
    @GetMapping("/user-followings")
    public List<UserFollowing> getAllUserFollowings(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all UserFollowings");
        return userFollowingService.findAll();
    }

    /**
     * {@code GET  /user-followings/:id} : get the "id" userFollowing.
     *
     * @param id the id of the userFollowing to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userFollowing, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-followings/{id}")
    public ResponseEntity<UserFollowing> getUserFollowing(@PathVariable Long id) {
        log.debug("REST request to get UserFollowing : {}", id);
        Optional<UserFollowing> userFollowing = userFollowingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userFollowing);
    }

    /**
     * {@code DELETE  /user-followings/:id} : delete the "id" userFollowing.
     *
     * @param id the id of the userFollowing to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-followings/{id}")
    public ResponseEntity<Void> deleteUserFollowing(@PathVariable Long id) {
        log.debug("REST request to delete UserFollowing : {}", id);
        userFollowingService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/user-followings?query=:query} : search for the userFollowing corresponding
     * to the query.
     *
     * @param query the query of the userFollowing search.
     * @return the result of the search.
     */
    @GetMapping("/_search/user-followings")
    public List<UserFollowing> searchUserFollowings(@RequestParam String query) {
        log.debug("REST request to search UserFollowings for query {}", query);
        return userFollowingService.search(query);
    }
}
