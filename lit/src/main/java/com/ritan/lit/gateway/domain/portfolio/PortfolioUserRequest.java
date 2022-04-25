package com.ritan.lit.gateway.domain.portfolio;

import java.util.Set;

public class PortfolioUserRequest {
    Long user;
    Set<PortfolioRequest> portfolios;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "PortfolioUserRequest{" +
            "user=" + user +
            ", portfolios=" + portfolios +
            '}';
    }
}
