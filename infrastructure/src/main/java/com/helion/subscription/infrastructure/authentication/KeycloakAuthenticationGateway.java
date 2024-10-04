package com.helion.subscription.infrastructure.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helion.subscription.domain.exceptions.InternalErrorException;
import com.helion.subscription.infrastructure.configuration.annotations.Keycloak;
import com.helion.subscription.infrastructure.configuration.properties.KeycloakProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Component
public class KeycloakAuthenticationGateway implements AuthenticationGateway{

    private final String tokenUri;
    private final RestClient restClient;

    public KeycloakAuthenticationGateway(
            final KeycloakProperties keycloakProperties,
            @Keycloak final RestClient restClient) {
        this.tokenUri = Objects.requireNonNull(keycloakProperties.tokenUri());
        this.restClient = Objects.requireNonNull(restClient);
    }

    @Override
    public AuthenticationResult login(ClientCredentialsInput input) {
        final var map = new LinkedMultiValueMap<>();
        map.set("grant_type", "client_credentials");
        map.set("client_id", input.clientId());
        map.set("client_secret", input.clientSecret());

        final var output = this.restClient.post()
                .uri(tokenUri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(map)
                .retrieve()
                .body(KeycloakAuthenticationResult.class);

        if (output == null) {
            throw InternalErrorException.with("Failed to create client credentials [clientId: %s]".formatted(input.clientId()));
        }
        return new AuthenticationResult(output.accessToken, output.refreshToken);
    }

    @Override
    public AuthenticationResult refresh(RefreshTokenInput input) {

        final var map = new LinkedMultiValueMap<>();
        map.set("grant_type", "refresh_token");
        map.set("client_id", input.clientId());
        map.set("client_secret", input.clientSecret());
        map.set("refresh_token", input.refreshToken());

        final var output = this.restClient.post()
                .uri(tokenUri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(map)
                .retrieve()
                .body(KeycloakAuthenticationResult.class);

        if (output == null) {
            throw InternalErrorException.with("Failed to refresh client credentials [clientId: %s]".formatted(input.clientId()));
        }
        return new AuthenticationResult(output.accessToken, output.refreshToken);
    }

    public record KeycloakAuthenticationResult(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken
    ){}
}
