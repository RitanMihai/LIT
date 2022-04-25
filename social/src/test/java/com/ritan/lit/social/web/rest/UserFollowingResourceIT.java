package com.ritan.lit.social.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.social.IntegrationTest;
import com.ritan.lit.social.domain.UserFollowing;
import com.ritan.lit.social.repository.UserFollowingRepository;
import com.ritan.lit.social.repository.search.UserFollowingSearchRepository;
import com.ritan.lit.social.service.UserFollowingService;
import java.util.ArrayList;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserFollowingResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserFollowingResourceIT {

    private static final Long DEFAULT_STOCK = 1L;
    private static final Long UPDATED_STOCK = 2L;

    private static final String ENTITY_API_URL = "/api/user-followings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/user-followings";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserFollowingRepository userFollowingRepository;

    @Mock
    private UserFollowingRepository userFollowingRepositoryMock;

    @Mock
    private UserFollowingService userFollowingServiceMock;

    /**
     * This repository is mocked in the com.ritan.lit.social.repository.search test package.
     *
     * @see com.ritan.lit.social.repository.search.UserFollowingSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserFollowingSearchRepository mockUserFollowingSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserFollowingMockMvc;

    private UserFollowing userFollowing;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserFollowing createEntity(EntityManager em) {
        UserFollowing userFollowing = new UserFollowing().stock(DEFAULT_STOCK);
        return userFollowing;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserFollowing createUpdatedEntity(EntityManager em) {
        UserFollowing userFollowing = new UserFollowing().stock(UPDATED_STOCK);
        return userFollowing;
    }

    @BeforeEach
    public void initTest() {
        userFollowing = createEntity(em);
    }

    @Test
    @Transactional
    void createUserFollowing() throws Exception {
        int databaseSizeBeforeCreate = userFollowingRepository.findAll().size();
        // Create the UserFollowing
        restUserFollowingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userFollowing)))
            .andExpect(status().isCreated());

        // Validate the UserFollowing in the database
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeCreate + 1);
        UserFollowing testUserFollowing = userFollowingList.get(userFollowingList.size() - 1);
        assertThat(testUserFollowing.getStock()).isEqualTo(DEFAULT_STOCK);

        // Validate the UserFollowing in Elasticsearch
        verify(mockUserFollowingSearchRepository, times(1)).save(testUserFollowing);
    }

    @Test
    @Transactional
    void createUserFollowingWithExistingId() throws Exception {
        // Create the UserFollowing with an existing ID
        userFollowing.setId(1L);

        int databaseSizeBeforeCreate = userFollowingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserFollowingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userFollowing)))
            .andExpect(status().isBadRequest());

        // Validate the UserFollowing in the database
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserFollowing in Elasticsearch
        verify(mockUserFollowingSearchRepository, times(0)).save(userFollowing);
    }

    @Test
    @Transactional
    void getAllUserFollowings() throws Exception {
        // Initialize the database
        userFollowingRepository.saveAndFlush(userFollowing);

        // Get all the userFollowingList
        restUserFollowingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userFollowing.getId().intValue())))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserFollowingsWithEagerRelationshipsIsEnabled() throws Exception {
        when(userFollowingServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserFollowingMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userFollowingServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserFollowingsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userFollowingServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserFollowingMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userFollowingServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getUserFollowing() throws Exception {
        // Initialize the database
        userFollowingRepository.saveAndFlush(userFollowing);

        // Get the userFollowing
        restUserFollowingMockMvc
            .perform(get(ENTITY_API_URL_ID, userFollowing.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userFollowing.getId().intValue()))
            .andExpect(jsonPath("$.stock").value(DEFAULT_STOCK.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingUserFollowing() throws Exception {
        // Get the userFollowing
        restUserFollowingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserFollowing() throws Exception {
        // Initialize the database
        userFollowingRepository.saveAndFlush(userFollowing);

        int databaseSizeBeforeUpdate = userFollowingRepository.findAll().size();

        // Update the userFollowing
        UserFollowing updatedUserFollowing = userFollowingRepository.findById(userFollowing.getId()).get();
        // Disconnect from session so that the updates on updatedUserFollowing are not directly saved in db
        em.detach(updatedUserFollowing);
        updatedUserFollowing.stock(UPDATED_STOCK);

        restUserFollowingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserFollowing.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserFollowing))
            )
            .andExpect(status().isOk());

        // Validate the UserFollowing in the database
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeUpdate);
        UserFollowing testUserFollowing = userFollowingList.get(userFollowingList.size() - 1);
        assertThat(testUserFollowing.getStock()).isEqualTo(UPDATED_STOCK);

        // Validate the UserFollowing in Elasticsearch
        verify(mockUserFollowingSearchRepository).save(testUserFollowing);
    }

    @Test
    @Transactional
    void putNonExistingUserFollowing() throws Exception {
        int databaseSizeBeforeUpdate = userFollowingRepository.findAll().size();
        userFollowing.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserFollowingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userFollowing.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userFollowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserFollowing in the database
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserFollowing in Elasticsearch
        verify(mockUserFollowingSearchRepository, times(0)).save(userFollowing);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserFollowing() throws Exception {
        int databaseSizeBeforeUpdate = userFollowingRepository.findAll().size();
        userFollowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserFollowingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userFollowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserFollowing in the database
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserFollowing in Elasticsearch
        verify(mockUserFollowingSearchRepository, times(0)).save(userFollowing);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserFollowing() throws Exception {
        int databaseSizeBeforeUpdate = userFollowingRepository.findAll().size();
        userFollowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserFollowingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userFollowing)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserFollowing in the database
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserFollowing in Elasticsearch
        verify(mockUserFollowingSearchRepository, times(0)).save(userFollowing);
    }

    @Test
    @Transactional
    void partialUpdateUserFollowingWithPatch() throws Exception {
        // Initialize the database
        userFollowingRepository.saveAndFlush(userFollowing);

        int databaseSizeBeforeUpdate = userFollowingRepository.findAll().size();

        // Update the userFollowing using partial update
        UserFollowing partialUpdatedUserFollowing = new UserFollowing();
        partialUpdatedUserFollowing.setId(userFollowing.getId());

        partialUpdatedUserFollowing.stock(UPDATED_STOCK);

        restUserFollowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserFollowing.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserFollowing))
            )
            .andExpect(status().isOk());

        // Validate the UserFollowing in the database
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeUpdate);
        UserFollowing testUserFollowing = userFollowingList.get(userFollowingList.size() - 1);
        assertThat(testUserFollowing.getStock()).isEqualTo(UPDATED_STOCK);
    }

    @Test
    @Transactional
    void fullUpdateUserFollowingWithPatch() throws Exception {
        // Initialize the database
        userFollowingRepository.saveAndFlush(userFollowing);

        int databaseSizeBeforeUpdate = userFollowingRepository.findAll().size();

        // Update the userFollowing using partial update
        UserFollowing partialUpdatedUserFollowing = new UserFollowing();
        partialUpdatedUserFollowing.setId(userFollowing.getId());

        partialUpdatedUserFollowing.stock(UPDATED_STOCK);

        restUserFollowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserFollowing.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserFollowing))
            )
            .andExpect(status().isOk());

        // Validate the UserFollowing in the database
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeUpdate);
        UserFollowing testUserFollowing = userFollowingList.get(userFollowingList.size() - 1);
        assertThat(testUserFollowing.getStock()).isEqualTo(UPDATED_STOCK);
    }

    @Test
    @Transactional
    void patchNonExistingUserFollowing() throws Exception {
        int databaseSizeBeforeUpdate = userFollowingRepository.findAll().size();
        userFollowing.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserFollowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userFollowing.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userFollowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserFollowing in the database
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserFollowing in Elasticsearch
        verify(mockUserFollowingSearchRepository, times(0)).save(userFollowing);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserFollowing() throws Exception {
        int databaseSizeBeforeUpdate = userFollowingRepository.findAll().size();
        userFollowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserFollowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userFollowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserFollowing in the database
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserFollowing in Elasticsearch
        verify(mockUserFollowingSearchRepository, times(0)).save(userFollowing);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserFollowing() throws Exception {
        int databaseSizeBeforeUpdate = userFollowingRepository.findAll().size();
        userFollowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserFollowingMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userFollowing))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserFollowing in the database
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserFollowing in Elasticsearch
        verify(mockUserFollowingSearchRepository, times(0)).save(userFollowing);
    }

    @Test
    @Transactional
    void deleteUserFollowing() throws Exception {
        // Initialize the database
        userFollowingRepository.saveAndFlush(userFollowing);

        int databaseSizeBeforeDelete = userFollowingRepository.findAll().size();

        // Delete the userFollowing
        restUserFollowingMockMvc
            .perform(delete(ENTITY_API_URL_ID, userFollowing.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserFollowing> userFollowingList = userFollowingRepository.findAll();
        assertThat(userFollowingList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserFollowing in Elasticsearch
        verify(mockUserFollowingSearchRepository, times(1)).deleteById(userFollowing.getId());
    }

    @Test
    @Transactional
    void searchUserFollowing() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        userFollowingRepository.saveAndFlush(userFollowing);
        when(mockUserFollowingSearchRepository.search("id:" + userFollowing.getId())).thenReturn(Stream.of(userFollowing));

        // Search the userFollowing
        restUserFollowingMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userFollowing.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userFollowing.getId().intValue())))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK.intValue())));
    }
}
