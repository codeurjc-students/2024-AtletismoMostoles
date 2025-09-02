package com.example.service2.dto;

public class AthleteDto {
    private String licenseNumber;
    private String firstName;
    private String lastName;

    public String getFullName(){
        return this.firstName + " " + this.lastName;
    }
}

