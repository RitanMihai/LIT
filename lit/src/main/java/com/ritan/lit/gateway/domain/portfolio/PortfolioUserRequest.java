package com.ritan.lit.gateway.domain.portfolio;

import java.util.Set;

public class PortfolioUserRequest {
    String user;
    Set<PortfolioRequest> portfolios;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
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
