package com.example;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Checks if the given authority is granted, regardless of advised object and method.
 */
public class RequireAuthority implements MethodBeforeAdvice {

    private String requestedAuthority;

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        if (method.getName().equals("breathe")) return; // example!

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("not logged in");
        }

        Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            if (grantedAuthority.getAuthority().equals(requestedAuthority)) return;
        }
        throw new AccessDeniedException(requestedAuthority + " required");
    }

    @Required
    public void setRequestedAuthority(String requestedAuthority) {
        this.requestedAuthority = requestedAuthority;
    }
}
