package com.ritan.lit.portfolio.web.rest.serviceClient;

import com.ritan.lit.portfolio.client.UserFeignClientInterceptor;
import com.ritan.lit.portfolio.security.SecurityUtils;
import com.ritan.lit.portfolio.security.jwt.TokenProvider;
import feign.RequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Component
public abstract class ServiceClient {
    private WebClient webClient;
    private final TokenProvider tokenProvider;

    public ServiceClient(TokenProvider tokenProvider) {

        Optional<String> currentUserJWT = SecurityUtils.getCurrentUserJWT();

        System.out.println("HEADER");
        System.out.println(currentUserJWT);

        this.tokenProvider = tokenProvider;
        this.webClient = WebClient.builder()
            .baseUrl("http://localhost:8085")
            .defaultHeader("Authorization", "Bearer " + currentUserJWT.get())
            .build();
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }
}
