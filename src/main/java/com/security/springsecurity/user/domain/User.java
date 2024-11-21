package com.security.springsecurity.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity(name = "users")
@Getter
@NoArgsConstructor
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String password;

    private String authority;

    private Boolean isDeleted;

    private String provider;

    @Builder
    public User(String name, String email, String password, String authority, Boolean isDeleted, String provider) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.authority = authority;
        this.isDeleted = isDeleted;
        this.provider = provider;
    }

    public User updateUser(String name, String email) {
        this.name = name;
        this.email = email;
        return this;
    }
}
