package com.common.repository;

import com.common.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, RoleSupportRepository {

    @Query("select r from Role r where r.code like %?1% or r.name like %?1%")
    Page<Role> findRoles(String q, Pageable pageable);
    
    @Query("select r from Role r where r.code like %?1% or r.name like %?1%")
    List<Role> findRoles(String q);
    
    boolean existsByCode(String code);
    
    boolean existsByName(String name);
    
    List<Role> findByIdIn(Long[] id);
    
    List<Role> findRolesByCodeLike(String usertype);
}