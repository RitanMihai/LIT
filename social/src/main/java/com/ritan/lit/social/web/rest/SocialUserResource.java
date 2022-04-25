package com.ritan.lit.social.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.social.domain.SocialUser;
import com.ritan.lit.social.repository.SocialUserRepository;
import com.ritan.lit.social.service.SocialUserService;
import com.ritan.lit.social.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ritan.lit.social.domain.SocialUser}.
 */
@RestController
@RequestMapping("/api")
public class SocialUserResource {

    private final Logger log = LoggerFactory.getLogger(SocialUserResource.class);

    private static final String ENTITY_NAME = "socialSocialUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SocialUserService socialUserService;

    private final SocialUserRepository socialUserRepository;

    public SocialUserResource(SocialUserService socialUserService, SocialUserRepository socialUserRepository) {
        this.socialUserService = socialUserService;
        this.socialUserRepository = socialUserRepository;
    }

    /**
     * {@code POST  /social-users} : Create a new socialUser.
     *
     * @param socialUser the socialUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new socialUser, or with status {@code 400 (Bad Request)} if the socialUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/social-users")
    public ResponseEntity<SocialUser> createSocialUser(@Valid @RequestBody SocialUser socialUser) throws URISyntaxException {
        log.debug("REST request to save SocialUser : {}", socialUser);
        if (socialUser.getId() != null) {
            throw new BadRequestAlertException("A new socialUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SocialUser result = socialUserService.save(socialUser);
        return ResponseEntity
            .created(new URI("/api/social-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /social-users/:id} : Updates an existing socialUser.
     *
     * @param id the id of the socialUser to save.
     * @param socialUser the socialUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated socialUser,
     * or with status {@code 400 (Bad Request)} if the socialUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the socialUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/social-users/{id}")
    public ResponseEntity<SocialUser> updateSocialUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SocialUser socialUser
    ) throws URISyntaxException {
        log.debug("REST request to update SocialUser : {}, {}", id, socialUser);
        if (socialUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, socialUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!socialUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SocialUser result = socialUserService.save(socialUser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, socialUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /social-users/:id} : Partial updates given fields of an existing socialUser, field will ignore if it is null
     *
     * @param id the id of the socialUser to save.
     * @param socialUser the socialUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated socialUser,
     * or with status {@code 400 (Bad Request)} if the socialUser is not valid,
     * or with status {@code 404 (Not Found)} if the socialUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the socialUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/social-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SocialUser> partialUpdateSocialUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SocialUser socialUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update SocialUser partially : {}, {}", id, socialUser);
        if (socialUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, socialUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!socialUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SocialUser> result = socialUserService.partialUpdate(socialUser);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, socialUser.getId().toString())
        );
    }

    /**
     * {@code GET  /social-users} : get all the socialUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of socialUsers in body.
     */
    @GetMapping("/social-users")
    public List<SocialUser> getAllSocialUsers() {
        log.debug("REST request to get all SocialUsers");
        return socialUserService.findAll();
    }

    /**
     * {@code GET  /social-users/:id} : get the "id" socialUser.
     *
     * @param id the id of the socialUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the socialUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/social-users/{id}")
    public ResponseEntity<SocialUser> getSocialUser(@PathVariable Long id) {
        log.debug("REST request to get SocialUser : {}", id);
        Optional<SocialUser> socialUser = socialUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(socialUser);
    }

    /**
     * {@code DELETE  /social-users/:id} : delete the "id" socialUser.
     *
     * @param id the id of the socialUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/social-users/{id}")
    public ResponseEntity<Void> deleteSocialUser(@PathVariable Long id) {
        log.debug("REST request to delete SocialUser : {}", id);
        socialUserService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/social-users?query=:query} : search for the socialUser corresponding
     * to the query.
     *
     * @param query the query of the socialUser search.
     * @return the result of the search.
     */
    @GetMapping("/_search/social-users")
    public List<SocialUser> searchSocialUsers(@RequestParam String query) {
        log.debug("REST request to search SocialUsers for query {}", query);
        return socialUserService.search(query);
    }
}
