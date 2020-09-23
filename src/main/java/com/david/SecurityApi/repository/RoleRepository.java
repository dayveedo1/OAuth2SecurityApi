package com.david.SecurityApi.repository;

import com.david.SecurityApi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
