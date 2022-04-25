package com.ritan.lit.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PortfolioUser.
 */
@Entity
@Table(name = "portfolio_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "portfoliouser")
public class PortfolioUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "jhi_user", nullable = false, unique = true)
    private Long user;

    @OneToMany(mappedBy = "portfolioUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "portfolioUser", "portfolioCurrency", "orders", "transactions" }, allowSetters = true)
    private Set<Portfolio> portfolios = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PortfolioUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser() {
        return this.user;
    }

    public PortfolioUser user(Long user) {
        this.setUser(user);
        return this;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Set<Portfolio> getPortfolios() {
        return this.portfolios;
    }

    public void setPortfolios(Set<Portfolio> portfolios) {
        if (this.portfolios != null) {
            this.portfolios.forEach(i -> i.setPortfolioUser(null));
        }
        if (portfolios != null) {
            portfolios.forEach(i -> i.setPortfolioUser(this));
        }
        this.portfolios = portfolios;
    }

    public PortfolioUser portfolios(Set<Portfolio> portfolios) {
        this.setPortfolios(portfolios);
        return this;
    }

    public PortfolioUser addPortfolio(Portfolio portfolio) {
        this.portfolios.add(portfolio);
        portfolio.setPortfolioUser(this);
        return this;
    }

    public PortfolioUser removePortfolio(Portfolio portfolio) {
        this.portfolios.remove(portfolio);
        portfolio.setPortfolioUser(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PortfolioUser)) {
            return false;
        }
        return id != null && id.equals(((PortfolioUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PortfolioUser{" +
            "id=" + getId() +
            ", user=" + getUser() +
            "}";
    }
}
