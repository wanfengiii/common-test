package com.fish.repository;

import com.fish.domain.mysql.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

    @Modifying
    @Query(value ="delete from order_details  where orderId = ?1", nativeQuery = true)
    void deleteByOrderById(Long orderId);

    List<OrderDetails> findByOrderId(Long orderId);
}
