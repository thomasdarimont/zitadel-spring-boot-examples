package demo.config;

import demo.support.TokenAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
class WebClientConfig {

    private final TokenAccessor tokenAccessor;

    @Bean
    @Qualifier("zitadel")
    RestTemplate restTemplate() {
        return new RestTemplateBuilder().interceptors((request, body, execution) -> {

            var accessTokenForCurrentUser = tokenAccessor.getAccessTokenForCurrentUser();
            if (accessTokenForCurrentUser != null) {
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenForCurrentUser.getTokenValue());
            }

            return execution.execute(request, body);
        }).build();
    }
}
