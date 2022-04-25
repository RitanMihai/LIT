package com.ritan.lit.watchlist.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A IncomeHistory.
 */
@Entity
@Table(name = "income_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "incomehistory")
public class IncomeHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "total_revenue")
    private Long totalRevenue;

    @Column(name = "cost_of_revenue")
    private Long costOfRevenue;

    @Column(name = "gross_profit")
    private Long grossProfit;

    @Column(name = "operating_expense")
    private Long operatingExpense;

    @Column(name = "operating_income")
    private Long operatingIncome;

    @ManyToOne
    @JsonIgnoreProperties(
        value = {
            "stockExchange",
            "company",
            "currency",
            "priceHistories",
            "dividendHistories",
            "stockSplitHistories",
            "capitalGainHistories",
            "incomeHistories",
        },
        allowSetters = true
    )
    private Stock stock;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public IncomeHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public IncomeHistory date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getTotalRevenue() {
        return this.totalRevenue;
    }

    public IncomeHistory totalRevenue(Long totalRevenue) {
        this.setTotalRevenue(totalRevenue);
        return this;
    }

    public void setTotalRevenue(Long totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getCostOfRevenue() {
        return this.costOfRevenue;
    }

    public IncomeHistory costOfRevenue(Long costOfRevenue) {
        this.setCostOfRevenue(costOfRevenue);
        return this;
    }

    public void setCostOfRevenue(Long costOfRevenue) {
        this.costOfRevenue = costOfRevenue;
    }

    public Long getGrossProfit() {
        return this.grossProfit;
    }

    public IncomeHistory grossProfit(Long grossProfit) {
        this.setGrossProfit(grossProfit);
        return this;
    }

    public void setGrossProfit(Long grossProfit) {
        this.grossProfit = grossProfit;
    }

    public Long getOperatingExpense() {
        return this.operatingExpense;
    }

    public IncomeHistory operatingExpense(Long operatingExpense) {
        this.setOperatingExpense(operatingExpense);
        return this;
    }

    public void setOperatingExpense(Long operatingExpense) {
        this.operatingExpense = operatingExpense;
    }

    public Long getOperatingIncome() {
        return this.operatingIncome;
    }

    public IncomeHistory operatingIncome(Long operatingIncome) {
        this.setOperatingIncome(operatingIncome);
        return this;
    }

    public void setOperatingIncome(Long operatingIncome) {
        this.operatingIncome = operatingIncome;
    }

    public Stock getStock() {
        return this.stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public IncomeHistory stock(Stock stock) {
        this.setStock(stock);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IncomeHistory)) {
            return false;
        }
        return id != null && id.equals(((IncomeHistory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IncomeHistory{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", totalRevenue=" + getTotalRevenue() +
            ", costOfRevenue=" + getCostOfRevenue() +
            ", grossProfit=" + getGrossProfit() +
            ", operatingExpense=" + getOperatingExpense() +
            ", operatingIncome=" + getOperatingIncome() +
            "}";
    }
}
