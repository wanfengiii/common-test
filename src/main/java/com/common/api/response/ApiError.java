package com.common.api.response;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.springframework.http.HttpStatus.*;

public enum ApiError {

    RESOURCE_NOT_FOUND(NOT_FOUND, "common.resource_not_found", "请求资源{0}不存在"),
    
    RESOURCE_FOUND(FOUND, "common.resource_found", "请求资源{0}已存在"),

    AUTHENTICATION_FAILED(FORBIDDEN, "auth.authentication_failed", "认证失败"),

    AUTHENTICATION_INVALID_TOKEN(FORBIDDEN, "auth.authentication_invalid_token", "token非法或已失效"),

    BAD_CREDENTIALS(FORBIDDEN, "auth.bad_credentials", "用户名或密码错误"),

    VALIDATE_NOT_PASS(BAD_REQUEST, "validation.uri_not_valid", "{0}"),

    BAD_PASSWORD(BAD_REQUEST, "auth.not_valid_password", "密码不能为纯数字"),

    USERNAME_NOT_FOUND(FORBIDDEN, "auth.username_not_found", "非法的用户"),

    USERNAME_EXIST(FORBIDDEN, "auth.username_exist", "用户{0}已存在"),

    ACCOUNT_EXPIRED(FORBIDDEN, "auth.account_expired", "用户已退出"),

    ACCOUNT_LOCKED(FORBIDDEN, "auth.account_locked", "用户已锁定"),

    ACCESS_DENIED(FORBIDDEN, "auth.access_denied", "拒绝访问"),

    METHOD_ARGUMENT_NOT_VALID(BAD_REQUEST, "validation.method_argument_not_valid", "{0}"),

    METHOD_PARAM_REQUIRED(BAD_REQUEST, "validation.method_param_required", "参数 {0} 不能为空"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "server.internal_server_error", "服务器错误: {0}"),

    // ----- business related -----

    VIOLATION_ENT_NOT_EXIST(BAD_REQUEST, "violation.ent_not_exist", "企业\"{0}\"不存在"),
    
    VIOLATION_NO_VIOLATION(BAD_REQUEST, "violation.no_violation", "违规数据无违法项"),

    RESOURCE_ENT_NOT_FOUND(NOT_FOUND, "common.resource_not_found", "企业代码{0}不存在"),
    DELETE_FAILED(BAD_REQUEST, "common.delete_failed", "删除失败: {0}"),
    SAVE_FAILED(BAD_REQUEST, "common.save_failed", "保存失败: {0}");


    // ----- /business related -----
	
    private HttpStatus httpStatus;

    private String code;

    private String message;

    private ApiError(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public static ApiError getApiError(AuthenticationException ex) {
        if (ex instanceof BadCredentialsException) {
            return BAD_CREDENTIALS;
        } else if (ex instanceof UsernameNotFoundException) {
            return USERNAME_NOT_FOUND;
        } else if (ex instanceof AccountExpiredException) {
            return ACCOUNT_EXPIRED;
        } else if (ex instanceof LockedException) {
            return ACCOUNT_LOCKED;
        } else if (ex instanceof CredentialsExpiredException) {
            return AUTHENTICATION_INVALID_TOKEN;
        }
        return AUTHENTICATION_FAILED;        
    }

}