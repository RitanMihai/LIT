package com.ritan.lit.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Portfolio.
 */
@Entity
@Table(name = "portfolio")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "portfolio")
public class Portfolio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private Double value;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    @Column(name = "unrealised_value")
    private Double unrealisedValue;

    /**
     * Only on closed positions
     */
    @Schema(description = "Only on closed positions")
    @Column(name = "profit_or_loss")
    private Double profitOrLoss;

    @ManyToOne
    @JsonIgnoreProperties(value = { "portfolios" }, allowSetters = true)
    private PortfolioUser portfolioUser;

    @ManyToOne
    @JsonIgnoreProperties(value = { "portfolios" }, allowSetters = true)
    private PortfolioCurrency portfolioCurrency;

    @OneToMany(mappedBy = "portfolio")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "stockInfo", "portfolio", "dividends" }, allowSetters = true)
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "portfolio")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "portfolio" }, allowSetters = true)
    private Set<Transaction> transactions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Portfolio id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Portfolio name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return this.value;
    }

    public Portfolio value(Double value) {
        this.setValue(value);
        return this;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Portfolio image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Portfolio imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Double getUnrealisedValue() {
        return this.unrealisedValue;
    }

    public Portfolio unrealisedValue(Double unrealisedValue) {
        this.setUnrealisedValue(unrealisedValue);
        return this;
    }

    public void setUnrealisedValue(Double unrealisedValue) {
        this.unrealisedValue = unrealisedValue;
    }

    public Double getProfitOrLoss() {
        return this.profitOrLoss;
    }

    public Portfolio profitOrLoss(Double profitOrLoss) {
        this.setProfitOrLoss(profitOrLoss);
        return this;
    }

    public void setProfitOrLoss(Double profitOrLoss) {
        this.profitOrLoss = profitOrLoss;
    }

    public PortfolioUser getPortfolioUser() {
        return this.portfolioUser;
    }

    public void setPortfolioUser(PortfolioUser portfolioUser) {
        this.portfolioUser = portfolioUser;
    }

    public Portfolio portfolioUser(PortfolioUser portfolioUser) {
        this.setPortfolioUser(portfolioUser);
        return this;
    }

    public PortfolioCurrency getPortfolioCurrency() {
        return this.portfolioCurrency;
    }

    public void setPortfolioCurrency(PortfolioCurrency portfolioCurrency) {
        this.portfolioCurrency = portfolioCurrency;
    }

    public Portfolio portfolioCurrency(PortfolioCurrency portfolioCurrency) {
        this.setPortfolioCurrency(portfolioCurrency);
        return this;
    }

    public Set<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<Order> orders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setPortfolio(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setPortfolio(this));
        }
        this.orders = orders;
    }

    public Portfolio orders(Set<Order> orders) {
        this.setOrders(orders);
        return this;
    }

    public Portfolio addOrder(Order order) {
        this.orders.add(order);
        order.setPortfolio(this);
        return this;
    }

    public Portfolio removeOrder(Order order) {
        this.orders.remove(order);
        order.setPortfolio(null);
        return this;
    }

    public Set<Transaction> getTransactions() {
        return this.transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        if (this.transactions != null) {
            this.transactions.forEach(i -> i.setPortfolio(null));
        }
        if (transactions != null) {
            transactions.forEach(i -> i.setPortfolio(this));
        }
        this.transactions = transactions;
    }

    public Portfolio transactions(Set<Transaction> transactions) {
        this.setTransactions(transactions);
        return this;
    }

    public Portfolio addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setPortfolio(this);
        return this;
    }

    public Portfolio removeTransaction(Transaction transaction) {
        this.transactions.remove(transaction);
        transaction.setPortfolio(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Portfolio)) {
            return false;
        }
        return id != null && id.equals(((Portfolio) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Portfolio{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", value=" + getValue() +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", unrealisedValue=" + getUnrealisedValue() +
            ", profitOrLoss=" + getProfitOrLoss() +
            "}";
    }
}
