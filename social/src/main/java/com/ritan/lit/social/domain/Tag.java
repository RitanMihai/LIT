package com.ritan.lit.social.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tag.
 */
@Entity
@Table(name = "tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "ticker")
    private String ticker;

    @ManyToMany
    @JoinTable(name = "rel_tag__post", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "socialUser", "reports", "comments", "userReactions", "tags" }, allowSetters = true)
    private Set<Post> posts = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "rel_tag__comment", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "comment_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "socialUser", "post", "reports", "replies", "tags" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "rel_tag__reply", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "reply_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "socialUser", "comment", "reports", "tags" }, allowSetters = true)
    private Set<Reply> replies = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tag id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStockName() {
        return this.stockName;
    }

    public Tag stockName(String stockName) {
        this.setStockName(stockName);
        return this;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getTicker() {
        return this.ticker;
    }

    public Tag ticker(String ticker) {
        this.setTicker(ticker);
        return this;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Set<Post> getPosts() {
        return this.posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public Tag posts(Set<Post> posts) {
        this.setPosts(posts);
        return this;
    }

    public Tag addPost(Post post) {
        this.posts.add(post);
        post.getTags().add(this);
        return this;
    }

    public Tag removePost(Post post) {
        this.posts.remove(post);
        post.getTags().remove(this);
        return this;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Tag comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Tag addComment(Comment comment) {
        this.comments.add(comment);
        comment.getTags().add(this);
        return this;
    }

    public Tag removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.getTags().remove(this);
        return this;
    }

    public Set<Reply> getReplies() {
        return this.replies;
    }

    public void setReplies(Set<Reply> replies) {
        this.replies = replies;
    }

    public Tag replies(Set<Reply> replies) {
        this.setReplies(replies);
        return this;
    }

    public Tag addReply(Reply reply) {
        this.replies.add(reply);
        reply.getTags().add(this);
        return this;
    }

    public Tag removeReply(Reply reply) {
        this.replies.remove(reply);
        reply.getTags().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        return id != null && id.equals(((Tag) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tag{" +
            "id=" + getId() +
            ", stockName='" + getStockName() + "'" +
            ", ticker='" + getTicker() + "'" +
            "}";
    }
}
