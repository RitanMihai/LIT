package com.ritan.lit.watchlist.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ritan.lit.watchlist.domain.enumeration.StockType;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Stock.
 */
@Entity
@Table(name = "stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "stock")
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    @Column(name = "market_cap")
    private String marketCap;

    @Column(name = "volume")
    private Integer volume;

    @Column(name = "pe_ration")
    private Double peRation;

    @Column(name = "ipo_date")
    private LocalDate ipoDate;

    @Column(name = "isin")
    private String isin;

    @Column(name = "is_delisted")
    private Boolean isDelisted;

    @Column(name = "has_dividend")
    private Boolean hasDividend;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private StockType type;

    @Column(name = "dividend_yield")
    private Double dividendYield;

    @ManyToOne
    @JsonIgnoreProperties(value = { "stocks" }, allowSetters = true)
    private StockExchange stockExchange;

    @ManyToOne
    @JsonIgnoreProperties(value = { "stocks" }, allowSetters = true)
    private Company company;

    @ManyToOne
    @JsonIgnoreProperties(value = { "stocks" }, allowSetters = true)
    private Currency currency;

    @OneToMany(mappedBy = "stock")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "stock" }, allowSetters = true)
    private Set<PriceHistory> priceHistories = new HashSet<>();

    @OneToMany(mappedBy = "stock")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "stock" }, allowSetters = true)
    private Set<DividendHistory> dividendHistories = new HashSet<>();

    @OneToMany(mappedBy = "stock")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "stock" }, allowSetters = true)
    private Set<StockSplitHistory> stockSplitHistories = new HashSet<>();

    @OneToMany(mappedBy = "stock")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "stock" }, allowSetters = true)
    private Set<CapitalGainHistory> capitalGainHistories = new HashSet<>();

    @OneToMany(mappedBy = "stock")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "stock" }, allowSetters = true)
    private Set<IncomeHistory> incomeHistories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Stock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicker() {
        return this.ticker;
    }

    public Stock ticker(String ticker) {
        this.setTicker(ticker);
        return this;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return this.name;
    }

    public Stock name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Stock image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Stock imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public String getMarketCap() {
        return this.marketCap;
    }

    public Stock marketCap(String marketCap) {
        this.setMarketCap(marketCap);
        return this;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public Integer getVolume() {
        return this.volume;
    }

    public Stock volume(Integer volume) {
        this.setVolume(volume);
        return this;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Double getPeRation() {
        return this.peRation;
    }

    public Stock peRation(Double peRation) {
        this.setPeRation(peRation);
        return this;
    }

    public void setPeRation(Double peRation) {
        this.peRation = peRation;
    }

    public LocalDate getIpoDate() {
        return this.ipoDate;
    }

    public Stock ipoDate(LocalDate ipoDate) {
        this.setIpoDate(ipoDate);
        return this;
    }

    public void setIpoDate(LocalDate ipoDate) {
        this.ipoDate = ipoDate;
    }

    public String getIsin() {
        return this.isin;
    }

    public Stock isin(String isin) {
        this.setIsin(isin);
        return this;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public Boolean getIsDelisted() {
        return this.isDelisted;
    }

    public Stock isDelisted(Boolean isDelisted) {
        this.setIsDelisted(isDelisted);
        return this;
    }

    public void setIsDelisted(Boolean isDelisted) {
        this.isDelisted = isDelisted;
    }

    public Boolean getHasDividend() {
        return this.hasDividend;
    }

    public Stock hasDividend(Boolean hasDividend) {
        this.setHasDividend(hasDividend);
        return this;
    }

    public void setHasDividend(Boolean hasDividend) {
        this.hasDividend = hasDividend;
    }

    public StockType getType() {
        return this.type;
    }

    public Stock type(StockType type) {
        this.setType(type);
        return this;
    }

    public void setType(StockType type) {
        this.type = type;
    }

    public Double getDividendYield() {
        return this.dividendYield;
    }

    public Stock dividendYield(Double dividendYield) {
        this.setDividendYield(dividendYield);
        return this;
    }

    public void setDividendYield(Double dividendYield) {
        this.dividendYield = dividendYield;
    }

    public StockExchange getStockExchange() {
        return this.stockExchange;
    }

    public void setStockExchange(StockExchange stockExchange) {
        this.stockExchange = stockExchange;
    }

    public Stock stockExchange(StockExchange stockExchange) {
        this.setStockExchange(stockExchange);
        return this;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Stock company(Company company) {
        this.setCompany(company);
        return this;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Stock currency(Currency currency) {
        this.setCurrency(currency);
        return this;
    }

    public Set<PriceHistory> getPriceHistories() {
        return this.priceHistories;
    }

    public void setPriceHistories(Set<PriceHistory> priceHistories) {
        if (this.priceHistories != null) {
            this.priceHistories.forEach(i -> i.setStock(null));
        }
        if (priceHistories != null) {
            priceHistories.forEach(i -> i.setStock(this));
        }
        this.priceHistories = priceHistories;
    }

    public Stock priceHistories(Set<PriceHistory> priceHistories) {
        this.setPriceHistories(priceHistories);
        return this;
    }

    public Stock addPriceHistory(PriceHistory priceHistory) {
        this.priceHistories.add(priceHistory);
        priceHistory.setStock(this);
        return this;
    }

    public Stock removePriceHistory(PriceHistory priceHistory) {
        this.priceHistories.remove(priceHistory);
        priceHistory.setStock(null);
        return this;
    }

    public Set<DividendHistory> getDividendHistories() {
        return this.dividendHistories;
    }

    public void setDividendHistories(Set<DividendHistory> dividendHistories) {
        if (this.dividendHistories != null) {
            this.dividendHistories.forEach(i -> i.setStock(null));
        }
        if (dividendHistories != null) {
            dividendHistories.forEach(i -> i.setStock(this));
        }
        this.dividendHistories = dividendHistories;
    }

    public Stock dividendHistories(Set<DividendHistory> dividendHistories) {
        this.setDividendHistories(dividendHistories);
        return this;
    }

    public Stock addDividendHistory(DividendHistory dividendHistory) {
        this.dividendHistories.add(dividendHistory);
        dividendHistory.setStock(this);
        return this;
    }

    public Stock removeDividendHistory(DividendHistory dividendHistory) {
        this.dividendHistories.remove(dividendHistory);
        dividendHistory.setStock(null);
        return this;
    }

    public Set<StockSplitHistory> getStockSplitHistories() {
        return this.stockSplitHistories;
    }

    public void setStockSplitHistories(Set<StockSplitHistory> stockSplitHistories) {
        if (this.stockSplitHistories != null) {
            this.stockSplitHistories.forEach(i -> i.setStock(null));
        }
        if (stockSplitHistories != null) {
            stockSplitHistories.forEach(i -> i.setStock(this));
        }
        this.stockSplitHistories = stockSplitHistories;
    }

    public Stock stockSplitHistories(Set<StockSplitHistory> stockSplitHistories) {
        this.setStockSplitHistories(stockSplitHistories);
        return this;
    }

    public Stock addStockSplitHistory(StockSplitHistory stockSplitHistory) {
        this.stockSplitHistories.add(stockSplitHistory);
        stockSplitHistory.setStock(this);
        return this;
    }

    public Stock removeStockSplitHistory(StockSplitHistory stockSplitHistory) {
        this.stockSplitHistories.remove(stockSplitHistory);
        stockSplitHistory.setStock(null);
        return this;
    }

    public Set<CapitalGainHistory> getCapitalGainHistories() {
        return this.capitalGainHistories;
    }

    public void setCapitalGainHistories(Set<CapitalGainHistory> capitalGainHistories) {
        if (this.capitalGainHistories != null) {
            this.capitalGainHistories.forEach(i -> i.setStock(null));
        }
        if (capitalGainHistories != null) {
            capitalGainHistories.forEach(i -> i.setStock(this));
        }
        this.capitalGainHistories = capitalGainHistories;
    }

    public Stock capitalGainHistories(Set<CapitalGainHistory> capitalGainHistories) {
        this.setCapitalGainHistories(capitalGainHistories);
        return this;
    }

    public Stock addCapitalGainHistory(CapitalGainHistory capitalGainHistory) {
        this.capitalGainHistories.add(capitalGainHistory);
        capitalGainHistory.setStock(this);
        return this;
    }

    public Stock removeCapitalGainHistory(CapitalGainHistory capitalGainHistory) {
        this.capitalGainHistories.remove(capitalGainHistory);
        capitalGainHistory.setStock(null);
        return this;
    }

    public Set<IncomeHistory> getIncomeHistories() {
        return this.incomeHistories;
    }

    public void setIncomeHistories(Set<IncomeHistory> incomeHistories) {
        if (this.incomeHistories != null) {
            this.incomeHistories.forEach(i -> i.setStock(null));
        }
        if (incomeHistories != null) {
            incomeHistories.forEach(i -> i.setStock(this));
        }
        this.incomeHistories = incomeHistories;
    }

    public Stock incomeHistories(Set<IncomeHistory> incomeHistories) {
        this.setIncomeHistories(incomeHistories);
        return this;
    }

    public Stock addIncomeHistory(IncomeHistory incomeHistory) {
        this.incomeHistories.add(incomeHistory);
        incomeHistory.setStock(this);
        return this;
    }

    public Stock removeIncomeHistory(IncomeHistory incomeHistory) {
        this.incomeHistories.remove(incomeHistory);
        incomeHistory.setStock(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stock)) {
            return false;
        }
        return id != null && id.equals(((Stock) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Stock{" +
            "id=" + getId() +
            ", ticker='" + getTicker() + "'" +
            ", name='" + getName() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", marketCap='" + getMarketCap() + "'" +
            ", volume=" + getVolume() +
            ", peRation=" + getPeRation() +
            ", ipoDate='" + getIpoDate() + "'" +
            ", isin='" + getIsin() + "'" +
            ", isDelisted='" + getIsDelisted() + "'" +
            ", hasDividend='" + getHasDividend() + "'" +
            ", type='" + getType() + "'" +
            ", dividendYield=" + getDividendYield() +
            "}";
    }
}
