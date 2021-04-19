package com.common.api.dto;

import lombok.Data;

import java.util.List;

// for user query result
@Data
public class UserInfoDTO {

    private UserDTO user;
    
    private List<MenuDTO> webMenus;

    private List<MenuDTO> appMenus;

    private List<String> functions;
    
    private List<String> appFunctions;
    
}