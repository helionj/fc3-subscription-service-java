package com.helion.subscription.infrastructure.authentication;

public interface AuthenticationGateway {

    AuthenticationResult login(ClientCredentialsInput input);
    AuthenticationResult refresh(RefreshTokenInput input);
    record AuthenticationResult(String accessToken, String refreshToken){}
    record ClientCredentialsInput(String clientId, String clientSecret){}
    record RefreshTokenInput(String clientId, String clientSecret, String refreshToken){}
}
