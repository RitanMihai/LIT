package com.ritan.lit.gateway.web.serviceClient;

import com.ritan.lit.gateway.domain.portfolio.PortfolioUserRequest;
import com.ritan.lit.gateway.domain.social.SocialUserRequest;
import com.ritan.lit.gateway.security.jwt.TokenProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PortfolioClient extends ServiceClient {
    private final String baseURL = "/services/portfolio/api/portfolio-users";

    public PortfolioClient(TokenProvider tokenProvider) {
        super(tokenProvider);
    }

    public void post() {
        PortfolioUserRequest portfolioUserRequest = new PortfolioUserRequest();
        portfolioUserRequest.setUser(super.getUser().getLogin());

        super.getWebClient().post()
            .uri("/services/portfolio/api/portfolio-users")
            .body(Mono.just(portfolioUserRequest), SocialUserRequest.class)
            .retrieve()
            .bodyToMono(SocialUserRequest.class)
            .toFuture();
    }
}
