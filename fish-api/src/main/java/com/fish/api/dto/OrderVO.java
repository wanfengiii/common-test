package com.fish.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderVO {
    private Long id;

    private String desc;

    private Integer isTurn = 0;

    private List<OrderDetailsVO> vos;
}
