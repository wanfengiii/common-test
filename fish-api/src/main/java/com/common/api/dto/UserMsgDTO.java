package com.common.api.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UserMsgDTO {

    @NotEmpty
	@Size(min = 5, max = 64)
    private String username;

    @Size(min = 6, max = 20)
    private String password;

    private String name;

    private String phone;

    private String email;

    private String address;
}