package com.david.SecurityApi.controller;

import com.david.SecurityApi.constants.ApiResponse;
import com.david.SecurityApi.constants.CustomMessages;
import com.david.SecurityApi.dto.LoginRequest;
import com.david.SecurityApi.dto.UserDto;
import com.david.SecurityApi.exceptions.CustomException;
import com.david.SecurityApi.exceptions.RecordAlreadyPresentException;
import com.david.SecurityApi.exceptions.RecordNotFoundException;
import com.david.SecurityApi.model.OauthClientDetails;
import com.david.SecurityApi.model.Role;
import com.david.SecurityApi.model.RoleUser;
import com.david.SecurityApi.model.User;
import com.david.SecurityApi.repository.UserRepository;
import com.david.SecurityApi.service.OauthService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/user")
@Validated
public class UserController {

    @Autowired
    OauthService oauthService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

//    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    ModelMapper modelMapper = new ModelMapper();

    @PostMapping("/authenticateAndGetUserRoles")
    public ResponseEntity authenticateUser(@RequestBody LoginRequest loginRequest){

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User principal = (User) authentication.getPrincipal();

        UserDto userDto = modelMapper.map(principal, UserDto.class);

        //get User ID from DB
        Optional<User> userIdToGet = oauthService.getUserByUsername(principal.getUsername());

        userDto.setId(userIdToGet.get().getId());
        return ResponseEntity.ok().body(new ApiResponse<>(CustomMessages.Success, userDto));
    }

    @ApiOperation("To return all Active Users")
    @GetMapping("/user")
    public Object getAllUser(){
        List<User> userInfos = userRepository.findAll();
        if(userInfos == null || userInfos.isEmpty()){
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }
        return userInfos;
    }

    @ApiOperation("To create a new user")
    @PostMapping("/createUser")
    public ResponseEntity addUser(@RequestBody User userRecord){

        Optional<User> findUserByUsername = oauthService.getUserByUsername(userRecord.getUsername());

        try{
            if (!findUserByUsername.isPresent()){

                if (userRecord == null){
                    return ResponseEntity.badRequest().body(Collections.singletonMap("Message", "Null Object was submitted"));
                }

                String encodedPassword = passwordEncoder.encode(userRecord.getPassword());
                String encodedClientSecret = passwordEncoder.encode(userRecord.getClientSecret());

                userRecord.setPassword(encodedPassword);
                userRecord.setAccountNonExpired(true);
                userRecord.setEnabled(true);
                userRecord.setDateCreated(new Date());
                userRecord.setCredentialsNonExpired(true);
                userRecord.setAccountNonLocked(true);

                OauthClientDetails oauthClientDetails = new OauthClientDetails();
                oauthClientDetails.setAccessTokenValidity(3600);
                oauthClientDetails.setAutoapprove("");
                oauthClientDetails.setAdditionalInformation("{}");
                oauthClientDetails.setAuthorizedGrantTypes("authorization_code,password,refresh_token,implicit");
                oauthClientDetails.setClientId(userRecord.getUsername());
//                oauthClientDetails.setClientId(userRecord.getClientId());
                oauthClientDetails.setClientSecret(encodedPassword);
//                oauthClientDetails.setClientSecret(encodedClientSecret);
                oauthClientDetails.setRefreshTokenValidity(10000);
                oauthClientDetails.setResourceIds("security-api");
//                oauthClientDetails.setAuthorities("ROLE_user, Role_admin");
                oauthClientDetails.setScope("READ, WRITE");
                oauthClientDetails.setWebServerRedirectUri("http://localhost:8081/");

                System.out.println(userRecord);

                oauthService.saveUser(userRecord);
                oauthService.saveClientDetails(oauthClientDetails);

                return ResponseEntity.ok(new ApiResponse<>(CustomMessages.Success, userRecord));

            }
            else throw new RecordAlreadyPresentException(
                    "User with username: " + userRecord.getUsername() + "already exists!!"
            );
        }
        catch(RecordAlreadyPresentException e){
            return ResponseEntity.ok().body(new ApiResponse<>(CustomMessages.AlreadyExist, "User with username: " + userRecord.getUsername() + " already exists!!"));
        }
    }

//    @ApiOperation("To edit user details")
//    @PutMapping("/edit/user")
//    public ResponseEntity editUser(@RequestBody User userRecord, @PathVariable("id") Long id){
//        Optional<User> u = oauthService.findUserById(id).map(record->{
//            User user = new User();
//            user.setEmail(userRecord.getEmail());
//            user.setFirstName(userRecord.getFirstName());
//            user.setLastName(userRecord.getLastName());
//            user.setMiddleName(userRecord.getMiddleName());
//            user.setPhone(userRecord.getPhone());
//            userRecord.setUsername(userRecord.getUsername());
//            userRecord.setPassword(userRecord.getPassword());
//            return oauthService.saveUser(record);
//        });
//        return ResponseEntity.ok(new ApiResponse<>(CustomMessages.Success));
//    }

