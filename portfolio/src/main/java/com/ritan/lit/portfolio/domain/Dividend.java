package com.ritan.lit.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ritan.lit.portfolio.domain.enumeration.DividendType;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Dividend.
 */
@Entity
@Table(name = "dividend")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "dividend")
public class Dividend implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "date_recived")
    private Instant dateRecived;

    @Column(name = "tax_rate")
    private Integer taxRate;

    @Column(name = "total_recived")
    private Double totalRecived;

    @Enumerated(EnumType.STRING)
    @Column(name = "dividend_type")
    private DividendType dividendType;

    @ManyToOne
    @JsonIgnoreProperties(value = { "stockInfo", "portfolio", "dividends" }, allowSetters = true)
    private Order order;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Dividend id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateRecived() {
        return this.dateRecived;
    }

    public Dividend dateRecived(Instant dateRecived) {
        this.setDateRecived(dateRecived);
        return this;
    }

    public void setDateRecived(Instant dateRecived) {
        this.dateRecived = dateRecived;
    }

    public Integer getTaxRate() {
        return this.taxRate;
    }

    public Dividend taxRate(Integer taxRate) {
        this.setTaxRate(taxRate);
        return this;
    }

    public void setTaxRate(Integer taxRate) {
        this.taxRate = taxRate;
    }

    public Double getTotalRecived() {
        return this.totalRecived;
    }

    public Dividend totalRecived(Double totalRecived) {
        this.setTotalRecived(totalRecived);
        return this;
    }

    public void setTotalRecived(Double totalRecived) {
        this.totalRecived = totalRecived;
    }

    public DividendType getDividendType() {
        return this.dividendType;
    }

    public Dividend dividendType(DividendType dividendType) {
        this.setDividendType(dividendType);
        return this;
    }

    public void setDividendType(DividendType dividendType) {
        this.dividendType = dividendType;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Dividend order(Order order) {
        this.setOrder(order);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dividend)) {
            return false;
        }
        return id != null && id.equals(((Dividend) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dividend{" +
            "id=" + getId() +
            ", dateRecived='" + getDateRecived() + "'" +
            ", taxRate=" + getTaxRate() +
            ", totalRecived=" + getTotalRecived() +
            ", dividendType='" + getDividendType() + "'" +
            "}";
    }
}
