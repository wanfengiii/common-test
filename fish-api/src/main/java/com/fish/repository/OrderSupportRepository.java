package com.fish.repository;

import com.fish.api.dto.OrderDTO;
import com.fish.api.qo.OrderQO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderSupportRepository {

    Page<OrderDTO> getOrder(OrderQO qo, Pageable pageable);
}
