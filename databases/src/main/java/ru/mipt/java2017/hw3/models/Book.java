package ru.mipt.java2017.hw3.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Books")
public class Book {

    @Id
    @GeneratedValue(generator = "customer_gen")
    @GenericGenerator(name = "customer_gen", strategy = "increment")
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ISBN")
    private Long isbn;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "cover", length = 400)
    private String cover;

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }

    public Integer getId() {
        return id;
    }

    public Long getIsbn() {
        return isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }
}
