package com.common.repository;

import com.common.domain.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    long deleteByRoleId(Long roleId);
    
    @Modifying
	@Query(value = "delete rp from rolePermission rp left join permission p on p.id = rp.permissionId and p.type like concat(?2,'%') where rp.roleId = ?1", nativeQuery = true)
//    @Query(value = "delete from RolePermission rp where rp.roleId = ?1 and rp.permissionId in(select p.id from Permission p where p.type like concat(?2,'%')) ")
    Integer deleteByRoleIdAndType(Long id, String type);
    
    @Query(value = "select rp.* from rolePermission rp INNER JOIN permission p on p.id = rp.permissionId and p.type like concat(?2,'_function','%') where rp.roleId = ?1 ", nativeQuery = true)
    List<RolePermission> authorityOne(Long id, String type);
}