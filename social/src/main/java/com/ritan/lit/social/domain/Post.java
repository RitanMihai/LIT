package com.ritan.lit.social.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ritan.lit.social.domain.enumeration.LanguageType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Post.
 */
@Entity
@Table(name = "post")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "post")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    @Column(name = "date")
    private Instant date;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private LanguageType language;

    @Column(name = "is_payed_promotion")
    private Boolean isPayedPromotion;

    @ManyToOne
    @JsonIgnoreProperties(value = { "reports", "posts", "comments", "replies", "userReactions", "userFollowings" }, allowSetters = true)
    private SocialUser socialUser;

    @OneToMany(mappedBy = "post")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "post", "comment", "reply", "socialUser" }, allowSetters = true)
    private Set<Report> reports = new HashSet<>();

    @OneToMany(mappedBy = "post")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "socialUser", "post", "reports", "replies", "tags" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "post")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "post", "socialUser" }, allowSetters = true)
    private Set<UserReaction> userReactions = new HashSet<>();

    @ManyToMany(mappedBy = "posts")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "posts", "comments", "replies" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Post id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public Post content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Post image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Post imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Instant getDate() {
        return this.date;
    }

    public Post date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public LanguageType getLanguage() {
        return this.language;
    }

    public Post language(LanguageType language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(LanguageType language) {
        this.language = language;
    }

    public Boolean getIsPayedPromotion() {
        return this.isPayedPromotion;
    }

    public Post isPayedPromotion(Boolean isPayedPromotion) {
        this.setIsPayedPromotion(isPayedPromotion);
        return this;
    }

    public void setIsPayedPromotion(Boolean isPayedPromotion) {
        this.isPayedPromotion = isPayedPromotion;
    }

    public SocialUser getSocialUser() {
        return this.socialUser;
    }

    public void setSocialUser(SocialUser socialUser) {
        this.socialUser = socialUser;
    }

    public Post socialUser(SocialUser socialUser) {
        this.setSocialUser(socialUser);
        return this;
    }

    public Set<Report> getReports() {
        return this.reports;
    }

    public void setReports(Set<Report> reports) {
        if (this.reports != null) {
            this.reports.forEach(i -> i.setPost(null));
        }
        if (reports != null) {
            reports.forEach(i -> i.setPost(this));
        }
        this.reports = reports;
    }

    public Post reports(Set<Report> reports) {
        this.setReports(reports);
        return this;
    }

    public Post addReport(Report report) {
        this.reports.add(report);
        report.setPost(this);
        return this;
    }

    public Post removeReport(Report report) {
        this.reports.remove(report);
        report.setPost(null);
        return this;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setPost(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setPost(this));
        }
        this.comments = comments;
    }

    public Post comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Post addComment(Comment comment) {
        this.comments.add(comment);
        comment.setPost(this);
        return this;
    }

    public Post removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setPost(null);
        return this;
    }

    public Set<UserReaction> getUserReactions() {
        return this.userReactions;
    }

    public void setUserReactions(Set<UserReaction> userReactions) {
        if (this.userReactions != null) {
            this.userReactions.forEach(i -> i.setPost(null));
        }
        if (userReactions != null) {
            userReactions.forEach(i -> i.setPost(this));
        }
        this.userReactions = userReactions;
    }

    public Post userReactions(Set<UserReaction> userReactions) {
        this.setUserReactions(userReactions);
        return this;
    }

    public Post addUserReaction(UserReaction userReaction) {
        this.userReactions.add(userReaction);
        userReaction.setPost(this);
        return this;
    }

    public Post removeUserReaction(UserReaction userReaction) {
        this.userReactions.remove(userReaction);
        userReaction.setPost(null);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        if (this.tags != null) {
            this.tags.forEach(i -> i.removePost(this));
        }
        if (tags != null) {
            tags.forEach(i -> i.addPost(this));
        }
        this.tags = tags;
    }

    public Post tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Post addTag(Tag tag) {
        this.tags.add(tag);
        tag.getPosts().add(this);
        return this;
    }

    public Post removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getPosts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        return id != null && id.equals(((Post) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Post{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", date='" + getDate() + "'" +
            ", language='" + getLanguage() + "'" +
            ", isPayedPromotion='" + getIsPayedPromotion() + "'" +
            "}";
    }
}
