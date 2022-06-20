package com.ritan.lit.portfolio.web.rest.serviceClient;

import com.ritan.lit.portfolio.domain.StockInfo;
import com.ritan.lit.portfolio.domain.services.DateDoublePair;
import com.ritan.lit.portfolio.domain.services.PriceHistoryRequest;
import com.ritan.lit.portfolio.security.jwt.TokenProvider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class WatcherClient extends ServiceClient {
    private PriceHistoryRequest priceHistory;
    private final String baseURL = "/services/portfolio/api/";

    public WatcherClient(TokenProvider tokenProvider) {super(tokenProvider);}

    public PriceHistoryRequest getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(PriceHistoryRequest priceHistory) {
        this.priceHistory = priceHistory;
    }

    public DateDoublePair[] getHistory(String stock, Instant filledDate) throws ExecutionException, InterruptedException {
        PriceHistoryRequest socialUserRequest = new PriceHistoryRequest();

        ZoneId zone = ZoneId.of("America/Edmonton");
        LocalDate date = LocalDateTime.ofInstant(filledDate, zone).toLocalDate();

        DateDoublePair[] obj = Objects.requireNonNull(super.getWebClient().get()
            .uri("/services/watcher/api/price-histories/symbol/"+stock+"?closePrice=true&start_date="+date.toString())
            .retrieve().toEntity(DateDoublePair[].class).block()).getBody();

        return obj;
    }
}
