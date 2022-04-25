package com.ritan.lit.social.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SocialUser.
 */
@Entity
@Table(name = "social_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "socialuser")
public class SocialUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "jhi_user", nullable = false, unique = true)
    private Long user;

    @OneToMany(mappedBy = "socialUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "post", "comment", "reply", "socialUser" }, allowSetters = true)
    private Set<Report> reports = new HashSet<>();

    @OneToMany(mappedBy = "socialUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "socialUser", "reports", "comments", "userReactions", "tags" }, allowSetters = true)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "socialUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "socialUser", "post", "reports", "replies", "tags" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "socialUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "socialUser", "comment", "reports", "tags" }, allowSetters = true)
    private Set<Reply> replies = new HashSet<>();

    @OneToMany(mappedBy = "socialUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "post", "socialUser" }, allowSetters = true)
    private Set<UserReaction> userReactions = new HashSet<>();

    @ManyToMany(mappedBy = "socialUsers")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "socialUsers" }, allowSetters = true)
    private Set<UserFollowing> userFollowings = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SocialUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser() {
        return this.user;
    }

    public SocialUser user(Long user) {
        this.setUser(user);
        return this;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Set<Report> getReports() {
        return this.reports;
    }

    public void setReports(Set<Report> reports) {
        if (this.reports != null) {
            this.reports.forEach(i -> i.setSocialUser(null));
        }
        if (reports != null) {
            reports.forEach(i -> i.setSocialUser(this));
        }
        this.reports = reports;
    }

    public SocialUser reports(Set<Report> reports) {
        this.setReports(reports);
        return this;
    }

    public SocialUser addReport(Report report) {
        this.reports.add(report);
        report.setSocialUser(this);
        return this;
    }

    public SocialUser removeReport(Report report) {
        this.reports.remove(report);
        report.setSocialUser(null);
        return this;
    }

    public Set<Post> getPosts() {
        return this.posts;
    }

    public void setPosts(Set<Post> posts) {
        if (this.posts != null) {
            this.posts.forEach(i -> i.setSocialUser(null));
        }
        if (posts != null) {
            posts.forEach(i -> i.setSocialUser(this));
        }
        this.posts = posts;
    }

    public SocialUser posts(Set<Post> posts) {
        this.setPosts(posts);
        return this;
    }

    public SocialUser addPost(Post post) {
        this.posts.add(post);
        post.setSocialUser(this);
        return this;
    }

    public SocialUser removePost(Post post) {
        this.posts.remove(post);
        post.setSocialUser(null);
        return this;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setSocialUser(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setSocialUser(this));
        }
        this.comments = comments;
    }

    public SocialUser comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public SocialUser addComment(Comment comment) {
        this.comments.add(comment);
        comment.setSocialUser(this);
        return this;
    }

    public SocialUser removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setSocialUser(null);
        return this;
    }

    public Set<Reply> getReplies() {
        return this.replies;
    }

    public void setReplies(Set<Reply> replies) {
        if (this.replies != null) {
            this.replies.forEach(i -> i.setSocialUser(null));
        }
        if (replies != null) {
            replies.forEach(i -> i.setSocialUser(this));
        }
        this.replies = replies;
    }

    public SocialUser replies(Set<Reply> replies) {
        this.setReplies(replies);
        return this;
    }

    public SocialUser addReply(Reply reply) {
        this.replies.add(reply);
        reply.setSocialUser(this);
        return this;
    }

    public SocialUser removeReply(Reply reply) {
        this.replies.remove(reply);
        reply.setSocialUser(null);
        return this;
    }

    public Set<UserReaction> getUserReactions() {
        return this.userReactions;
    }

    public void setUserReactions(Set<UserReaction> userReactions) {
        if (this.userReactions != null) {
            this.userReactions.forEach(i -> i.setSocialUser(null));
        }
        if (userReactions != null) {
            userReactions.forEach(i -> i.setSocialUser(this));
        }
        this.userReactions = userReactions;
    }

    public SocialUser userReactions(Set<UserReaction> userReactions) {
        this.setUserReactions(userReactions);
        return this;
    }

    public SocialUser addUserReaction(UserReaction userReaction) {
        this.userReactions.add(userReaction);
        userReaction.setSocialUser(this);
        return this;
    }

    public SocialUser removeUserReaction(UserReaction userReaction) {
        this.userReactions.remove(userReaction);
        userReaction.setSocialUser(null);
        return this;
    }

    public Set<UserFollowing> getUserFollowings() {
        return this.userFollowings;
    }

    public void setUserFollowings(Set<UserFollowing> userFollowings) {
        if (this.userFollowings != null) {
            this.userFollowings.forEach(i -> i.removeSocialUser(this));
        }
        if (userFollowings != null) {
            userFollowings.forEach(i -> i.addSocialUser(this));
        }
        this.userFollowings = userFollowings;
    }

    public SocialUser userFollowings(Set<UserFollowing> userFollowings) {
        this.setUserFollowings(userFollowings);
        return this;
    }

    public SocialUser addUserFollowing(UserFollowing userFollowing) {
        this.userFollowings.add(userFollowing);
        userFollowing.getSocialUsers().add(this);
        return this;
    }

    public SocialUser removeUserFollowing(UserFollowing userFollowing) {
        this.userFollowings.remove(userFollowing);
        userFollowing.getSocialUsers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SocialUser)) {
            return false;
        }
        return id != null && id.equals(((SocialUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SocialUser{" +
            "id=" + getId() +
            ", user=" + getUser() +
            "}";
    }
}
