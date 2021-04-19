package com.common.api.dto;

import com.common.domain.Role;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UserDTO {

    private Long id;

    @NotEmpty
	@Size(min = 5, max = 64)
    private String username;

    @Size(min = 6, max = 20)
    private String password;

    private String name;

    private String phone;

    private String email;

    private String address;

    private String type;

    private List<Role> roles;

    private String roleId;

    private List<Long> roless;
}