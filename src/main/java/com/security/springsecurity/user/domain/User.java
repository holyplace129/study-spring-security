package com.security.springsecurity.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String email;

    private String password;

    private String authority;

    private Boolean isDeleted;

    @Builder
    public User(String email, String password, String authority, Boolean isDeleted) {
        this.email = email;
        this.password = password;
        this.authority = authority;
        this.isDeleted = isDeleted;
    }
}
