package com.ritan.lit.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ritan.lit.portfolio.domain.enumeration.OrderType;
import com.ritan.lit.portfolio.domain.enumeration.PositionType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Order.
 */
@Entity
@Table(name = "jhi_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @NotNull
    @Column(name = "share_price", nullable = false)
    private Double sharePrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OrderType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private PositionType position;

    @Column(name = "subbmited_date")
    private Instant subbmitedDate;

    @Column(name = "filled_date")
    private Instant filledDate;

    @Column(name = "notes")
    private String notes;

    @Column(name = "total")
    private Double total;

    @Column(name = "taxes")
    private Double taxes;

    @Column(name = "stop_loss")
    private Double stopLoss;

    @Column(name = "take_profit")
    private Double takeProfit;

    @Column(name = "leverage")
    private Integer leverage;

    @Column(name = "exchange_rate")
    private Double exchangeRate;

    @Column(name = "is_cfd")
    private Boolean isCFD;

    @ManyToOne
    @JsonIgnoreProperties(value = { "orders" }, allowSetters = true)
    private StockInfo stockInfo;

    @ManyToOne
    @JsonIgnoreProperties(value = { "portfolioUser", "portfolioCurrency", "orders", "transactions" }, allowSetters = true)
    private Portfolio portfolio;

    @OneToMany(mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private Set<Dividend> dividends = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Order id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantity() {
        return this.quantity;
    }

    public Order quantity(Double quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getSharePrice() {
        return this.sharePrice;
    }

    public Order sharePrice(Double sharePrice) {
        this.setSharePrice(sharePrice);
        return this;
    }

    public void setSharePrice(Double sharePrice) {
        this.sharePrice = sharePrice;
    }

    public OrderType getType() {
        return this.type;
    }

    public Order type(OrderType type) {
        this.setType(type);
        return this;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public PositionType getPosition() {
        return this.position;
    }

    public Order position(PositionType position) {
        this.setPosition(position);
        return this;
    }

    public void setPosition(PositionType position) {
        this.position = position;
    }

    public Instant getSubbmitedDate() {
        return this.subbmitedDate;
    }

    public Order subbmitedDate(Instant subbmitedDate) {
        this.setSubbmitedDate(subbmitedDate);
        return this;
    }

    public void setSubbmitedDate(Instant subbmitedDate) {
        this.subbmitedDate = subbmitedDate;
    }

    public Instant getFilledDate() {
        return this.filledDate;
    }

    public Order filledDate(Instant filledDate) {
        this.setFilledDate(filledDate);
        return this;
    }

    public void setFilledDate(Instant filledDate) {
        this.filledDate = filledDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public Order notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getTotal() {
        return this.total;
    }

    public Order total(Double total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getTaxes() {
        return this.taxes;
    }

    public Order taxes(Double taxes) {
        this.setTaxes(taxes);
        return this;
    }

    public void setTaxes(Double taxes) {
        this.taxes = taxes;
    }

    public Double getStopLoss() {
        return this.stopLoss;
    }

    public Order stopLoss(Double stopLoss) {
        this.setStopLoss(stopLoss);
        return this;
    }

    public void setStopLoss(Double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public Double getTakeProfit() {
        return this.takeProfit;
    }

    public Order takeProfit(Double takeProfit) {
        this.setTakeProfit(takeProfit);
        return this;
    }

    public void setTakeProfit(Double takeProfit) {
        this.takeProfit = takeProfit;
    }

    public Integer getLeverage() {
        return this.leverage;
    }

    public Order leverage(Integer leverage) {
        this.setLeverage(leverage);
        return this;
    }

    public void setLeverage(Integer leverage) {
        this.leverage = leverage;
    }

    public Double getExchangeRate() {
        return this.exchangeRate;
    }

    public Order exchangeRate(Double exchangeRate) {
        this.setExchangeRate(exchangeRate);
        return this;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Boolean getIsCFD() {
        return this.isCFD;
    }

    public Order isCFD(Boolean isCFD) {
        this.setIsCFD(isCFD);
        return this;
    }

    public void setIsCFD(Boolean isCFD) {
        this.isCFD = isCFD;
    }

    public StockInfo getStockInfo() {
        return this.stockInfo;
    }

    public void setStockInfo(StockInfo stockInfo) {
        this.stockInfo = stockInfo;
    }

    public Order stockInfo(StockInfo stockInfo) {
        this.setStockInfo(stockInfo);
        return this;
    }

    public Portfolio getPortfolio() {
        return this.portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Order portfolio(Portfolio portfolio) {
        this.setPortfolio(portfolio);
        return this;
    }

    public Set<Dividend> getDividends() {
        return this.dividends;
    }

    public void setDividends(Set<Dividend> dividends) {
        if (this.dividends != null) {
            this.dividends.forEach(i -> i.setOrder(null));
        }
        if (dividends != null) {
            dividends.forEach(i -> i.setOrder(this));
        }
        this.dividends = dividends;
    }

    public Order dividends(Set<Dividend> dividends) {
        this.setDividends(dividends);
        return this;
    }

    public Order addDividend(Dividend dividend) {
        this.dividends.add(dividend);
        dividend.setOrder(this);
        return this;
    }

    public Order removeDividend(Dividend dividend) {
        this.dividends.remove(dividend);
        dividend.setOrder(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", sharePrice=" + getSharePrice() +
            ", type='" + getType() + "'" +
            ", position='" + getPosition() + "'" +
            ", subbmitedDate='" + getSubbmitedDate() + "'" +
            ", filledDate='" + getFilledDate() + "'" +
            ", notes='" + getNotes() + "'" +
            ", total=" + getTotal() +
            ", taxes=" + getTaxes() +
            ", stopLoss=" + getStopLoss() +
            ", takeProfit=" + getTakeProfit() +
            ", leverage=" + getLeverage() +
            ", exchangeRate=" + getExchangeRate() +
            ", isCFD='" + getIsCFD() + "'" +
            "}";
    }
}
