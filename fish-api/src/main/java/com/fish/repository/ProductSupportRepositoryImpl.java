package com.fish.repository;

import com.common.repository.AbstractJpaRepository;
import com.fish.api.dto.ProductDto;
import com.fish.api.qo.ProductQO;
import com.fish.domain.mysql.Product;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class ProductSupportRepositoryImpl extends AbstractJpaRepository<Product, Long> implements ProductSupportRepository {
    public ProductSupportRepositoryImpl(EntityManager em) {
        super(Product.class, em);
    }


    @Override
    public Page<ProductDto> getProduct(ProductQO qo, Pageable pageable) {

        StringBuilder sqlSelect = new StringBuilder("SELECT t1.*");
        StringBuilder sqlContent = new StringBuilder(" FROM product t1")
                .append(" INNER JOIN category t2 ON t1.category = t2.code")
                .append(" where t1.status = 1");


        StringBuilder sqlOrder = new StringBuilder();
        if (StringUtils.isNotBlank(qo.getCategory())) {
            sqlContent.append(" AND t2.code = :category or t2.parent = :category");
        }
        if (StringUtils.isNotBlank(qo.getName())) {
            sqlContent.append(" AND (t1.name like :name or t1.tag like :name)");
            toLikeValue(qo, "name");
        }

        if(null != qo.getIsTurn()){
            sqlContent.append(" AND t1.isTurn = :isTurn");
        }

        if(null != qo.getPriceStart()){
            sqlContent.append(" AND t1.price >= :priceStart");
        }

        if(null != qo.getPriceStart()){
            sqlContent.append(" AND t1.price <= :priceEnd");
        }

        sqlOrder.append(" ORDER BY t1.createdDate desc");
        String querySql = sqlSelect.append(sqlContent).append(sqlOrder).toString();
        String countSql = "SELECT COUNT(t1.id)" + sqlContent.toString();

        return getPageResult(countSql, querySql, qo, pageable, ProductDto.class);
    }
}