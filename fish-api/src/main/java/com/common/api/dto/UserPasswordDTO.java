package com.common.api.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UserPasswordDTO {

    private Long userId;

    private String password;

    @Size(min = 6, max = 20)
    private String newPassword;
    
    private String username;

}