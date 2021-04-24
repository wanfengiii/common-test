package com.fish.domain.mysql;

import com.common.domain.AuditableEntity;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity(name = "Product")
@Table(name="product", catalog="fish")
public class Product extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long entId;

    private String name;

    private String category;

    private String desc;

    private Integer price;

    private Integer realPrice;

    private Integer isTurn;

    private Integer status;

    private String image1;
    private String image2;
    private String image3;

    private String tag;

}


