package com.david.SecurityApi.repository;

import com.david.SecurityApi.model.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleUserRepository extends JpaRepository<RoleUser, Long> {

    @Query("select c from RoleUser c where c.userId.id =?1 AND c.roleId.id = ?2")
    Optional<RoleUser> findByUserIdAndRoleId(Long userId, Long roleId);
}
