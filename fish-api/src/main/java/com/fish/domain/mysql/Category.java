package com.fish.domain.mysql;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "Category")
@Table(name="category", catalog="fish")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entId")
    private Long entId;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "parent")
    private String parent;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "description")
    private String description;
}
