package com.pingxin.service;

import java.util.List;
import static java.util.Collections.emptyList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pingxin.model.ApplicationUser;
import com.pingxin.repository.ApplicationUserRepository;


@Service
public class ApplicationUserDetailService implements UserDetailsService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        List<ApplicationUser> applicationUser = applicationUserRepository.findByEmail(email);
        if (applicationUser.size() == 0) {
            throw new UsernameNotFoundException(email);
        }
        return new User(applicationUser.get(0).getUsername(), applicationUser.get(0).getPassword(), emptyList());
    }
}
