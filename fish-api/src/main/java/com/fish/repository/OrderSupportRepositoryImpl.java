package com.fish.repository;

import com.common.repository.AbstractJpaRepository;
import com.fish.domain.mysql.Order;

import javax.persistence.EntityManager;

public class OrderSupportRepositoryImpl extends AbstractJpaRepository<Order, Long> implements OrderSupportRepository {
        public OrderSupportRepositoryImpl(EntityManager em) {
        super(Order.class, em);
    }


}

