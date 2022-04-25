package com.ritan.lit.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PortfolioCurrency.
 */
@Entity
@Table(name = "portfolio_currency")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "portfoliocurrency")
public class PortfolioCurrency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "currency_symbol")
    private String currencySymbol;

    @OneToMany(mappedBy = "portfolioCurrency")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "portfolioUser", "portfolioCurrency", "orders", "transactions" }, allowSetters = true)
    private Set<Portfolio> portfolios = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PortfolioCurrency id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public PortfolioCurrency code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public PortfolioCurrency name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrencySymbol() {
        return this.currencySymbol;
    }

    public PortfolioCurrency currencySymbol(String currencySymbol) {
        this.setCurrencySymbol(currencySymbol);
        return this;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public Set<Portfolio> getPortfolios() {
        return this.portfolios;
    }

    public void setPortfolios(Set<Portfolio> portfolios) {
        if (this.portfolios != null) {
            this.portfolios.forEach(i -> i.setPortfolioCurrency(null));
        }
        if (portfolios != null) {
            portfolios.forEach(i -> i.setPortfolioCurrency(this));
        }
        this.portfolios = portfolios;
    }

    public PortfolioCurrency portfolios(Set<Portfolio> portfolios) {
        this.setPortfolios(portfolios);
        return this;
    }

    public PortfolioCurrency addPortfolio(Portfolio portfolio) {
        this.portfolios.add(portfolio);
        portfolio.setPortfolioCurrency(this);
        return this;
    }

    public PortfolioCurrency removePortfolio(Portfolio portfolio) {
        this.portfolios.remove(portfolio);
        portfolio.setPortfolioCurrency(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PortfolioCurrency)) {
            return false;
        }
        return id != null && id.equals(((PortfolioCurrency) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PortfolioCurrency{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", currencySymbol='" + getCurrencySymbol() + "'" +
            "}";
    }
}
