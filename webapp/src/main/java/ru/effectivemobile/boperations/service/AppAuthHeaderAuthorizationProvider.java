package ru.effectivemobile.boperations.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Авторизация запросов по Bearer токену
 */
@Component
@AllArgsConstructor
public class AppAuthHeaderAuthorizationProvider implements AuthenticationProvider {

    private final AppAuthTokenManager authTokenManager;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String tokenValue = authentication.getName();
        AppUserDetails appUserDetails = authTokenManager.validate(tokenValue);

        if (appUserDetails == null) {
            return authentication;
        }

        return new PreAuthenticatedAuthenticationToken(appUserDetails, null, appUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }

}