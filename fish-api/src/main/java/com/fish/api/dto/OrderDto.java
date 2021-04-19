package com.fish.api.dto;

import lombok.Data;

@Data
public class OrderDto {
    private Long id;

    private Integer price;

    private Integer realPrice;

    private Integer status;

    private String desc;

    private Integer isTurn;
}
