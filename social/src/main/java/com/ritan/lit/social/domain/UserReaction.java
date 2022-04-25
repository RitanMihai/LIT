package com.ritan.lit.social.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ritan.lit.social.domain.enumeration.UserReactionType;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserReaction.
 */
@Entity
@Table(name = "user_reaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userreaction")
public class UserReaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private UserReactionType type;

    @ManyToOne
    @JsonIgnoreProperties(value = { "socialUser", "reports", "comments", "userReactions", "tags" }, allowSetters = true)
    private Post post;

    @ManyToOne
    @JsonIgnoreProperties(value = { "reports", "posts", "comments", "replies", "userReactions", "userFollowings" }, allowSetters = true)
    private SocialUser socialUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserReaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserReactionType getType() {
        return this.type;
    }

    public UserReaction type(UserReactionType type) {
        this.setType(type);
        return this;
    }

    public void setType(UserReactionType type) {
        this.type = type;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public UserReaction post(Post post) {
        this.setPost(post);
        return this;
    }

    public SocialUser getSocialUser() {
        return this.socialUser;
    }

    public void setSocialUser(SocialUser socialUser) {
        this.socialUser = socialUser;
    }

    public UserReaction socialUser(SocialUser socialUser) {
        this.setSocialUser(socialUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserReaction)) {
            return false;
        }
        return id != null && id.equals(((UserReaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserReaction{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            "}";
    }
}
