package com.david.SecurityApi.service;

import com.david.SecurityApi.model.*;
import com.david.SecurityApi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OauthServiceImpl implements OauthService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    PermissionRepository permissionRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    OauthClientDetailsRepo oauthClientDetailsRepo;

    @Autowired
    RoleUserRepository roleUserRepo;

    @Override
    public User saveUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public Permission savePermission(Permission permission) {
        return permissionRepo.save(permission);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepo.save(role);
    }

    @Override
    public OauthClientDetails saveClientDetails(OauthClientDetails details) {
        return oauthClientDetailsRepo.save(details);
    }

    @Override
    public void saveAllPermissions(List<Permission> permissionList) {
        permissionRepo.saveAll(permissionList);
    }

    @Override
    public void saveAllRoles(List<Role> roleList) {
        roleRepo.saveAll(roleList);
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepo.findAll();
    }

    @Override
    public void deleteUserRole(Long id) {
        userRepo.deleteUserById(id);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepo.findById(id);
    }

    @Override
    public Optional<Role> findRoleByName(String name) {
        return roleRepo.findRoleByName(name);
    }

    @Override
    public Optional<Role> findRoleById(Long id) {
        return roleRepo.findById(id);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepo.deleteUserById(id);
    }

    @Override
    public Optional<RoleUser> findRoleUserById(Long userId, Long roleId) {
        return roleUserRepo.findByUserIdAndRoleId(userId, roleId);
    }

    @Override
    public void deleteRoleUser(RoleUser roleUser) {
        roleUserRepo.delete(roleUser);
    }

    @Override
    public RoleUser saveRoleUser(RoleUser roleUser) {
        return roleUserRepo.save(roleUser);
    }
}
