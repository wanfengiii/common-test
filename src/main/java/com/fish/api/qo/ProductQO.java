package com.fish.api.qo;

import com.common.api.qo.QueryObject;
import lombok.Data;

@Data
public class ProductQO implements QueryObject {

    private String name;

    private String category;

    private Integer priceStart;

    private Integer priceEnd;

    private Integer isTurn;

}
