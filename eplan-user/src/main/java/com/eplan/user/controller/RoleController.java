package com.eplan.user.controller;

import com.eplan.user.request.RoleRequest;
import com.eplan.user.service.RoleService;
import com.eplan.user.util.Response;
import com.eplan.user.util.UserUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/role")
@Slf4j
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserUtility userUtility;

    @PostMapping("/v1/create")
    public ResponseEntity<Response> createRole(@RequestBody RoleRequest request){
        log.debug("createRole someone sent = {}", request);
        Long loggedUser = 0L;
        Boolean isCreated = this.roleService.create(request, loggedUser);
        Response response = new Response(null, "Success to create role", isCreated);
        log.info("createRole result = {}", isCreated);
        return new ResponseEntity(response, HttpStatus.OK);
    }

}
