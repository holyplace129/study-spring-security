package com.security.springsecurity.user.domain.repository;

import com.security.springsecurity.user.domain.Provider;
import com.security.springsecurity.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndProvider(String email, String provider);

    boolean existsByEmail(String email);
}
