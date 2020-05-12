package com.pingxin.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.pingxin.model.JsonWebToken;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter{

    private JsonWebToken jsonWebToken;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JsonWebToken jsonWebToken) {
        super(authenticationManager);
        this.jsonWebToken = jsonWebToken;
    }

    @Override
    public void doFilterInternal (HttpServletRequest req,
                                  HttpServletResponse res,
                                  FilterChain chain) throws IOException, ServletException {
        System.out.println("JWTAuthorizationFilter called");
        String header = req.getHeader("x-auth-token");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String token = req.getHeader("x-auth-token");
        if (token == null) return null;
        token = token.substring(7);
        String user = null;
        try {
            user = jsonWebToken.getKeyFromToken(token);
        } catch (Exception e) {
            return null;
        }
        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    }

}
