package com.ritan.lit.social.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.social.IntegrationTest;
import com.ritan.lit.social.domain.UserReaction;
import com.ritan.lit.social.domain.enumeration.UserReactionType;
import com.ritan.lit.social.repository.UserReactionRepository;
import com.ritan.lit.social.repository.search.UserReactionSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserReactionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserReactionResourceIT {

    private static final UserReactionType DEFAULT_TYPE = UserReactionType.LIT;
    private static final UserReactionType UPDATED_TYPE = UserReactionType.LOVE;

    private static final String ENTITY_API_URL = "/api/user-reactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/user-reactions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserReactionRepository userReactionRepository;

    /**
     * This repository is mocked in the com.ritan.lit.social.repository.search test package.
     *
     * @see com.ritan.lit.social.repository.search.UserReactionSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserReactionSearchRepository mockUserReactionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserReactionMockMvc;

    private UserReaction userReaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserReaction createEntity(EntityManager em) {
        UserReaction userReaction = new UserReaction().type(DEFAULT_TYPE);
        return userReaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserReaction createUpdatedEntity(EntityManager em) {
        UserReaction userReaction = new UserReaction().type(UPDATED_TYPE);
        return userReaction;
    }

    @BeforeEach
    public void initTest() {
        userReaction = createEntity(em);
    }

    @Test
    @Transactional
    void createUserReaction() throws Exception {
        int databaseSizeBeforeCreate = userReactionRepository.findAll().size();
        // Create the UserReaction
        restUserReactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userReaction)))
            .andExpect(status().isCreated());

        // Validate the UserReaction in the database
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeCreate + 1);
        UserReaction testUserReaction = userReactionList.get(userReactionList.size() - 1);
        assertThat(testUserReaction.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the UserReaction in Elasticsearch
        verify(mockUserReactionSearchRepository, times(1)).save(testUserReaction);
    }

    @Test
    @Transactional
    void createUserReactionWithExistingId() throws Exception {
        // Create the UserReaction with an existing ID
        userReaction.setId(1L);

        int databaseSizeBeforeCreate = userReactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserReactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userReaction)))
            .andExpect(status().isBadRequest());

        // Validate the UserReaction in the database
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserReaction in Elasticsearch
        verify(mockUserReactionSearchRepository, times(0)).save(userReaction);
    }

    @Test
    @Transactional
    void getAllUserReactions() throws Exception {
        // Initialize the database
        userReactionRepository.saveAndFlush(userReaction);

        // Get all the userReactionList
        restUserReactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userReaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getUserReaction() throws Exception {
        // Initialize the database
        userReactionRepository.saveAndFlush(userReaction);

        // Get the userReaction
        restUserReactionMockMvc
            .perform(get(ENTITY_API_URL_ID, userReaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userReaction.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserReaction() throws Exception {
        // Get the userReaction
        restUserReactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserReaction() throws Exception {
        // Initialize the database
        userReactionRepository.saveAndFlush(userReaction);

        int databaseSizeBeforeUpdate = userReactionRepository.findAll().size();

        // Update the userReaction
        UserReaction updatedUserReaction = userReactionRepository.findById(userReaction.getId()).get();
        // Disconnect from session so that the updates on updatedUserReaction are not directly saved in db
        em.detach(updatedUserReaction);
        updatedUserReaction.type(UPDATED_TYPE);

        restUserReactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserReaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserReaction))
            )
            .andExpect(status().isOk());

        // Validate the UserReaction in the database
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeUpdate);
        UserReaction testUserReaction = userReactionList.get(userReactionList.size() - 1);
        assertThat(testUserReaction.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the UserReaction in Elasticsearch
        verify(mockUserReactionSearchRepository).save(testUserReaction);
    }

    @Test
    @Transactional
    void putNonExistingUserReaction() throws Exception {
        int databaseSizeBeforeUpdate = userReactionRepository.findAll().size();
        userReaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserReactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userReaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userReaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReaction in the database
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserReaction in Elasticsearch
        verify(mockUserReactionSearchRepository, times(0)).save(userReaction);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserReaction() throws Exception {
        int databaseSizeBeforeUpdate = userReactionRepository.findAll().size();
        userReaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userReaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReaction in the database
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserReaction in Elasticsearch
        verify(mockUserReactionSearchRepository, times(0)).save(userReaction);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserReaction() throws Exception {
        int databaseSizeBeforeUpdate = userReactionRepository.findAll().size();
        userReaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userReaction)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserReaction in the database
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserReaction in Elasticsearch
        verify(mockUserReactionSearchRepository, times(0)).save(userReaction);
    }

    @Test
    @Transactional
    void partialUpdateUserReactionWithPatch() throws Exception {
        // Initialize the database
        userReactionRepository.saveAndFlush(userReaction);

        int databaseSizeBeforeUpdate = userReactionRepository.findAll().size();

        // Update the userReaction using partial update
        UserReaction partialUpdatedUserReaction = new UserReaction();
        partialUpdatedUserReaction.setId(userReaction.getId());

        restUserReactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserReaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserReaction))
            )
            .andExpect(status().isOk());

        // Validate the UserReaction in the database
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeUpdate);
        UserReaction testUserReaction = userReactionList.get(userReactionList.size() - 1);
        assertThat(testUserReaction.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateUserReactionWithPatch() throws Exception {
        // Initialize the database
        userReactionRepository.saveAndFlush(userReaction);

        int databaseSizeBeforeUpdate = userReactionRepository.findAll().size();

        // Update the userReaction using partial update
        UserReaction partialUpdatedUserReaction = new UserReaction();
        partialUpdatedUserReaction.setId(userReaction.getId());

        partialUpdatedUserReaction.type(UPDATED_TYPE);

        restUserReactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserReaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserReaction))
            )
            .andExpect(status().isOk());

        // Validate the UserReaction in the database
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeUpdate);
        UserReaction testUserReaction = userReactionList.get(userReactionList.size() - 1);
        assertThat(testUserReaction.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingUserReaction() throws Exception {
        int databaseSizeBeforeUpdate = userReactionRepository.findAll().size();
        userReaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserReactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userReaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userReaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReaction in the database
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserReaction in Elasticsearch
        verify(mockUserReactionSearchRepository, times(0)).save(userReaction);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserReaction() throws Exception {
        int databaseSizeBeforeUpdate = userReactionRepository.findAll().size();
        userReaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userReaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReaction in the database
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserReaction in Elasticsearch
        verify(mockUserReactionSearchRepository, times(0)).save(userReaction);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserReaction() throws Exception {
        int databaseSizeBeforeUpdate = userReactionRepository.findAll().size();
        userReaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReactionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userReaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserReaction in the database
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserReaction in Elasticsearch
        verify(mockUserReactionSearchRepository, times(0)).save(userReaction);
    }

    @Test
    @Transactional
    void deleteUserReaction() throws Exception {
        // Initialize the database
        userReactionRepository.saveAndFlush(userReaction);

        int databaseSizeBeforeDelete = userReactionRepository.findAll().size();

        // Delete the userReaction
        restUserReactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, userReaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserReaction> userReactionList = userReactionRepository.findAll();
        assertThat(userReactionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserReaction in Elasticsearch
        verify(mockUserReactionSearchRepository, times(1)).deleteById(userReaction.getId());
    }

    @Test
    @Transactional
    void searchUserReaction() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        userReactionRepository.saveAndFlush(userReaction);
        when(mockUserReactionSearchRepository.search("id:" + userReaction.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(userReaction), PageRequest.of(0, 1), 1));

        // Search the userReaction
        restUserReactionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userReaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userReaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
}
