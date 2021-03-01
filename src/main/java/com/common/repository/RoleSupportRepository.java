package com.common.repository;

import com.common.domain.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleSupportRepository {

    List<Role> findByUserId(Long userId);
    
}