package com.common.repository;

import com.common.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserSupportRepository {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query(value = "select u.* from user u, userrole ur, rolepermission rp, permission p where u.id = ur.userId and ur.roleId = rp.roleId and rp.permissionId = p.id and p.code = ?1",
            nativeQuery = true)
    List<User> findUserByPermission(String permissionCode);

}