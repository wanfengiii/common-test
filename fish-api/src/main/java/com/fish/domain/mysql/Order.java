package com.fish.domain.mysql;

import com.common.domain.AuditableEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "Order")
@Table(name="order", catalog="fish")
public class Order extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer price;

    private Integer realPrice;

    private Integer status;

    private String desc;

    private String phone;

    private String userName;

    private String address;

    private Integer isTurn;
}
