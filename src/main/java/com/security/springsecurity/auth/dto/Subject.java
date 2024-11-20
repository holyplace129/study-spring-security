package com.security.springsecurity.auth.dto;
import lombok.Getter;

@Getter
public class Subject {
    private final Long id;
    private final String email;
    private final String type;
    private final String authority;

    private Subject(Long id, String email, String type, String authority) {
        this.id = id;
        this.email = email;
        this.type = type;
        this.authority = authority;
    }

    public static Subject atk(Long id, String email, String authorities) {
        return new Subject(id, email, "ATK", authorities);
    }

    public static Subject rtk(Long id, String email, String authorities) {
        return new Subject(id, email, "RTK", authorities);
    }
}