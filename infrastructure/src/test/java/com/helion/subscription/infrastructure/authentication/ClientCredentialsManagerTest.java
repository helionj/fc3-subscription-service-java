package com.helion.subscription.infrastructure.authentication;

import com.helion.subscription.domain.exceptions.InternalErrorException;
import com.helion.subscription.infrastructure.configuration.properties.KeycloakProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ClientCredentialsManagerTest {

    @Mock
    private KeycloakProperties keycloakProperties;

    @Mock
    private AuthenticationGateway authenticationGateway;

    @InjectMocks
    private ClientCredentialsManager manager;

    @Test
    public void givenValidAuthenticationResult_whenCallsRefresh_shouldCreateCredentials(){

        final var expectedAccessToken = "access";
        final var expectedRefreshToken = "refresh";
        final var expectedClientId = "client-123";
        final var expectedClientSecret ="secret-123";

        doReturn(expectedClientId).when(keycloakProperties).clientId();
        doReturn(expectedClientSecret).when(keycloakProperties).clientSecret();

        Mockito.doReturn(new AuthenticationGateway.AuthenticationResult(expectedAccessToken, expectedRefreshToken))
                .when(authenticationGateway).login(new AuthenticationGateway.ClientCredentialsInput(expectedClientId, expectedClientSecret));

        this.manager.refresh();

        final var actualToken = this.manager.retrieve();

        Assertions.assertEquals(expectedAccessToken,actualToken);
    }

    @Test
    public void givenPreviusAuthentication_whenCallsRefresh_shouldUpdateCredentials(){

        final var expectedAccessToken = "access";
        final var expectedRefreshToken = "refresh";
        final var expectedClientId = "client-123";
        final var expectedClientSecret ="secret-123";

        ReflectionTestUtils.setField(this.manager, "credentials", new ClientCredentialsManager.ClientCredentials(expectedClientId, "acc", "ref"));

        doReturn(expectedClientId).when(keycloakProperties).clientId();
        doReturn(expectedClientSecret).when(keycloakProperties).clientSecret();

        Mockito.doReturn(new AuthenticationGateway.AuthenticationResult(expectedAccessToken, expectedRefreshToken))
                .when(authenticationGateway).refresh(new AuthenticationGateway.RefreshTokenInput(expectedClientId, expectedClientSecret, "ref"));

        this.manager.refresh();

        final var actualCredentials = (ClientCredentialsManager.ClientCredentials)ReflectionTestUtils.getField(this.manager, "credentials");
        Assertions.assertEquals(expectedAccessToken,actualCredentials.accessToken());
        Assertions.assertEquals(expectedRefreshToken,actualCredentials.refreshToken());
    }

    @Test
    public void givenErrorFromRefreshToken_whenCallsRefresh_shouldFallbackToLogin(){

        final var expectedAccessToken = "access";
        final var expectedRefreshToken = "refresh";
        final var expectedClientId = "client-123";
        final var expectedClientSecret ="secret-123";

        ReflectionTestUtils.setField(this.manager, "credentials", new ClientCredentialsManager.ClientCredentials(expectedClientId, "acc", "ref"));

        doReturn(expectedClientId).when(keycloakProperties).clientId();
        doReturn(expectedClientSecret).when(keycloakProperties).clientSecret();

        doThrow(InternalErrorException.with("BLA!"))
                .when(authenticationGateway).refresh(new AuthenticationGateway.RefreshTokenInput(expectedClientId, expectedClientSecret, "ref"));

        Mockito.doReturn(new AuthenticationGateway.AuthenticationResult(expectedAccessToken, expectedRefreshToken))
                .when(authenticationGateway).login(new AuthenticationGateway.ClientCredentialsInput(expectedClientId, expectedClientSecret));

        this.manager.refresh();

        final var actualCredentials = (ClientCredentialsManager.ClientCredentials)ReflectionTestUtils.getField(this.manager, "credentials");
        Assertions.assertEquals(expectedAccessToken,actualCredentials.accessToken());
        Assertions.assertEquals(expectedRefreshToken,actualCredentials.refreshToken());
    }
}