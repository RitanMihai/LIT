package com.ritan.lit.portfolio.domain.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A PriceHistory.
 */
@Entity
@Table(name = "price_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "pricehistory")
public class PriceHistoryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "open")
    private Double open;

    @Column(name = "high")
    private Double high;

    @Column(name = "low")
    private Double low;

    @Column(name = "close")
    private Double close;

    @Column(name = "adj_close")
    private Double adjClose;

    @Column(name = "volume")
    private Double volume;

    @ManyToOne(fetch = FetchType.EAGER)
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

    public PriceHistoryRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public PriceHistoryRequest date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getOpen() {
        return this.open;
    }

    public PriceHistoryRequest open(Double open) {
        this.setOpen(open);
        return this;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return this.high;
    }

    public PriceHistoryRequest high(Double high) {
        this.setHigh(high);
        return this;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return this.low;
    }

    public PriceHistoryRequest low(Double low) {
        this.setLow(low);
        return this;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return this.close;
    }

    public PriceHistoryRequest close(Double close) {
        this.setClose(close);
        return this;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getAdjClose() {
        return this.adjClose;
    }

    public PriceHistoryRequest adjClose(Double adjClose) {
        this.setAdjClose(adjClose);
        return this;
    }

    public void setAdjClose(Double adjClose) {
        this.adjClose = adjClose;
    }

    public Double getVolume() {
        return this.volume;
    }

    public PriceHistoryRequest volume(Double volume) {
        this.setVolume(volume);
        return this;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Stock getStock() {
        return this.stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public PriceHistoryRequest stock(Stock stock) {
        this.setStock(stock);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PriceHistoryRequest)) {
            return false;
        }
        return id != null && id.equals(((PriceHistoryRequest) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PriceHistory{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", open=" + getOpen() +
            ", high=" + getHigh() +
            ", low=" + getLow() +
            ", close=" + getClose() +
            ", adjClose=" + getAdjClose() +
            ", volume=" + getVolume() +
            "}";
    }
}
