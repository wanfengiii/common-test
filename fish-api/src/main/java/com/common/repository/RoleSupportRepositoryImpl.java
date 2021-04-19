package com.common.repository;

import com.common.domain.Role;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class RoleSupportRepositoryImpl extends AbstractJpaRepository<Role, Long>
        implements RoleSupportRepository {

    private static final String USER_ROLE_SQL = 
        "select \n" +
        "   r.*\n" +
        "from \n" +
        "   user u,\n" +
        "   userrole ur,\n" +
        "   role r\n" +
        "where \n" +
        "   u.id = ?\n" +
        "   and ur.userId = u.id\n" +
        "   and ur.roleId = r.id\n"
    ;

    public RoleSupportRepositoryImpl(EntityManager em) {
        super(Role.class, em);
    }

    @Override
    public List<Role> findByUserId(Long userId) {
        return queryForList(USER_ROLE_SQL, userId);
    }

}