package com.pingxin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pingxin.model.ApplicationUser;
import com.pingxin.repository.ApplicationUserRepository;

@Service
public class ApplicationUserService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;


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
}
