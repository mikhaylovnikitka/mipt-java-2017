package ru.mipt.java2017.hw3.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Books_Authors")
public class Book_Author {
    @Id
    @GeneratedValue(generator = "customer_gen")
    @GenericGenerator(name = "customer_gen", strategy = "increment")
    @Column(name = "ID")
    private Integer id;

    @Column(name = "books_id")
    private Integer bookId;

    @Column(name = "authors_id")
    private Integer authorId;

    @Column(name = "num")
    private Integer num;

    public Book_Author(int bookId, int authorId, int num) {
        this.bookId = bookId;
        this.authorId = authorId;
        this.num = num;
    }

    public Integer getId() {
        return id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public Integer getNum() {
        return num;
    }
}
