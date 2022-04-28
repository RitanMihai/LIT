package com.ritan.lit.social.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.social.IntegrationTest;
import com.ritan.lit.social.domain.SocialUser;
import com.ritan.lit.social.repository.SocialUserRepository;
import com.ritan.lit.social.repository.search.SocialUserSearchRepository;
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
 * Integration tests for the {@link SocialUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SocialUserResourceIT {

    private static final String DEFAULT_USER = "AAAAAAAAAA";
    private static final String UPDATED_USER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/social-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/social-users";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SocialUserRepository socialUserRepository;

    /**
     * This repository is mocked in the com.ritan.lit.social.repository.search test package.
     *
     * @see com.ritan.lit.social.repository.search.SocialUserSearchRepositoryMockConfiguration
     */
    @Autowired
    private SocialUserSearchRepository mockSocialUserSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSocialUserMockMvc;

    private SocialUser socialUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SocialUser createEntity(EntityManager em) {
        SocialUser socialUser = new SocialUser().user(DEFAULT_USER);
        return socialUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SocialUser createUpdatedEntity(EntityManager em) {
        SocialUser socialUser = new SocialUser().user(UPDATED_USER);
        return socialUser;
    }

    @BeforeEach
    public void initTest() {
        socialUser = createEntity(em);
    }

    @Test
    @Transactional
    void createSocialUser() throws Exception {
        int databaseSizeBeforeCreate = socialUserRepository.findAll().size();
        // Create the SocialUser
        restSocialUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(socialUser)))
            .andExpect(status().isCreated());

        // Validate the SocialUser in the database
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeCreate + 1);
        SocialUser testSocialUser = socialUserList.get(socialUserList.size() - 1);
        assertThat(testSocialUser.getUser()).isEqualTo(DEFAULT_USER);

        // Validate the SocialUser in Elasticsearch
        verify(mockSocialUserSearchRepository, times(1)).save(testSocialUser);
    }

    @Test
    @Transactional
    void createSocialUserWithExistingId() throws Exception {
        // Create the SocialUser with an existing ID
        socialUser.setId(1L);

        int databaseSizeBeforeCreate = socialUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSocialUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(socialUser)))
            .andExpect(status().isBadRequest());

        // Validate the SocialUser in the database
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeCreate);

        // Validate the SocialUser in Elasticsearch
        verify(mockSocialUserSearchRepository, times(0)).save(socialUser);
    }

    @Test
    @Transactional
    void checkUserIsRequired() throws Exception {
        int databaseSizeBeforeTest = socialUserRepository.findAll().size();
        // set the field null
        socialUser.setUser(null);

        // Create the SocialUser, which fails.

        restSocialUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(socialUser)))
            .andExpect(status().isBadRequest());

        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSocialUsers() throws Exception {
        // Initialize the database
        socialUserRepository.saveAndFlush(socialUser);

        // Get all the socialUserList
        restSocialUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(socialUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER)));
    }

    @Test
    @Transactional
    void getSocialUser() throws Exception {
        // Initialize the database
        socialUserRepository.saveAndFlush(socialUser);

        // Get the socialUser
        restSocialUserMockMvc
            .perform(get(ENTITY_API_URL_ID, socialUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(socialUser.getId().intValue()))
            .andExpect(jsonPath("$.user").value(DEFAULT_USER));
    }

    @Test
    @Transactional
    void getNonExistingSocialUser() throws Exception {
        // Get the socialUser
        restSocialUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSocialUser() throws Exception {
        // Initialize the database
        socialUserRepository.saveAndFlush(socialUser);

        int databaseSizeBeforeUpdate = socialUserRepository.findAll().size();

        // Update the socialUser
        SocialUser updatedSocialUser = socialUserRepository.findById(socialUser.getId()).get();
        // Disconnect from session so that the updates on updatedSocialUser are not directly saved in db
        em.detach(updatedSocialUser);
        updatedSocialUser.user(UPDATED_USER);

        restSocialUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSocialUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSocialUser))
            )
            .andExpect(status().isOk());

        // Validate the SocialUser in the database
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeUpdate);
        SocialUser testSocialUser = socialUserList.get(socialUserList.size() - 1);
        assertThat(testSocialUser.getUser()).isEqualTo(UPDATED_USER);

        // Validate the SocialUser in Elasticsearch
        verify(mockSocialUserSearchRepository).save(testSocialUser);
    }

    @Test
    @Transactional
    void putNonExistingSocialUser() throws Exception {
        int databaseSizeBeforeUpdate = socialUserRepository.findAll().size();
        socialUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSocialUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, socialUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(socialUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialUser in the database
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SocialUser in Elasticsearch
        verify(mockSocialUserSearchRepository, times(0)).save(socialUser);
    }

    @Test
    @Transactional
    void putWithIdMismatchSocialUser() throws Exception {
        int databaseSizeBeforeUpdate = socialUserRepository.findAll().size();
        socialUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(socialUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialUser in the database
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SocialUser in Elasticsearch
        verify(mockSocialUserSearchRepository, times(0)).save(socialUser);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSocialUser() throws Exception {
        int databaseSizeBeforeUpdate = socialUserRepository.findAll().size();
        socialUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(socialUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SocialUser in the database
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SocialUser in Elasticsearch
        verify(mockSocialUserSearchRepository, times(0)).save(socialUser);
    }

    @Test
    @Transactional
    void partialUpdateSocialUserWithPatch() throws Exception {
        // Initialize the database
        socialUserRepository.saveAndFlush(socialUser);

        int databaseSizeBeforeUpdate = socialUserRepository.findAll().size();

        // Update the socialUser using partial update
        SocialUser partialUpdatedSocialUser = new SocialUser();
        partialUpdatedSocialUser.setId(socialUser.getId());

        partialUpdatedSocialUser.user(UPDATED_USER);

        restSocialUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSocialUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSocialUser))
            )
            .andExpect(status().isOk());

        // Validate the SocialUser in the database
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeUpdate);
        SocialUser testSocialUser = socialUserList.get(socialUserList.size() - 1);
        assertThat(testSocialUser.getUser()).isEqualTo(UPDATED_USER);
    }

    @Test
    @Transactional
    void fullUpdateSocialUserWithPatch() throws Exception {
        // Initialize the database
        socialUserRepository.saveAndFlush(socialUser);

        int databaseSizeBeforeUpdate = socialUserRepository.findAll().size();

        // Update the socialUser using partial update
        SocialUser partialUpdatedSocialUser = new SocialUser();
        partialUpdatedSocialUser.setId(socialUser.getId());

        partialUpdatedSocialUser.user(UPDATED_USER);

        restSocialUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSocialUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSocialUser))
            )
            .andExpect(status().isOk());

        // Validate the SocialUser in the database
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeUpdate);
        SocialUser testSocialUser = socialUserList.get(socialUserList.size() - 1);
        assertThat(testSocialUser.getUser()).isEqualTo(UPDATED_USER);
    }

    @Test
    @Transactional
    void patchNonExistingSocialUser() throws Exception {
        int databaseSizeBeforeUpdate = socialUserRepository.findAll().size();
        socialUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSocialUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, socialUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(socialUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialUser in the database
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SocialUser in Elasticsearch
        verify(mockSocialUserSearchRepository, times(0)).save(socialUser);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSocialUser() throws Exception {
        int databaseSizeBeforeUpdate = socialUserRepository.findAll().size();
        socialUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(socialUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialUser in the database
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SocialUser in Elasticsearch
        verify(mockSocialUserSearchRepository, times(0)).save(socialUser);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSocialUser() throws Exception {
        int databaseSizeBeforeUpdate = socialUserRepository.findAll().size();
        socialUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialUserMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(socialUser))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SocialUser in the database
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SocialUser in Elasticsearch
        verify(mockSocialUserSearchRepository, times(0)).save(socialUser);
    }

    @Test
    @Transactional
    void deleteSocialUser() throws Exception {
        // Initialize the database
        socialUserRepository.saveAndFlush(socialUser);

        int databaseSizeBeforeDelete = socialUserRepository.findAll().size();

        // Delete the socialUser
        restSocialUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, socialUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SocialUser> socialUserList = socialUserRepository.findAll();
        assertThat(socialUserList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the SocialUser in Elasticsearch
        verify(mockSocialUserSearchRepository, times(1)).deleteById(socialUser.getId());
    }

    @Test
    @Transactional
    void searchSocialUser() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        socialUserRepository.saveAndFlush(socialUser);
        when(mockSocialUserSearchRepository.search("id:" + socialUser.getId())).thenReturn(Stream.of(socialUser));

        // Search the socialUser
        restSocialUserMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + socialUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(socialUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER)));
    }
}
