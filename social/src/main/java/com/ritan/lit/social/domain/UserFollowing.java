package com.ritan.lit.social.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserFollowing.
 */
@Entity
@Table(name = "user_following")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userfollowing")
public class UserFollowing implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "stock")
    private String stock;

    @ManyToMany
    @JoinTable(
        name = "rel_user_following__social_user",
        joinColumns = @JoinColumn(name = "user_following_id"),
        inverseJoinColumns = @JoinColumn(name = "social_user_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "reports", "posts", "comments", "replies", "userReactions", "userFollowings" }, allowSetters = true)
    private Set<SocialUser> socialUsers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserFollowing id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStock() {
        return this.stock;
    }

    public UserFollowing stock(String stock) {
        this.setStock(stock);
        return this;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public Set<SocialUser> getSocialUsers() {
        return this.socialUsers;
    }

    public void setSocialUsers(Set<SocialUser> socialUsers) {
        this.socialUsers = socialUsers;
    }

    public UserFollowing socialUsers(Set<SocialUser> socialUsers) {
        this.setSocialUsers(socialUsers);
        return this;
    }

    public UserFollowing addSocialUser(SocialUser socialUser) {
        this.socialUsers.add(socialUser);
        socialUser.getUserFollowings().add(this);
        return this;
    }

    public UserFollowing removeSocialUser(SocialUser socialUser) {
        this.socialUsers.remove(socialUser);
        socialUser.getUserFollowings().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserFollowing)) {
            return false;
        }
        return id != null && id.equals(((UserFollowing) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserFollowing{" +
            "id=" + getId() +
            ", stock=" + getStock() +
            "}";
    }
}
