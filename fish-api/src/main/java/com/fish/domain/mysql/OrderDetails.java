package com.fish.domain.mysql;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "Order")
@Table(name="order_details", catalog="fish")
public class OrderDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long productId;

    private Integer num;

    private Integer price;

    private Integer isTurn;
}
