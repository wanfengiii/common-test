package com.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
public class User extends AuditableEntity {

    public static final String TYPE_GOV = "gov";

    public static final String TYPE_ENT = "ent";

    public static final String TYPE_CONSUMER = "consumer";

    public static final String TYPE_ADMIN = "admin";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String firstname;

    private String lastname;

    private String phone;

    private String email;

    private String avatar;

    private String type;

    private String district;

    private String institute;

}