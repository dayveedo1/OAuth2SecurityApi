package com.david.SecurityApi.repository;

import com.david.SecurityApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    public Optional<User> findUserById(Long id);

    public void deleteUserById(Long id);
}
