package com.pingxin.controller;

import javax.validation.Valid;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> register(@Valid @RequestBody ApplicationUser user) throws Exception {
        System.out.println(user.toJson());
        String email = user.getEmail();
        if (applicationUserService.existsUserWithEmail(email)) {
            return new ResponseEntity<>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Email address already used",
                    "/api/register"
            ), HttpStatus.BAD_REQUEST);
        }
        user.convertToNewUser();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        applicationUserService.saveUser(user);
        return new ResponseEntity<>(user.toJson(), HttpStatus.OK);
    }
}
