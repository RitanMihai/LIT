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
 * A Comment.
 */
@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "date")
    private Instant date;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private LanguageType language;

    @ManyToOne
    @JsonIgnoreProperties(value = { "reports", "posts", "comments", "replies", "userReactions", "userFollowings" }, allowSetters = true)
    private SocialUser socialUser;

    @ManyToOne
    @JsonIgnoreProperties(value = { "socialUser", "reports", "comments", "userReactions", "tags" }, allowSetters = true)
    private Post post;

    @OneToMany(mappedBy = "comment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "post", "comment", "reply", "socialUser" }, allowSetters = true)
    private Set<Report> reports = new HashSet<>();

    @OneToMany(mappedBy = "comment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "socialUser", "comment", "reports", "tags" }, allowSetters = true)
    private Set<Reply> replies = new HashSet<>();

    @ManyToMany(mappedBy = "comments")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "posts", "comments", "replies" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Comment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public Comment content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getDate() {
        return this.date;
    }

    public Comment date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public LanguageType getLanguage() {
        return this.language;
    }

    public Comment language(LanguageType language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(LanguageType language) {
        this.language = language;
    }

    public SocialUser getSocialUser() {
        return this.socialUser;
    }

    public void setSocialUser(SocialUser socialUser) {
        this.socialUser = socialUser;
    }

    public Comment socialUser(SocialUser socialUser) {
        this.setSocialUser(socialUser);
        return this;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment post(Post post) {
        this.setPost(post);
        return this;
    }

    public Set<Report> getReports() {
        return this.reports;
    }

    public void setReports(Set<Report> reports) {
        if (this.reports != null) {
            this.reports.forEach(i -> i.setComment(null));
        }
        if (reports != null) {
            reports.forEach(i -> i.setComment(this));
        }
        this.reports = reports;
    }

    public Comment reports(Set<Report> reports) {
        this.setReports(reports);
        return this;
    }

    public Comment addReport(Report report) {
        this.reports.add(report);
        report.setComment(this);
        return this;
    }

    public Comment removeReport(Report report) {
        this.reports.remove(report);
        report.setComment(null);
        return this;
    }

    public Set<Reply> getReplies() {
        return this.replies;
    }

    public void setReplies(Set<Reply> replies) {
        if (this.replies != null) {
            this.replies.forEach(i -> i.setComment(null));
        }
        if (replies != null) {
            replies.forEach(i -> i.setComment(this));
        }
        this.replies = replies;
    }

    public Comment replies(Set<Reply> replies) {
        this.setReplies(replies);
        return this;
    }

    public Comment addReply(Reply reply) {
        this.replies.add(reply);
        reply.setComment(this);
        return this;
    }

    public Comment removeReply(Reply reply) {
        this.replies.remove(reply);
        reply.setComment(null);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        if (this.tags != null) {
            this.tags.forEach(i -> i.removeComment(this));
        }
        if (tags != null) {
            tags.forEach(i -> i.addComment(this));
        }
        this.tags = tags;
    }

    public Comment tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Comment addTag(Tag tag) {
        this.tags.add(tag);
        tag.getComments().add(this);
        return this;
    }

    public Comment removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getComments().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comment)) {
            return false;
        }
        return id != null && id.equals(((Comment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Comment{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", date='" + getDate() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
}
