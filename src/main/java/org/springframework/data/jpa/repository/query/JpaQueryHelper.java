package org.springframework.data.jpa.repository.query;

import com.common.api.qo.QueryObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 
 * I need some package visible class in org.springframework.data.jpa.repository.query
 * 
 */
public class JpaQueryHelper<T> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private static final ConversionService CONVERSION = new DefaultConversionService();
    private final SpelExpressionParser parser = PARSER;
    private ConversionService conversionService = CONVERSION;

    private final DeclaredQuery query;
	private final DeclaredQuery countQuery;
    private final EntityManager em;
    private final JpaEntityMetadata<T> meta;

    public JpaQueryHelper(EntityManager em, JpaEntityMetadata<T> meta, String queryString) {
        this.em = em;
        this.meta = meta;
        this.query = new ExpressionBasedStringQuery(queryString, meta, parser);
        DeclaredQuery countQuery = query.deriveCountQuery(null, null);
        this.countQuery = ExpressionBasedStringQuery.from(countQuery, meta, parser);
    }

    public JpaQueryHelper(EntityManager em, JpaEntityMetadata<T> meta, String countQueryString, String queryString) {
        this.em = em;
        this.meta = meta;
        this.query = new ExpressionBasedStringQuery(queryString, meta, parser);
        DeclaredQuery countQuery = query.deriveCountQuery(countQueryString, null);
        this.countQuery = ExpressionBasedStringQuery.from(countQuery, meta, parser);
    }

    private long count(Query q) {
        Object result = q.getSingleResult();
        return conversionService.convert(result, Long.class);
    }

    private Query createCountQuery(boolean nativeQuery) {
		String queryString = countQuery.getQueryString();
		return nativeQuery
				? em.createNativeQuery(queryString)
				: em.createQuery(queryString, Long.class);
    }

    private Query createQuery(Pageable pageable, boolean nativeQuery) {
        String queryString = QueryUtils.applySorting(query.getQueryString(), pageable.getSort(), query.getAlias());
        Query q = nativeQuery
                ? em.createNativeQuery(queryString, meta.getJavaType())
                : em.createQuery(queryString, meta.getJavaType());
        q = q.setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize());
        return q;
    }

    private Query bindValues(Query q, QueryObject qo) {
        Set<Parameter<?>> params = q.getParameters();
        if (params != null && params.size() > 0) {
            BeanWrapper bw = new BeanWrapperImpl(qo);
            for (Parameter<?> p : params) {
                String name = p.getName();
                if (StringUtils.isNotBlank(name)) {
                    q.setParameter(name, bw.getPropertyValue(name));
                }
            }
        }
        return q;
    }

    public Page<T> getPageResult(QueryObject qo, Pageable pageable) {
        return getPageResult(qo, pageable, false);
    }

    @SuppressWarnings("unchecked")
    public Page<T> getPageResult(QueryObject qo, Pageable pageable, boolean nativeQuery) {
        long count = count(qo, nativeQuery);
        List<T> content = Collections.<T>emptyList();
        if (count > 0L) {
            Query q = createQuery(pageable, nativeQuery);
            bindValues(q, qo);
            content = q.getResultList();
        }
        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    public long count(QueryObject qo) {
        return count(qo, false);
    }

    public long count(QueryObject qo, boolean nativeQuery) {
		Query q = createCountQuery(nativeQuery);
        q = bindValues(q, qo);
        return count(q);
	}

}