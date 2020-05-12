package com.pingxin.controller;

import javax.validation.Valid;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pingxin.service.ApplicationUserService;
import com.pingxin.model.ApplicationUser;
import com.pingxin.model.ErrorResponse;

@RestController
public class AuthController {

    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/api/register")
    public Object register(@Valid @RequestBody ApplicationUser user) throws Exception{
        String email = user.getEmail();
        if (applicationUserService.existsUserWithEmail(email)) {
            return new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Email address already used",
                    "/api/register"
            );
        }
        user.convertToNewUser();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        applicationUserService.saveUser(user);
        return user;
    }
}
