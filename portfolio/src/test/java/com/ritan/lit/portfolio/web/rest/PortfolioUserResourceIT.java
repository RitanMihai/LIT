package com.ritan.lit.portfolio.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.portfolio.IntegrationTest;
import com.ritan.lit.portfolio.domain.PortfolioUser;
import com.ritan.lit.portfolio.repository.PortfolioUserRepository;
import com.ritan.lit.portfolio.repository.search.PortfolioUserSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PortfolioUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PortfolioUserResourceIT {

    private static final Long DEFAULT_USER = 1L;
    private static final Long UPDATED_USER = 2L;

    private static final String ENTITY_API_URL = "/api/portfolio-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/portfolio-users";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PortfolioUserRepository portfolioUserRepository;

    /**
     * This repository is mocked in the com.ritan.lit.portfolio.repository.search test package.
     *
     * @see com.ritan.lit.portfolio.repository.search.PortfolioUserSearchRepositoryMockConfiguration
     */
    @Autowired
    private PortfolioUserSearchRepository mockPortfolioUserSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPortfolioUserMockMvc;

    private PortfolioUser portfolioUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PortfolioUser createEntity(EntityManager em) {
        PortfolioUser portfolioUser = new PortfolioUser().user(DEFAULT_USER);
        return portfolioUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PortfolioUser createUpdatedEntity(EntityManager em) {
        PortfolioUser portfolioUser = new PortfolioUser().user(UPDATED_USER);
        return portfolioUser;
    }

    @BeforeEach
    public void initTest() {
        portfolioUser = createEntity(em);
    }

    @Test
    @Transactional
    void createPortfolioUser() throws Exception {
        int databaseSizeBeforeCreate = portfolioUserRepository.findAll().size();
        // Create the PortfolioUser
        restPortfolioUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(portfolioUser)))
            .andExpect(status().isCreated());

        // Validate the PortfolioUser in the database
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeCreate + 1);
        PortfolioUser testPortfolioUser = portfolioUserList.get(portfolioUserList.size() - 1);
        assertThat(testPortfolioUser.getUser()).isEqualTo(DEFAULT_USER);

        // Validate the PortfolioUser in Elasticsearch
        verify(mockPortfolioUserSearchRepository, times(1)).save(testPortfolioUser);
    }

    @Test
    @Transactional
    void createPortfolioUserWithExistingId() throws Exception {
        // Create the PortfolioUser with an existing ID
        portfolioUser.setId(1L);

        int databaseSizeBeforeCreate = portfolioUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPortfolioUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(portfolioUser)))
            .andExpect(status().isBadRequest());

        // Validate the PortfolioUser in the database
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeCreate);

        // Validate the PortfolioUser in Elasticsearch
        verify(mockPortfolioUserSearchRepository, times(0)).save(portfolioUser);
    }

    @Test
    @Transactional
    void checkUserIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioUserRepository.findAll().size();
        // set the field null
        portfolioUser.setUser(null);

        // Create the PortfolioUser, which fails.

        restPortfolioUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(portfolioUser)))
            .andExpect(status().isBadRequest());

        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPortfolioUsers() throws Exception {
        // Initialize the database
        portfolioUserRepository.saveAndFlush(portfolioUser);

        // Get all the portfolioUserList
        restPortfolioUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(portfolioUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER.intValue())));
    }

    @Test
    @Transactional
    void getPortfolioUser() throws Exception {
        // Initialize the database
        portfolioUserRepository.saveAndFlush(portfolioUser);

        // Get the portfolioUser
        restPortfolioUserMockMvc
            .perform(get(ENTITY_API_URL_ID, portfolioUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(portfolioUser.getId().intValue()))
            .andExpect(jsonPath("$.user").value(DEFAULT_USER.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingPortfolioUser() throws Exception {
        // Get the portfolioUser
        restPortfolioUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPortfolioUser() throws Exception {
        // Initialize the database
        portfolioUserRepository.saveAndFlush(portfolioUser);

        int databaseSizeBeforeUpdate = portfolioUserRepository.findAll().size();

        // Update the portfolioUser
        PortfolioUser updatedPortfolioUser = portfolioUserRepository.findById(portfolioUser.getId()).get();
        // Disconnect from session so that the updates on updatedPortfolioUser are not directly saved in db
        em.detach(updatedPortfolioUser);
        updatedPortfolioUser.user(UPDATED_USER);

        restPortfolioUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPortfolioUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPortfolioUser))
            )
            .andExpect(status().isOk());

        // Validate the PortfolioUser in the database
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeUpdate);
        PortfolioUser testPortfolioUser = portfolioUserList.get(portfolioUserList.size() - 1);
        assertThat(testPortfolioUser.getUser()).isEqualTo(UPDATED_USER);

        // Validate the PortfolioUser in Elasticsearch
        verify(mockPortfolioUserSearchRepository).save(testPortfolioUser);
    }

    @Test
    @Transactional
    void putNonExistingPortfolioUser() throws Exception {
        int databaseSizeBeforeUpdate = portfolioUserRepository.findAll().size();
        portfolioUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPortfolioUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, portfolioUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(portfolioUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the PortfolioUser in the database
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioUser in Elasticsearch
        verify(mockPortfolioUserSearchRepository, times(0)).save(portfolioUser);
    }

    @Test
    @Transactional
    void putWithIdMismatchPortfolioUser() throws Exception {
        int databaseSizeBeforeUpdate = portfolioUserRepository.findAll().size();
        portfolioUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(portfolioUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the PortfolioUser in the database
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioUser in Elasticsearch
        verify(mockPortfolioUserSearchRepository, times(0)).save(portfolioUser);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPortfolioUser() throws Exception {
        int databaseSizeBeforeUpdate = portfolioUserRepository.findAll().size();
        portfolioUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(portfolioUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PortfolioUser in the database
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioUser in Elasticsearch
        verify(mockPortfolioUserSearchRepository, times(0)).save(portfolioUser);
    }

    @Test
    @Transactional
    void partialUpdatePortfolioUserWithPatch() throws Exception {
        // Initialize the database
        portfolioUserRepository.saveAndFlush(portfolioUser);

        int databaseSizeBeforeUpdate = portfolioUserRepository.findAll().size();

        // Update the portfolioUser using partial update
        PortfolioUser partialUpdatedPortfolioUser = new PortfolioUser();
        partialUpdatedPortfolioUser.setId(portfolioUser.getId());

        partialUpdatedPortfolioUser.user(UPDATED_USER);

        restPortfolioUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPortfolioUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPortfolioUser))
            )
            .andExpect(status().isOk());

        // Validate the PortfolioUser in the database
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeUpdate);
        PortfolioUser testPortfolioUser = portfolioUserList.get(portfolioUserList.size() - 1);
        assertThat(testPortfolioUser.getUser()).isEqualTo(UPDATED_USER);
    }

    @Test
    @Transactional
    void fullUpdatePortfolioUserWithPatch() throws Exception {
        // Initialize the database
        portfolioUserRepository.saveAndFlush(portfolioUser);

        int databaseSizeBeforeUpdate = portfolioUserRepository.findAll().size();

        // Update the portfolioUser using partial update
        PortfolioUser partialUpdatedPortfolioUser = new PortfolioUser();
        partialUpdatedPortfolioUser.setId(portfolioUser.getId());

        partialUpdatedPortfolioUser.user(UPDATED_USER);

        restPortfolioUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPortfolioUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPortfolioUser))
            )
            .andExpect(status().isOk());

        // Validate the PortfolioUser in the database
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeUpdate);
        PortfolioUser testPortfolioUser = portfolioUserList.get(portfolioUserList.size() - 1);
        assertThat(testPortfolioUser.getUser()).isEqualTo(UPDATED_USER);
    }

    @Test
    @Transactional
    void patchNonExistingPortfolioUser() throws Exception {
        int databaseSizeBeforeUpdate = portfolioUserRepository.findAll().size();
        portfolioUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPortfolioUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, portfolioUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(portfolioUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the PortfolioUser in the database
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioUser in Elasticsearch
        verify(mockPortfolioUserSearchRepository, times(0)).save(portfolioUser);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPortfolioUser() throws Exception {
        int databaseSizeBeforeUpdate = portfolioUserRepository.findAll().size();
        portfolioUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(portfolioUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the PortfolioUser in the database
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioUser in Elasticsearch
        verify(mockPortfolioUserSearchRepository, times(0)).save(portfolioUser);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPortfolioUser() throws Exception {
        int databaseSizeBeforeUpdate = portfolioUserRepository.findAll().size();
        portfolioUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioUserMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(portfolioUser))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PortfolioUser in the database
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PortfolioUser in Elasticsearch
        verify(mockPortfolioUserSearchRepository, times(0)).save(portfolioUser);
    }

    @Test
    @Transactional
    void deletePortfolioUser() throws Exception {
        // Initialize the database
        portfolioUserRepository.saveAndFlush(portfolioUser);

        int databaseSizeBeforeDelete = portfolioUserRepository.findAll().size();

        // Delete the portfolioUser
        restPortfolioUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, portfolioUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PortfolioUser> portfolioUserList = portfolioUserRepository.findAll();
        assertThat(portfolioUserList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PortfolioUser in Elasticsearch
        verify(mockPortfolioUserSearchRepository, times(1)).deleteById(portfolioUser.getId());
    }

    @Test
    @Transactional
    void searchPortfolioUser() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        portfolioUserRepository.saveAndFlush(portfolioUser);
        when(mockPortfolioUserSearchRepository.search("id:" + portfolioUser.getId())).thenReturn(Stream.of(portfolioUser));

        // Search the portfolioUser
        restPortfolioUserMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + portfolioUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(portfolioUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER.intValue())));
    }
}
