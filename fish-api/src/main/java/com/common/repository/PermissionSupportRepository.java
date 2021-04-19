package com.common.repository;

import com.common.domain.Permission;

import java.util.List;

public interface PermissionSupportRepository {


    List<Permission> findUserPermissions(String username);

}