    @ApiOperation("To add role to user")
    @PostMapping(value = "/add/userRole", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addUserRole(@RequestBody User user){
        Set<Role> roleList = user.getRoles().stream().distinct().collect(Collectors.toSet());
        user.setRoles(roleList);
        User savedUserRole  = oauthService.saveUser(user);
        return ResponseEntity.ok(new ApiResponse<>(CustomMessages.Success, savedUserRole));
    }

    @ApiOperation("To save roles")
    @PostMapping("add/role")
    public ResponseEntity addRoles(@RequestBody Role role){

        //check if role name already exist
        Optional<Role> checkRole = oauthService.findRoleByName(role.getName());

        if(checkRole.isPresent()){
            return ResponseEntity.ok(new ApiResponse<>(CustomMessages.Failed, CustomMessages.AlreadyExist));
        }

        //if doesn;t exist, add role name records
        Role roles = oauthService.saveRole(role);
        return ResponseEntity.ok(new ApiResponse<>(CustomMessages.Success, roles));
    }

    @ApiOperation("To edit role")
    @PutMapping("/edit/role")
    public ResponseEntity editRoles(@RequestBody Role role){

        //find the role id to be edited
        Optional<Role> r = oauthService.findRoleById(role.getId());

        if(r.isPresent()){
            r.get().setName(role.getName());
            Role roles = oauthService.saveRole(r.get());
            return ResponseEntity.ok(new ApiResponse<>(CustomMessages.Success, roles));
        }
        return ResponseEntity.ok(new ApiResponse<>(CustomMessages.NotFound, "Role to edit not found"));
    }

    @ApiOperation("Delete role by id")
    @DeleteMapping("/role/delete/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable ("id") Long id){

        //find the role to be deleted by the role_id
        return oauthService.findRoleById(id).map(record-> {
            oauthService.deleteUserRole(id);
            return ResponseEntity.ok(new ApiResponse<>(CustomMessages.Deleted, CustomMessages.DeletedMessage));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(CustomMessages.NotFoundMessage)));
    }

    @ApiOperation("To return list of all roles")
    @GetMapping("/list/role")
    public List<Role> getAllRoles(){
        List<Role> roleList = oauthService.findAllRoles();
        return roleList;
    }

    @ApiOperation("Get user by Id")
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id){
        Optional<User> user = oauthService.getUserById(id);

        if(user.isPresent()){
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("To delete a user byId")
    @DeleteMapping("/user/{id}")
    public ResponseEntity deleteUserById(@PathVariable ("id") Long id){
        oauthService.deleteUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(CustomMessages.Deleted, CustomMessages.DeletedMessage));
    }

    @ApiOperation("To return list of all users")
    @GetMapping("/list/user")
    public List<User> getAllUsers(){
        return oauthService.findAll();
    }

    @ApiOperation("To delete role assigned to a user")
    @DeleteMapping("/deleteAsignedRole/{userId}/{roleId}")
    public ResponseEntity deleteAsignedRole(@PathVariable ("userId") Long userId, @PathVariable ("roleId") Long roleId ){

        Optional<RoleUser> ru = oauthService.findRoleUserById(userId, roleId);

        if (ru.isPresent()){
            oauthService.deleteRoleUser(ru.get());
            return ResponseEntity.ok(new ApiResponse<>(CustomMessages.Deleted, CustomMessages.DeletedMessage));
        }
        return ResponseEntity.ok(new ApiResponse<>(CustomMessages.NotFound, CustomMessages.NotFoundMessage));
    }

    @ApiOperation("To add a role to a user")
    @PostMapping("assign/role/user")
    public ResponseEntity addRoleToUser(@RequestParam ("userId") Long userId, @RequestParam ("roleId") Long roleId){

        Optional<User> searchedUser = oauthService.findUserById(userId);
        Optional<Role> searchedRole = oauthService.findRoleById(roleId);

        RoleUser ru = new RoleUser();
        ru.setUserId(searchedUser.get());
        ru.setRoleId(searchedRole.get());

        RoleUser savedRu = oauthService.saveRoleUser(ru);

        return ResponseEntity.ok(new ApiResponse<>(CustomMessages.Success, savedRu));

    }
}
