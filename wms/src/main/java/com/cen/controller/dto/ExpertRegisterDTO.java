package com.cen.controller.dto;

import lombok.Data;

@Data
public class ExpertRegisterDTO {
    private Long id;
    private Long userId;
    
    // User account information
    private String username;
    private String password;
    
    // Expert information
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String qualification;
    private String expertise;
    private String education;
    private String workExperience;
    private String achievements;
    private String status;
} 