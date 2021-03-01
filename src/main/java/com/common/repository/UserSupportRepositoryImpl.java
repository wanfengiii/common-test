package com.common.repository;

import com.common.api.dto.UserDTO;
import com.common.api.qo.UserQO;
import com.common.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class UserSupportRepositoryImpl extends AbstractJpaRepository<User, Long> implements UserSupportRepository {

    public UserSupportRepositoryImpl(EntityManager em) {
        super(User.class, em);
    }

    @Override
    public Page<UserDTO> findUsers(UserQO q, Pageable pageable) {
        String fromSql = findUsersFromSql(q);
        String countSql = "select count(distinct u.id) " + fromSql;
        String groupBySql = " GROUP BY u.id order by u.lastModifiedDate desc";
        
        String querySql = "select u.*,GROUP_CONCAT(r.`id`) roleId " + fromSql + groupBySql;
        
        if (q.getRoleId() != null) {
        	String orderBySql = " order by m.lastModifiedDate desc";
        	countSql = "select count(distinct m.id) from ( "
        			+ "select u.*,GROUP_CONCAT(r.`id`) roleId " + fromSql + " GROUP BY u.id ) m where  m.roleId like '%"+q.getRoleId()+"%'";
        	querySql = "select m.* from ( select u.*,GROUP_CONCAT(r.`id`) roleId " + fromSql 
        			+ " GROUP BY u.id ) m where  m.roleId like '%"+q.getRoleId()+"%'"+orderBySql;
        }
        return getPageResult(countSql, querySql, q, pageable, UserDTO.class);
    }

    private String findUsersFromSql(UserQO q) {
        StringBuilder sb = new StringBuilder("from user u left join userRole ur on u.id = ur.userId LEFT JOIN role r on ur.roleId = r.id where 1 = 1");
        if (StringUtils.isNotBlank(q.getQ())) {
            sb.append(" and (u.username like :q or u.firstname like :q or u.lastname like :q)");
            toLikeValue(q, "q");
        }
//        if (q.getRoleId() != null) {
//            sb.append(" and ur.roleId = :roleId");
//        }
        if (StringUtils.isNotBlank(q.getType())) {
            sb.append(" and u.type = :type");
        }
        if (StringUtils.isNotBlank(q.getDistrict())) {
            sb.append(" and u.district = :district");
        }
        if (StringUtils.isNotBlank(q.getInstitute())) {
            sb.append(" and u.institute = :institute");
        }
//        sb.append(" GROUP BY u.id");
//        sb.append(" order by u.lastModifiedDate desc");
        return sb.toString();
    }

}