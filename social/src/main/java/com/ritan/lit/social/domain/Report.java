package com.ritan.lit.social.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ritan.lit.social.domain.enumeration.RportType;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Report.
 */
@Entity
@Table(name = "report")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "report")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RportType type;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JsonIgnoreProperties(value = { "socialUser", "reports", "comments", "userReactions", "tags" }, allowSetters = true)
    private Post post;

    @ManyToOne
    @JsonIgnoreProperties(value = { "socialUser", "post", "reports", "replies", "tags" }, allowSetters = true)
    private Comment comment;

    @ManyToOne
    @JsonIgnoreProperties(value = { "socialUser", "comment", "reports", "tags" }, allowSetters = true)
    private Reply reply;

    @ManyToOne
    @JsonIgnoreProperties(value = { "reports", "posts", "comments", "replies", "userReactions", "userFollowings" }, allowSetters = true)
    private SocialUser socialUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Report id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RportType getType() {
        return this.type;
    }

    public Report type(RportType type) {
        this.setType(type);
        return this;
    }

    public void setType(RportType type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public Report description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Report post(Post post) {
        this.setPost(post);
        return this;
    }

    public Comment getComment() {
        return this.comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Report comment(Comment comment) {
        this.setComment(comment);
        return this;
    }

    public Reply getReply() {
        return this.reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public Report reply(Reply reply) {
        this.setReply(reply);
        return this;
    }

    public SocialUser getSocialUser() {
        return this.socialUser;
    }

    public void setSocialUser(SocialUser socialUser) {
        this.socialUser = socialUser;
    }

    public Report socialUser(SocialUser socialUser) {
        this.setSocialUser(socialUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Report)) {
            return false;
        }
        return id != null && id.equals(((Report) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Report{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
