package com.fish.api.dto;

import com.fish.domain.mysql.OrderDetails;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FullOrderDTO {
    private Long id;

    private Integer price;

    private Integer realPrice;

    private Integer status;

    private String desc;

    private String phone;

    private String userName;

    private String address;

    private Integer isTurn;

    private LocalDateTime createdDate;
    
    private List<OrderDetails> details;
}
