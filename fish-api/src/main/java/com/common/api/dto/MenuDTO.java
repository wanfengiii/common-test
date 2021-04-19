package com.common.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

// 方便前端展示菜单, 根据前端需要的数据结构设计
@Data
public class MenuDTO {

    private Long id;
    
    private String code;

    private String name;

    private String path;

    private String component;

    @JsonIgnore
    private int sort = 0;

    private String icon;

    private List<MenuDTO> children;

}