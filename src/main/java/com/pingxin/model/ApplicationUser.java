package com.pingxin.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "user")
@JsonIgnoreProperties({"exception", "hibernateLazyInitializer"})
public class ApplicationUser implements Jsonify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 1000, message = "Password must be at least 8 characters")
    String password;

    @NotBlank(message = "Email cannot be empty")
    @Size(max = 1000, message = "Email must be at most 1000 characters")
    @Email(message = "Invalid Email address")
    String email;

    @Column(name = "created_date")
    Date createdDate;

    int role;

    int status;

    public ApplicationUser() {
    }

    public ApplicationUser(String password, String email) {
        this.password = password;
        this.email = email;
    }

    public void convertToNewUser() {
        this.createdDate = new Date();
        this.role = 2;
        this.status = 0;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
