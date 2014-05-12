package com.example;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Technical demonstation of securing method invocations with spring-security
 */
public class ContextRunnerMain {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context;
        context = new ClassPathXmlApplicationContext("applicationContext.xml");

        authenticate(context, "dev", "654321");

        // intercept-methods is local to a bean,
        // even if it uses class name in method pattern
        //MyService myService = context.getBean("myUnprotectedService", MyService.class);

        MyService myService = context.getBean("myService", MyService.class);

        callService(myService);
        logout();
        System.out.println("Logged out");
        callService(myService);
    }

    private static void callService(MyService myService) {
        try {
            myService.breathe();
            try {
                myService.useTheComputer();
            } catch (AccessDeniedException e) {
                System.out.println("Access to the computer denied!");
            }
            try {
                myService.formatDisk();
            } catch (AccessDeniedException e) {
                System.out.println("Access to disk formatting denied!");
            }
            try {
                myService.compile();
            } catch (AccessDeniedException e) {
                System.out.println("Access to compilation denied!");
            }
        } catch (AuthenticationCredentialsNotFoundException e) {
            System.out.println("You are not logged in. Cannot call any secured method!");
        }
    }

    public static void authenticate(AbstractApplicationContext context, String user, String password) {
        AuthenticationManager authenticationManager
                = context.getBean("myAuthManager", AuthenticationManager.class);

        try {
            Authentication request = new UsernamePasswordAuthenticationToken(user, password);
            Authentication result = authenticationManager.authenticate(request);
            SecurityContextHolder.getContext().setAuthentication(result);
        } catch (AuthenticationException e) {
            // eg. BadCredentialsException: Bad credentials
            throw new RuntimeException(e);
        }
        System.out.println("Successfully authenticated. Security context contains: " +
                SecurityContextHolder.getContext().getAuthentication());
    }

    private static void logout() {
        SecurityContextHolder.clearContext();
    }
}
