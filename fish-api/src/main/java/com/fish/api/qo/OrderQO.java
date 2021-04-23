package com.fish.api.qo;

import com.common.api.qo.QueryObject;
import lombok.Data;

@Data
public class OrderQO implements QueryObject {

    private String userName;

    private Integer status;

    private Integer isTurn;

}
