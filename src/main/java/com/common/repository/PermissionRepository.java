package com.common.repository;

import com.common.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, PermissionSupportRepository {

    List<Permission> findByType(String type);
    
    List<Permission> findByTypeLikeOrderBySort(String type);


}