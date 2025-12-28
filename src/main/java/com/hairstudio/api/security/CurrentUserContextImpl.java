package com.hairstudio.api.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserContextImpl implements CurrentUserContext {

    @Override
    public Short getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new AuthenticationCredentialsNotFoundException("Invalid authentication principal");
        }

        Short userId = userDetails.getId();
        if (userId == null) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated user has no ID");
        }

        return userId;
    }
}
