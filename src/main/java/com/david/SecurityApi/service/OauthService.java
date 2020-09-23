package com.david.SecurityApi.service;

import com.david.SecurityApi.model.OauthClientDetails;
import com.david.SecurityApi.model.Permission;
import com.david.SecurityApi.model.Role;
import com.david.SecurityApi.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


public interface OauthService {

    public User saveUser(User user);

    public Optional<User> getUserByUsername(String username);

    public List<User> findAll();

    public Permission savePermission(Permission permission);

    public Role saveRole(Role role);

    public OauthClientDetails saveClientDetails(OauthClientDetails details);

    public void saveAllPermissions(List<Permission> permissionList);

    public void saveAllRoles(List<Role> roleList);

    public List<Role> findAllRoles();

    public void deleteUserRole(Long id);

    Optional<User> findUserById(Long id);
}
