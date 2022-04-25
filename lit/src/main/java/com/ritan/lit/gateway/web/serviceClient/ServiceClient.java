package com.ritan.lit.gateway.web.serviceClient;

import com.ritan.lit.gateway.domain.User;
import com.ritan.lit.gateway.security.jwt.TokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public abstract class ServiceClient {
    private WebClient webClient;
    private User user;
    private final TokenProvider tokenProvider;

    public ServiceClient(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getLogin(),
            user.getPassword());

        this.webClient = WebClient.builder()
            .baseUrl("http://localhost:8085")
            .defaultHeader("Authorization", "Bearer " + this.tokenProvider.createToken(authentication,
                true))
            .build();
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public abstract void post();
}
