package com.common.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Permission {

    public static final String TYPE_WEB_MENU = "web_menu";

    public static final String TYPE_FUNCTION = "web_function";

    public static final String TYPE_APP_MENU = "app_menu";
    
    public static final String TYPE_APP_FUNCTION = "app_function";

    public static final String ALL_PERMISSION = "all_permission";

    public static final String VIOLATION_PUSH_MSG_ANDROID = "violation_push_msg_android";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    
    private String name;

    private Long parentId;

    private String path;

    private String component;

    private String icon;

    private int sort;

    private String type;
    
    /** 子菜单 */
    @Transient
    private List<Permission> children = new ArrayList<Permission>();

}