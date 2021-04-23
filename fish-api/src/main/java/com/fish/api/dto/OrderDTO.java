package com.fish.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDTO {
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
}
