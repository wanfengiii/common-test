package com.fish.repository;

import com.common.repository.AbstractJpaRepository;
import com.fish.api.dto.OrderDTO;
import com.fish.api.qo.OrderQO;
import com.fish.domain.mysql.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

public class OrderSupportRepositoryImpl extends AbstractJpaRepository<Order, Long> implements OrderSupportRepository {
        public OrderSupportRepositoryImpl(EntityManager em) {
        super(Order.class, em);
    }


    @Override
    public Page<OrderDTO> getOrder(OrderQO qo, Pageable pageable) {

        StringBuilder sqlSelect = new StringBuilder("SELECT t1.*");
        StringBuilder sqlContent = new StringBuilder(" FROM order t1")
                .append(" where t1.entId = :entId");


        StringBuilder sqlOrder = new StringBuilder();
        if (StringUtils.isNotBlank(qo.getUserName())) {
            sqlContent.append(" AND t1.createdBy = :userName");
        }

        if (null != qo.getStatus()) {
            sqlContent.append(" AND t1.status = :status");
        }

        if(null != qo.getIsTurn()){
            sqlContent.append(" AND t1.isTurn = :isTurn");
        }

        sqlOrder.append(" ORDER BY t1.createdDate desc");
        String querySql = sqlSelect.append(sqlContent).append(sqlOrder).toString();
        String countSql = "SELECT COUNT(t1.id)" + sqlContent.toString();

        return getPageResult(countSql, querySql, qo, pageable, OrderDTO.class);
    }
}

