package com.security.springsecurity.user.domain;

import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Builder
    public User(String email, String password, String authority, Boolean isDeleted, Provider provider) {
        this.email = email;
        this.password = password;
        this.authority = authority;
        this.isDeleted = isDeleted;
        this.provider = provider;
    }
}
