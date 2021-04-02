package com.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity(name = "User")
@Table(name="user", catalog="fish")
public class User extends AuditableEntity {

    public static final String TYPE_ENT = "ent";

    public static final String TYPE_CONSUMER = "cus";

    public static final String TYPE_ADMIN = "admin";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String name;

    private String phone;

    private String email;

    private String address;

    private String type;

    private int coin;
}