package com.example.auth.model;

import javax.persistence.*;

@Entity
@Table(name = "token")
public class Token {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String token;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user.id")
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
