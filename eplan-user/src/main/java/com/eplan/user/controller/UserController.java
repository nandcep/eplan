package com.eplan.user.controller;

import javax.servlet.http.HttpServletResponse;
import com.eplan.user.dto.UserLoginDto;
import com.eplan.user.request.UserLoginRequest;
import com.eplan.user.request.UserRequest;
import com.eplan.user.service.UserService;
import com.eplan.user.util.Response;
import com.eplan.user.util.WebUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@RestController
@RequestMapping
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WebUtility webUtility;
    
    @PostMapping("/v1/create")
    public ResponseEntity<Response> createUser(@RequestBody UserRequest request){
        log.debug("createUser someone sent = {}", request);
        Long loggedUser = 0L;
        Boolean isCreated = this.userService.create(request, loggedUser);
        Response response = new Response(null, "Success to create user", isCreated);
        log.info("createUser result = {}", isCreated);
        return new ResponseEntity(response, HttpStatus.OK);
    }
    
    @PostMapping("/v1/sign-in")
    public ResponseEntity<Response> signIn(@RequestBody UserLoginRequest request, Authentication authentication, HttpServletResponse servletResponse){
        log.debug("signIn someone sent = {}", request);
        UserLoginDto userLoginDto = this.userService.signIn(request, authentication);
        webUtility.doWriteCookie(servletResponse, "Authorization", userLoginDto.getToken(), null, false, true);
        userLoginDto.setToken(null);
        userLoginDto.setPermissions(null);
        Response response = new Response(userLoginDto, "Success to sign in", true);
        log.info("createUser result = {}", response);
        return new ResponseEntity(response, HttpStatus.OK);
    }
    
}
