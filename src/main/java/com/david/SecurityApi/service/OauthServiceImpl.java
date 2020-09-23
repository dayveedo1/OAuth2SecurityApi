package com.david.SecurityApi.service;

import com.david.SecurityApi.model.OauthClientDetails;
import com.david.SecurityApi.model.Permission;
import com.david.SecurityApi.model.Role;
import com.david.SecurityApi.model.User;
import com.david.SecurityApi.repository.OauthClientDetailsRepo;
import com.david.SecurityApi.repository.PermissionRepository;
import com.david.SecurityApi.repository.RoleRepository;
import com.david.SecurityApi.repository.UserRepository;
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
}
