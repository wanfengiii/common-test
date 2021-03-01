package com.common.api.dto;

import lombok.Data;

@Data
public class RoleDTO {

    private Long id;

    private String code;

    private String name;
    
    private String desc;
    
    private String type;
    
    private Long[] menuIds;
}