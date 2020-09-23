package com.david.SecurityApi.controller;

import com.david.SecurityApi.constants.ApiResponse;
import com.david.SecurityApi.constants.CustomMessages;
import com.david.SecurityApi.dto.LoginRequest;
import com.david.SecurityApi.dto.UserDto;
import com.david.SecurityApi.exceptions.RecordAlreadyPresentException;
import com.david.SecurityApi.model.OauthClientDetails;
import com.david.SecurityApi.model.User;
import com.david.SecurityApi.repository.UserRepository;
import com.david.SecurityApi.service.OauthService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

}
