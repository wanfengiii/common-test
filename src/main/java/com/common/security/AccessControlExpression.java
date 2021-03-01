package com.common.security;

public final class AccessControlExpression {

    private static final String S = "hasPermission(null, '";
    private static final String E = "')";

    public static final String USER_LIST = S + "user_list" + E;
    public static final String USER_CREATE = S + "user_create" + E;
    public static final String USER_UPDATE = S + "user_update" + E;
    public static final String USER_DELETE = S + "user_delete" + E;
    public static final String USER_ADMIN_RESET_PASSWORD = S + "user_admin_reset_password" + E;
    public static final String USER_ROLE = S + "user_role" + E;

    public static final String ROLE_LIST = S + "role_list" + E;
    public static final String ROLE_CREATE = S + "role_create" + E;
    public static final String ROLE_UPDATE = S + "role_update" + E;
    public static final String ROLE_DELETE = S + "role_delete" + E;
    public static final String ROLE_AUTHORITY = S + "role_authority" + E;
    
    //文章发布接口
    public static final String ARTICLE_LIST = S + "article_list" + E;
    public static final String ARTICLE_CREATE = S + "article_create" + E;
    public static final String ARTICLE_UPDATE = S + "article_update" + E;
    public static final String ARTICLE_DELETE = S + "article_delete" + E;
    
    //盒子信息接口
    public static final String AIBOX_LIST = S + "aibox_list" + E;
    public static final String AIBOX_CREATE = S + "aibox_create" + E;
    public static final String AIBOX_UPDATE = S + "aibox_update" + E;
    public static final String AIBOX_DELETE = S + "aibox_delete" + E;
    
    //摄像头信息接口
    public static final String CAMERA_LIST = S + "camera_list" + E;
    public static final String CAMERA_CREATE = S + "camera_create" + E;
    public static final String CAMERA_UPDATE = S + "camera_update" + E;
    public static final String CAMERA_DELETE = S + "camera_delete" + E;

    // 所级监管人员只能查看本所违规监管
    public static final String P_VIOLATION_GOV_INSTITUTE_DATA = "violation_gov_institute_data";

    // 所级监管人员只能查看本所企业
    public static final String P_ENT_GOV_INSTITUTE_DATA = "ent_gov_institute_data";


    public static final String DELIVERY_ADD = S + "delivery_add" + E;
    public static final String DELIVERY_DELETE = S + "delivery_delete" + E;

    // 大屏
    public static final String SCREEN = S + "screen" + E;

}