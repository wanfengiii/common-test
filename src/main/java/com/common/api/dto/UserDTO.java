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

    private String firstname;

    private String lastname;    
    
    private String phone;

    private String email;
    
    private String avatar;

    private String type;

    private String district;

    private String institute;
    
    private String typeName;

    private String districtName;

    private String ensureGroup;

    private String userImage;
    
    private List<Role> roles;
    
    private String roleId;
    
    private List<Long> ids;
    
    private List<Long> roless;

    private String realName;

}