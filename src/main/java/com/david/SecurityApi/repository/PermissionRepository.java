package com.david.SecurityApi.repository;


import com.david.SecurityApi.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
