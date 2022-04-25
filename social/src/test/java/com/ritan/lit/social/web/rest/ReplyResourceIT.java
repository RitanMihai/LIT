package com.ritan.lit.social.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ritan.lit.social.IntegrationTest;
import com.ritan.lit.social.domain.Reply;
import com.ritan.lit.social.domain.enumeration.LanguageType;
import com.ritan.lit.social.repository.ReplyRepository;
import com.ritan.lit.social.repository.search.ReplySearchRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ReplyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReplyResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final LanguageType DEFAULT_LANGUAGE = LanguageType.ENG;
    private static final LanguageType UPDATED_LANGUAGE = LanguageType.RO;

    private static final String ENTITY_API_URL = "/api/replies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/replies";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReplyRepository replyRepository;

    /**
     * This repository is mocked in the com.ritan.lit.social.repository.search test package.
     *
     * @see com.ritan.lit.social.repository.search.ReplySearchRepositoryMockConfiguration
     */
    @Autowired
    private ReplySearchRepository mockReplySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReplyMockMvc;

    private Reply reply;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reply createEntity(EntityManager em) {
        Reply reply = new Reply().content(DEFAULT_CONTENT).date(DEFAULT_DATE).language(DEFAULT_LANGUAGE);
        return reply;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reply createUpdatedEntity(EntityManager em) {
        Reply reply = new Reply().content(UPDATED_CONTENT).date(UPDATED_DATE).language(UPDATED_LANGUAGE);
        return reply;
    }

    @BeforeEach
    public void initTest() {
        reply = createEntity(em);
    }

    @Test
    @Transactional
    void createReply() throws Exception {
        int databaseSizeBeforeCreate = replyRepository.findAll().size();
        // Create the Reply
        restReplyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reply)))
            .andExpect(status().isCreated());

        // Validate the Reply in the database
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeCreate + 1);
        Reply testReply = replyList.get(replyList.size() - 1);
        assertThat(testReply.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testReply.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testReply.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);

        // Validate the Reply in Elasticsearch
        verify(mockReplySearchRepository, times(1)).save(testReply);
    }

    @Test
    @Transactional
    void createReplyWithExistingId() throws Exception {
        // Create the Reply with an existing ID
        reply.setId(1L);

        int databaseSizeBeforeCreate = replyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReplyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reply)))
            .andExpect(status().isBadRequest());

        // Validate the Reply in the database
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeCreate);

        // Validate the Reply in Elasticsearch
        verify(mockReplySearchRepository, times(0)).save(reply);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = replyRepository.findAll().size();
        // set the field null
        reply.setContent(null);

        // Create the Reply, which fails.

        restReplyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reply)))
            .andExpect(status().isBadRequest());

        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReplies() throws Exception {
        // Initialize the database
        replyRepository.saveAndFlush(reply);

        // Get all the replyList
        restReplyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reply.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }

    @Test
    @Transactional
    void getReply() throws Exception {
        // Initialize the database
        replyRepository.saveAndFlush(reply);

        // Get the reply
        restReplyMockMvc
            .perform(get(ENTITY_API_URL_ID, reply.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reply.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingReply() throws Exception {
        // Get the reply
        restReplyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewReply() throws Exception {
        // Initialize the database
        replyRepository.saveAndFlush(reply);

        int databaseSizeBeforeUpdate = replyRepository.findAll().size();

        // Update the reply
        Reply updatedReply = replyRepository.findById(reply.getId()).get();
        // Disconnect from session so that the updates on updatedReply are not directly saved in db
        em.detach(updatedReply);
        updatedReply.content(UPDATED_CONTENT).date(UPDATED_DATE).language(UPDATED_LANGUAGE);

        restReplyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReply.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedReply))
            )
            .andExpect(status().isOk());

        // Validate the Reply in the database
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeUpdate);
        Reply testReply = replyList.get(replyList.size() - 1);
        assertThat(testReply.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testReply.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testReply.getLanguage()).isEqualTo(UPDATED_LANGUAGE);

        // Validate the Reply in Elasticsearch
        verify(mockReplySearchRepository).save(testReply);
    }

    @Test
    @Transactional
    void putNonExistingReply() throws Exception {
        int databaseSizeBeforeUpdate = replyRepository.findAll().size();
        reply.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReplyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reply.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reply))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reply in the database
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Reply in Elasticsearch
        verify(mockReplySearchRepository, times(0)).save(reply);
    }

    @Test
    @Transactional
    void putWithIdMismatchReply() throws Exception {
        int databaseSizeBeforeUpdate = replyRepository.findAll().size();
        reply.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReplyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reply))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reply in the database
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Reply in Elasticsearch
        verify(mockReplySearchRepository, times(0)).save(reply);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReply() throws Exception {
        int databaseSizeBeforeUpdate = replyRepository.findAll().size();
        reply.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReplyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reply)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reply in the database
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Reply in Elasticsearch
        verify(mockReplySearchRepository, times(0)).save(reply);
    }

    @Test
    @Transactional
    void partialUpdateReplyWithPatch() throws Exception {
        // Initialize the database
        replyRepository.saveAndFlush(reply);

        int databaseSizeBeforeUpdate = replyRepository.findAll().size();

        // Update the reply using partial update
        Reply partialUpdatedReply = new Reply();
        partialUpdatedReply.setId(reply.getId());

        partialUpdatedReply.content(UPDATED_CONTENT).date(UPDATED_DATE).language(UPDATED_LANGUAGE);

        restReplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReply.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReply))
            )
            .andExpect(status().isOk());

        // Validate the Reply in the database
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeUpdate);
        Reply testReply = replyList.get(replyList.size() - 1);
        assertThat(testReply.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testReply.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testReply.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void fullUpdateReplyWithPatch() throws Exception {
        // Initialize the database
        replyRepository.saveAndFlush(reply);

        int databaseSizeBeforeUpdate = replyRepository.findAll().size();

        // Update the reply using partial update
        Reply partialUpdatedReply = new Reply();
        partialUpdatedReply.setId(reply.getId());

        partialUpdatedReply.content(UPDATED_CONTENT).date(UPDATED_DATE).language(UPDATED_LANGUAGE);

        restReplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReply.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReply))
            )
            .andExpect(status().isOk());

        // Validate the Reply in the database
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeUpdate);
        Reply testReply = replyList.get(replyList.size() - 1);
        assertThat(testReply.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testReply.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testReply.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void patchNonExistingReply() throws Exception {
        int databaseSizeBeforeUpdate = replyRepository.findAll().size();
        reply.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reply.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reply))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reply in the database
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Reply in Elasticsearch
        verify(mockReplySearchRepository, times(0)).save(reply);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReply() throws Exception {
        int databaseSizeBeforeUpdate = replyRepository.findAll().size();
        reply.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reply))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reply in the database
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Reply in Elasticsearch
        verify(mockReplySearchRepository, times(0)).save(reply);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReply() throws Exception {
        int databaseSizeBeforeUpdate = replyRepository.findAll().size();
        reply.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReplyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(reply)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reply in the database
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Reply in Elasticsearch
        verify(mockReplySearchRepository, times(0)).save(reply);
    }

    @Test
    @Transactional
    void deleteReply() throws Exception {
        // Initialize the database
        replyRepository.saveAndFlush(reply);

        int databaseSizeBeforeDelete = replyRepository.findAll().size();

        // Delete the reply
        restReplyMockMvc
            .perform(delete(ENTITY_API_URL_ID, reply.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Reply> replyList = replyRepository.findAll();
        assertThat(replyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Reply in Elasticsearch
        verify(mockReplySearchRepository, times(1)).deleteById(reply.getId());
    }

    @Test
    @Transactional
    void searchReply() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        replyRepository.saveAndFlush(reply);
        when(mockReplySearchRepository.search("id:" + reply.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(reply), PageRequest.of(0, 1), 1));

        // Search the reply
        restReplyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + reply.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reply.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }
}
