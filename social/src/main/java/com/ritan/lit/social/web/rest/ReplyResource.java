package com.ritan.lit.social.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.social.domain.Reply;
import com.ritan.lit.social.repository.ReplyRepository;
import com.ritan.lit.social.service.ReplyService;
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
 * REST controller for managing {@link com.ritan.lit.social.domain.Reply}.
 */
@RestController
@RequestMapping("/api")
public class ReplyResource {

    private final Logger log = LoggerFactory.getLogger(ReplyResource.class);

    private static final String ENTITY_NAME = "socialReply";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReplyService replyService;

    private final ReplyRepository replyRepository;

    public ReplyResource(ReplyService replyService, ReplyRepository replyRepository) {
        this.replyService = replyService;
        this.replyRepository = replyRepository;
    }

    /**
     * {@code POST  /replies} : Create a new reply.
     *
     * @param reply the reply to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reply, or with status {@code 400 (Bad Request)} if the reply has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/replies")
    public ResponseEntity<Reply> createReply(@Valid @RequestBody Reply reply) throws URISyntaxException {
        log.debug("REST request to save Reply : {}", reply);
        if (reply.getId() != null) {
            throw new BadRequestAlertException("A new reply cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Reply result = replyService.save(reply);
        return ResponseEntity
            .created(new URI("/api/replies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /replies/:id} : Updates an existing reply.
     *
     * @param id the id of the reply to save.
     * @param reply the reply to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reply,
     * or with status {@code 400 (Bad Request)} if the reply is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reply couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/replies/{id}")
    public ResponseEntity<Reply> updateReply(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Reply reply)
        throws URISyntaxException {
        log.debug("REST request to update Reply : {}, {}", id, reply);
        if (reply.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reply.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!replyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Reply result = replyService.save(reply);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reply.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /replies/:id} : Partial updates given fields of an existing reply, field will ignore if it is null
     *
     * @param id the id of the reply to save.
     * @param reply the reply to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reply,
     * or with status {@code 400 (Bad Request)} if the reply is not valid,
     * or with status {@code 404 (Not Found)} if the reply is not found,
     * or with status {@code 500 (Internal Server Error)} if the reply couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/replies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Reply> partialUpdateReply(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Reply reply
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reply partially : {}, {}", id, reply);
        if (reply.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reply.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!replyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Reply> result = replyService.partialUpdate(reply);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reply.getId().toString())
        );
    }

    /**
     * {@code GET  /replies} : get all the replies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of replies in body.
     */
    @GetMapping("/replies")
    public ResponseEntity<List<Reply>> getAllReplies(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Replies");
        Page<Reply> page = replyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /replies/:id} : get the "id" reply.
     *
     * @param id the id of the reply to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reply, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/replies/{id}")
    public ResponseEntity<Reply> getReply(@PathVariable Long id) {
        log.debug("REST request to get Reply : {}", id);
        Optional<Reply> reply = replyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reply);
    }

    /**
     * {@code DELETE  /replies/:id} : delete the "id" reply.
     *
     * @param id the id of the reply to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/replies/{id}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long id) {
        log.debug("REST request to delete Reply : {}", id);
        replyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/replies?query=:query} : search for the reply corresponding
     * to the query.
     *
     * @param query the query of the reply search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/replies")
    public ResponseEntity<List<Reply>> searchReplies(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Replies for query {}", query);
        Page<Reply> page = replyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
