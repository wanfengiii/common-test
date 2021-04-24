package com.fish.api.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrderVO {
    @NotNull
    private Long id;

    private Long entId;

    private String desc;

    private Integer isTurn = 0;

    private List<OrderDetailsVO> vos;
}
