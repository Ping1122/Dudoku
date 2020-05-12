package com.pingxin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pingxin.model.ApplicationUser;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Integer>  {

    List<ApplicationUser> findByEmail(String email);
}
