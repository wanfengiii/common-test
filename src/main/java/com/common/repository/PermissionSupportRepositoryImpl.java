package com.common.repository;

import com.common.domain.Permission;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class PermissionSupportRepositoryImpl extends AbstractJpaRepository<Permission, Long>
        implements PermissionSupportRepository {

    /**
     * 4 tables join together, use native sql here
     */
    private static final String USER_PERMISSION_SQL = 
        "select \n" +
        "   p.*\n" +
        "from \n" +
        "   user u,\n" +
        "   userrole ur,\n" +
        "   rolepermission rp,\n" +
        "   permission p\n" +
        "where \n" +
        "   u.username = ?\n" +
        "   and ur.userId = u.id\n" +
        "   and ur.roleId = rp.roleId\n" +
        "   and rp.permissionId = p.id\n" +
        "order by\n" +
        "   p.sort";

    public PermissionSupportRepositoryImpl(EntityManager em) {
        super(Permission.class, em);
    }

    @Override
    public List<Permission> findUserPermissions(String username) {
        return queryForList(USER_PERMISSION_SQL, username);
    }

}