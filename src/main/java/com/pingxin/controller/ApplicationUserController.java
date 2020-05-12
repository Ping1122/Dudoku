package com.pingxin.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.pingxin.model.JsonWebToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pingxin.model.ApplicationUser;
import com.pingxin.service.ApplicationUserService;

@RestController
public class ApplicationUserController {

    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    private JsonWebToken jsonWebToken;

    @GetMapping("api/user/all")
    public List<ApplicationUser> getUsers() throws Exception {
        return applicationUserService.getAllUsers();
    }

    @GetMapping("/api/me")
    public ApplicationUser getUserByEmail(HttpServletRequest request) throws Exception {
        String token = request.getHeader("x-auth-token").substring(7);
        String email = jsonWebToken.getKeyFromToken(token);
        return applicationUserService.getUserByEmail(email);
    }
}
