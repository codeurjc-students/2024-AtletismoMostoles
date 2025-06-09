package com.example.service1.Models;

import java.util.List;

public class UserDTO {

    private Long id;
    private String name;
    private String encodedPassword;
    private List<String> roles;

    public UserDTO() {
    }

    public UserDTO(String name, String encodedPassword, String... roles) {
        this.name = name;
        this.encodedPassword = encodedPassword;
        this.roles = List.of(roles);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

