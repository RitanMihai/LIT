package com.ritan.lit.social.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.social.domain.UserReaction;
import com.ritan.lit.social.repository.UserReactionRepository;
import com.ritan.lit.social.service.UserReactionService;
import com.ritan.lit.social.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.checkerframework.common.reflection.qual.GetClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ritan.lit.social.domain.UserReaction}.
 */
@RestController
@RequestMapping("/api")
public class UserReactionResource {

    private final Logger log = LoggerFactory.getLogger(UserReactionResource.class);

    private static final String ENTITY_NAME = "socialUserReaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserReactionService userReactionService;

    private final UserReactionRepository userReactionRepository;

    public UserReactionResource(UserReactionService userReactionService, UserReactionRepository userReactionRepository) {
        this.userReactionService = userReactionService;
        this.userReactionRepository = userReactionRepository;
    }

    /**
     * {@code POST  /user-reactions} : Create a new userReaction.
     *
     * @param userReaction the userReaction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userReaction, or with status {@code 400 (Bad Request)} if the userReaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-reactions")
    public ResponseEntity<UserReaction> createUserReaction(@RequestBody UserReaction userReaction) throws URISyntaxException {
        log.debug("REST request to save UserReaction : {}", userReaction);
        if (userReaction.getId() != null) {
            throw new BadRequestAlertException("A new userReaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserReaction result = userReactionService.save(userReaction);
        return ResponseEntity
            .created(new URI("/api/user-reactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-reactions/:id} : Updates an existing userReaction.
     *
     * @param id           the id of the userReaction to save.
     * @param userReaction the userReaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userReaction,
     * or with status {@code 400 (Bad Request)} if the userReaction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userReaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-reactions/{id}")
    public ResponseEntity<UserReaction> updateUserReaction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserReaction userReaction
    ) throws URISyntaxException {
        log.debug("REST request to update UserReaction : {}, {}", id, userReaction);
        if (userReaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userReaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userReactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserReaction result = userReactionService.save(userReaction);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userReaction.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-reactions/:id} : Partial updates given fields of an existing userReaction, field will ignore if it is null
     *
     * @param id           the id of the userReaction to save.
     * @param userReaction the userReaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userReaction,
     * or with status {@code 400 (Bad Request)} if the userReaction is not valid,
     * or with status {@code 404 (Not Found)} if the userReaction is not found,
     * or with status {@code 500 (Internal Server Error)} if the userReaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-reactions/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<UserReaction> partialUpdateUserReaction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserReaction userReaction
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserReaction partially : {}, {}", id, userReaction);
        if (userReaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userReaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userReactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserReaction> result = userReactionService.partialUpdate(userReaction);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userReaction.getId().toString())
        );
    }

    /**
     * {@code GET  /user-reactions} : get all the userReactions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userReactions in body.
     */
    @GetMapping("/user-reactions")
    public ResponseEntity<List<UserReaction>> getAllUserReactions(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of UserReactions");
        Page<UserReaction> page = userReactionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-reactions/:id} : get the "id" userReaction.
     *
     * @param id the id of the userReaction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userReaction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-reactions/{id}")
    public ResponseEntity<UserReaction> getUserReaction(@PathVariable Long id) {
        log.debug("REST request to get UserReaction : {}", id);
        Optional<UserReaction> userReaction = userReactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userReaction);
    }

    @GetMapping("/user-reactions/user/{username}")
    public ResponseEntity<List<UserReaction>> getUserReactions(@PathVariable String username) {
        Optional<List<UserReaction>> userReactions = userReactionService.findAllByUsername(username);
        return ResponseUtil.wrapOrNotFound(userReactions);
    }

    @GetMapping("/user-reactions/specific")
    public ResponseEntity<?> getUserReactionByPostAndSocialUser(
        @RequestParam("postId") Long postId,
        @RequestParam("user") String user) {
        Optional<UserReaction> userReaction = userReactionService.findByPostAndUser(postId, user);
        return ResponseUtil.wrapOrNotFound(userReaction);
    }

    /**
     * {@code DELETE  /user-reactions/:id} : delete the "id" userReaction.
     *
     * @param id the id of the userReaction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-reactions/{id}")
    public ResponseEntity<Void> deleteUserReaction(@PathVariable Long id) {
        log.debug("REST request to delete UserReaction : {}", id);
        userReactionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
