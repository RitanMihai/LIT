package com.ritan.lit.portfolio.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.portfolio.domain.PortfolioUser;
import com.ritan.lit.portfolio.repository.PortfolioUserRepository;
import com.ritan.lit.portfolio.service.PortfolioUserService;
import com.ritan.lit.portfolio.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.ritan.lit.portfolio.domain.PortfolioUser}.
 */
@RestController
@RequestMapping("/api")
public class PortfolioUserResource {

    private final Logger log = LoggerFactory.getLogger(PortfolioUserResource.class);

    private static final String ENTITY_NAME = "portfolioPortfolioUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PortfolioUserService portfolioUserService;

    private final PortfolioUserRepository portfolioUserRepository;

    public PortfolioUserResource(PortfolioUserService portfolioUserService, PortfolioUserRepository portfolioUserRepository) {
        this.portfolioUserService = portfolioUserService;
        this.portfolioUserRepository = portfolioUserRepository;
    }

    /**
     * {@code POST  /portfolio-users} : Create a new portfolioUser.
     *
     * @param portfolioUser the portfolioUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new portfolioUser, or with status {@code 400 (Bad Request)} if the portfolioUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/portfolio-users")
    public ResponseEntity<PortfolioUser> createPortfolioUser(@Valid @RequestBody PortfolioUser portfolioUser) throws URISyntaxException {
        log.debug("REST request to save PortfolioUser : {}", portfolioUser);
        if (portfolioUser.getId() != null) {
            throw new BadRequestAlertException("A new portfolioUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PortfolioUser result = portfolioUserService.save(portfolioUser);
        return ResponseEntity
            .created(new URI("/api/portfolio-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /portfolio-users/:id} : Updates an existing portfolioUser.
     *
     * @param id the id of the portfolioUser to save.
     * @param portfolioUser the portfolioUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolioUser,
     * or with status {@code 400 (Bad Request)} if the portfolioUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the portfolioUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/portfolio-users/{id}")
    public ResponseEntity<PortfolioUser> updatePortfolioUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PortfolioUser portfolioUser
    ) throws URISyntaxException {
        log.debug("REST request to update PortfolioUser : {}, {}", id, portfolioUser);
        if (portfolioUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolioUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!portfolioUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PortfolioUser result = portfolioUserService.save(portfolioUser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, portfolioUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /portfolio-users/:id} : Partial updates given fields of an existing portfolioUser, field will ignore if it is null
     *
     * @param id the id of the portfolioUser to save.
     * @param portfolioUser the portfolioUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolioUser,
     * or with status {@code 400 (Bad Request)} if the portfolioUser is not valid,
     * or with status {@code 404 (Not Found)} if the portfolioUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the portfolioUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/portfolio-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PortfolioUser> partialUpdatePortfolioUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PortfolioUser portfolioUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update PortfolioUser partially : {}, {}", id, portfolioUser);
        if (portfolioUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolioUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!portfolioUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PortfolioUser> result = portfolioUserService.partialUpdate(portfolioUser);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, portfolioUser.getId().toString())
        );
    }

    /**
     * {@code GET  /portfolio-users} : get all the portfolioUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of portfolioUsers in body.
     */
    @GetMapping("/portfolio-users")
    public List<PortfolioUser> getAllPortfolioUsers() {
        log.debug("REST request to get all PortfolioUsers");
        return portfolioUserService.findAll();
    }

    /**
     * {@code GET  /portfolio-users/:id} : get the "id" portfolioUser.
     *
     * @param id the id of the portfolioUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the portfolioUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/portfolio-users/{id}")
    public ResponseEntity<PortfolioUser> getPortfolioUser(@PathVariable Long id) {
        log.debug("REST request to get PortfolioUser : {}", id);
        Optional<PortfolioUser> portfolioUser = portfolioUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(portfolioUser);
    }

    /**
     * {@code DELETE  /portfolio-users/:id} : delete the "id" portfolioUser.
     *
     * @param id the id of the portfolioUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/portfolio-users/{id}")
    public ResponseEntity<Void> deletePortfolioUser(@PathVariable Long id) {
        log.debug("REST request to delete PortfolioUser : {}", id);
        portfolioUserService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/portfolio-users?query=:query} : search for the portfolioUser corresponding
     * to the query.
     *
     * @param query the query of the portfolioUser search.
     * @return the result of the search.
     */
    @GetMapping("/_search/portfolio-users")
    public List<PortfolioUser> searchPortfolioUsers(@RequestParam String query) {
        log.debug("REST request to search PortfolioUsers for query {}", query);
        return portfolioUserService.search(query);
    }
}
