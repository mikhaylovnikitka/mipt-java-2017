package ru.mipt.java2017.hw3.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Authors")
public class Author {
    @Id
    @GeneratedValue(generator = "customer_gen")
    @GenericGenerator(name = "customer_gen", strategy = "increment")
    @Column(name = "ID")
    private Integer id;

    @Column(name = "name", length = 50)
    private String name;

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
}

