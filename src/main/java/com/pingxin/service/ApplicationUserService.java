package com.pingxin.service;

import java.util.List;

import com.pingxin.model.JsonWebToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pingxin.model.ApplicationUser;
import com.pingxin.repository.ApplicationUserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class ApplicationUserService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private JsonWebToken jsonWebToken;

    public List<ApplicationUser> getAllUsers() {
        return applicationUserRepository.findAll();
    }

    public ApplicationUser getUserByEmail(String email) {
        List<ApplicationUser> applicationUsers = applicationUserRepository.findByEmail(email);
        if (applicationUsers.size() == 0) {
            return null;
        }
        return applicationUsers.get(0);
    }

    public boolean existsUserWithEmail(String email) {
        List<ApplicationUser> applicationUsers = applicationUserRepository.findByEmail(email);
        return applicationUsers.size() != 0;
    }

    public void saveUser(ApplicationUser applicationUser) {
        applicationUserRepository.save(applicationUser);
    }

    public String getEmailFromRequest(HttpServletRequest request) {
        String token = request.getHeader("x-auth-token").substring(7);
        return jsonWebToken.getKeyFromToken(token).split(" ")[0];
    }

    public String getUsernameFromRequest(HttpServletRequest request) {
        String token = request.getHeader("x-auth-token").substring(7);
        return jsonWebToken.getKeyFromToken(token).split(" ")[1];
    }

    public String getCredsFromRequest(HttpServletRequest request) {
        String token = request.getHeader("x-auth-token").substring(7);
        return jsonWebToken.getKeyFromToken(token);
    }

}
