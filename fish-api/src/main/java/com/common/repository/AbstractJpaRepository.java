package com.common.repository;

import com.common.api.qo.QueryObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.JpaQueryHelper;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {

    protected final Logger log = LogManager.getLogger(getClass());

    protected final JpaEntityInformation<T, ?> entityInformation;
    
    protected EntityManager em;

    private Class<T> domainClass;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
	public AbstractJpaRepository(Class<T> domainClass, EntityManager em) {
        super(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
        this.em = em;
        this.domainClass = domainClass;
    }

    protected Page<T> getPageResult(String jpql, QueryObject qo, Pageable pageable) {
        return getJpaQueryHelper(jpql).getPageResult(qo, pageable);
    }

    protected Page<T> getPageResult(String countSql, String jpql, QueryObject qo, Pageable pageable) {
        return getJpaQueryHelper(countSql, jpql).getPageResult(qo, pageable);
    }

    protected JpaQueryHelper<T> getJpaQueryHelper(String jpql) {
        return new JpaQueryHelper<>(em, entityInformation, jpql);
    }

    protected JpaQueryHelper<T> getJpaQueryHelper(String countSql, String jpql) {
        return new JpaQueryHelper<>(em, entityInformation, countSql, jpql);
    }

    protected void toLikeValue(QueryObject qo, String prop) {
        BeanWrapper bw = new BeanWrapperImpl(qo);
        Object val = bw.getPropertyValue(prop);
        if (val == null) {
            return;
        }
        if (!(val instanceof String)) {
            throw new UnsupportedOperationException("prop " + prop + " of class " + qo.getClass().getName() + " for like query should be type of String");
        }

        String s =  (String) val;
        String newVal = (StringUtils.isBlank(s) ? "%" : "%" + s + "%");
        bw.setPropertyValue(prop, newVal);
    }

    @SuppressWarnings("unchecked")
    protected List<T> queryForList(String nativeSql, Object... params) {
        Query q = em.createNativeQuery(nativeSql, domainClass);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                q = q.setParameter(i + 1, params[i]);
            }
        }
        
        return q.getResultList();
    }

    /**
     * for native query via NamedParameterJdbcTemplate
     */
    protected <R> Page<R> getPageResult(String countSql, String querySql, QueryObject q, Pageable pageable, Class<R> clazz) {
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(q);
        Long count = jdbcTemplate.queryForObject(countSql, paramSource, Long.class);

        List<R> content = new ArrayList<>();
        if (count != null && count > 0L) {
            querySql += (" limit " + pageable.getPageSize() + " offset " + pageable.getOffset());
            content =  jdbcTemplate.query(querySql, paramSource, new BeanPropertyRowMapper<R>(clazz));
        }

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    protected <R> List<R> getListResult(String querySql, QueryObject q, Class<R> clazz) {
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(q);
        return jdbcTemplate.query(querySql, paramSource, new BeanPropertyRowMapper<R>(clazz));
    }
}