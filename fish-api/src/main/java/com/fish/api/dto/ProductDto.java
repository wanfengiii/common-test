package com.fish.api.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProductDto {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String category;

    private String desc;

    @NotNull
    private Integer price;

    private Integer realPrice;

    private Integer isTurn;

    private Integer status;

    private String tag;

    private String image1;
    private String image2;
    private String image3;
}


