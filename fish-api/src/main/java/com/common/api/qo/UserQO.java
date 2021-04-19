package com.common.api.qo;

import lombok.Data;

@Data
public class UserQO implements QueryObject {

    // match username, firstname, lastname
    private String q;

    private String type;

    private Long roleId;

}