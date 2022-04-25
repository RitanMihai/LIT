package com.ritan.lit.gateway.web.serviceClient;

import com.ritan.lit.gateway.domain.User;
import com.ritan.lit.gateway.domain.social.SocialUserRequest;
import com.ritan.lit.gateway.security.jwt.TokenProvider;
import com.ritan.lit.gateway.web.rest.vm.ManagedUserVM;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tech.jhipster.config.JHipsterProperties;

@Component
public class SocialClient extends ServiceClient {
    private final String baseURL = "/services/social/api/";

    public SocialClient(TokenProvider tokenProvider) {
        super(tokenProvider);
    }

    public void post() {
        SocialUserRequest socialUserRequest = new SocialUserRequest();
        socialUserRequest.setUser(super.getUser().getId());

        super.getWebClient().post()
            .uri("/services/social/api/social-users")
            .body(Mono.just(socialUserRequest), SocialUserRequest.class)
            .retrieve()
            .bodyToMono(SocialUserRequest.class)
            .toFuture();
    }
}
