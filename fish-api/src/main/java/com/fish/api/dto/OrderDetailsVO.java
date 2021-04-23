package com.fish.api.dto;

import lombok.Data;

@Data
public class OrderDetailsVO {

    private Long productId;

    private Integer num;

    private Integer isTurn = 0;
}
