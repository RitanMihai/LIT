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
 * A Reply.
 */
@Entity
@Table(name = "reply")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "reply")
public class Reply implements Serializable {

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
    @JsonIgnoreProperties(value = { "socialUser", "post", "reports", "replies", "tags" }, allowSetters = true)
    private Comment comment;

    @OneToMany(mappedBy = "reply")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "post", "comment", "reply", "socialUser" }, allowSetters = true)
    private Set<Report> reports = new HashSet<>();

    @ManyToMany(mappedBy = "replies")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "posts", "comments", "replies" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reply id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public Reply content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getDate() {
        return this.date;
    }

    public Reply date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public LanguageType getLanguage() {
        return this.language;
    }

    public Reply language(LanguageType language) {
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

    public Reply socialUser(SocialUser socialUser) {
        this.setSocialUser(socialUser);
        return this;
    }

    public Comment getComment() {
        return this.comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Reply comment(Comment comment) {
        this.setComment(comment);
        return this;
    }

    public Set<Report> getReports() {
        return this.reports;
    }

    public void setReports(Set<Report> reports) {
        if (this.reports != null) {
            this.reports.forEach(i -> i.setReply(null));
        }
        if (reports != null) {
            reports.forEach(i -> i.setReply(this));
        }
        this.reports = reports;
    }

    public Reply reports(Set<Report> reports) {
        this.setReports(reports);
        return this;
    }

    public Reply addReport(Report report) {
        this.reports.add(report);
        report.setReply(this);
        return this;
    }

    public Reply removeReport(Report report) {
        this.reports.remove(report);
        report.setReply(null);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        if (this.tags != null) {
            this.tags.forEach(i -> i.removeReply(this));
        }
        if (tags != null) {
            tags.forEach(i -> i.addReply(this));
        }
        this.tags = tags;
    }

    public Reply tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Reply addTag(Tag tag) {
        this.tags.add(tag);
        tag.getReplies().add(this);
        return this;
    }

    public Reply removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getReplies().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reply)) {
            return false;
        }
        return id != null && id.equals(((Reply) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reply{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", date='" + getDate() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
}
