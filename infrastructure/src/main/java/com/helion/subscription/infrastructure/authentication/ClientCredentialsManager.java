package com.helion.subscription.infrastructure.authentication;

import com.helion.subscription.infrastructure.configuration.properties.KeycloakProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

@Component
public class ClientCredentialsManager implements GetClientCredentials, RefreshClientCredentials{

    private final static AtomicReferenceFieldUpdater<ClientCredentialsManager, ClientCredentials> UPDATER
            = AtomicReferenceFieldUpdater.newUpdater(ClientCredentialsManager.class, ClientCredentials.class, "credentials");

    private volatile ClientCredentials credentials;
    private final AuthenticationGateway authenticationGateway;
    private final KeycloakProperties keycloakProperties;

    public ClientCredentialsManager(AuthenticationGateway authenticationGateway,
                                    KeycloakProperties keycloakProperties) {
        this.authenticationGateway = Objects.requireNonNull(authenticationGateway);
        this.keycloakProperties = Objects.requireNonNull(keycloakProperties);
    }

    @Override
    public String retrieve() {
        return this.credentials.accessToken;
    }

    @Override
    public void refresh() {
        final var result = this.credentials == null
                ? login()
                : refreshToken();
        UPDATER.set(this, new ClientCredentials(clientId(), result.accessToken(), result.refreshToken()));
    }

    private AuthenticationGateway.AuthenticationResult refreshToken() {
        try{
            return this.authenticationGateway.refresh(new AuthenticationGateway.RefreshTokenInput(clientId(), this.clientSecret(), this.credentials.refreshToken()));
        } catch (RuntimeException ex) {
            return this.login();
        }

    }


    private AuthenticationGateway.AuthenticationResult login() {
        return this.authenticationGateway.login(new AuthenticationGateway.ClientCredentialsInput(clientId(), clientSecret()));
    }

    public String clientId(){
        return keycloakProperties.clientId();
    }

    public String clientSecret(){
        return keycloakProperties.clientSecret();
    }

    record ClientCredentials(String clientId, String accessToken, String refreshToken){}
}
