package com.pingxin.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.pingxin.service.ApplicationUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingxin.model.ApplicationUser;
import com.pingxin.model.ErrorResponse;
import com.pingxin.model.JWTResponse;
import com.pingxin.model.JsonWebToken;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JsonWebToken jsonWebToken;
    private ApplicationUser applicationUser;
    private ApplicationUserService applicationUserService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JsonWebToken jsonWebToken, ApplicationUserService applicationUserService) {
        this.authenticationManager = authenticationManager;
        this.jsonWebToken = jsonWebToken;
        this.applicationUserService = applicationUserService;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws AuthenticationException {
        System.out.println("JWTAuthenticationFilter attemptAuthentication called");
        try {
            ApplicationUser creds = new ObjectMapper()
                    .readValue(req.getInputStream(), ApplicationUser.class);
            this.applicationUser = applicationUserService.getUserByEmail(creds.getEmail());
            System.out.println(applicationUser.toJson());
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            System.out.println("IOException");
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        System.out.println("JWTAuthenticationFilter successfulAuthentication called");
        String key = this.applicationUser.getEmail() + " " + this.applicationUser.getUsername();
        String token = jsonWebToken.generateToken(key);
        res.addHeader("x-auth-token", "Bearer " + token);
        res.addHeader("content-type", "application/json");
        res.getWriter().write(new JWTResponse("Bearer " + token).toJson());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        System.out.println("JWTAuthenticationFilter unsuccessfulAuthentication called");
        SecurityContextHolder.clearContext();
        response.setStatus(403);
        response.addHeader("content-type", "application/json");
        response.getWriter().write(new ErrorResponse(new Date(),
                403,
                "Forbidden",
                "Incorrect Email or Password",
                "/login").toJson());
    }
}
