package com.fish.repository;

import com.fish.domain.mysql.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository  extends JpaRepository<Order, Long>,OrderSupportRepository{

}
