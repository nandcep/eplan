package com.eplan.user.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.eplan.user.dto.UserDto;
import com.eplan.user.dto.UserLoginDto;
import com.eplan.user.entity.Permission;
import com.eplan.user.entity.Role;
import com.eplan.user.entity.User;
import com.eplan.user.repository.RoleRepository;
import com.eplan.user.repository.UserRepository;
import com.eplan.user.request.UserLoginRequest;
import com.eplan.user.request.UserRequest;
import com.eplan.user.service.UserService;
import com.eplan.user.util.CacheUtility;
import com.eplan.user.util.UserConstants;
import com.eplan.user.util.UserUtility;
import com.eplan.user.util.WebUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Value("${eplan.user.password}")
    private String eplanUserPassword;

    @Value("${eplan.user.session.login}")
    private Integer eplanUserSessionLogin;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserUtility userUtility;

    @Autowired
    private WebUtility webUtility;

    @Autowired
    private CacheUtility cacheUtility;

    private void doValidateRequest(UserRequest request) {
        if (StringUtils.isEmpty(request.getEmail())) {
            log.error("Email is required = {}", request);
            webUtility.doThrowResponseException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (StringUtils.isEmpty(request.getUsername())) {
            log.error("Username is required = {}", request);
            webUtility.doThrowResponseException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (StringUtils.isEmpty(request.getFullname())) {
            log.error("Fullname is required = {}", request);
            webUtility.doThrowResponseException(HttpStatus.BAD_REQUEST, "Fullname is required");
        }
        if (StringUtils.isEmpty(request.getPhoneNo())) {
            log.error("Phone is required = {}", request);
            webUtility.doThrowResponseException(HttpStatus.BAD_REQUEST, "Phone is required");
        }
    }

    @Override
    public Boolean create(UserRequest request, Long loggedUser) {
        log.info("UserService.create = {} by {}", request, loggedUser);
        this.doValidateRequest(request);
        if (this.userRepository.getUserCountByUsername(request.getUsername()) > 0) {
            webUtility.doThrowResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Username already registered");
        }
        if (this.userRepository.getUserCountByEmail(request.getEmail()) > 0) {
            webUtility.doThrowResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Email already registered");
        }
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNo(request.getPhoneNo());
        newUser.setFullname(request.getFullname());
        newUser.setIsDeleted(false);
        newUser.setPassword(passwordEncoder.encode(eplanUserPassword));
        newUser.setStatus(UserConstants.USER_STATUS.ACTIVE.getValue());
        Role existingRole = this.roleRepository.getOne(request.getRoleId());
        newUser.setRole(existingRole);
        newUser.setCreatedBy(loggedUser);
        this.userRepository.save(newUser);
        return (newUser.getId() != null);
    }

    @Override
    public Boolean update(UserRequest request, Long loggedUser) {
        log.info("UserService.update = {} by {}", request, loggedUser);
        this.doValidateRequest(request);
        if (UserConstants.USER_STATUS.valueOf(request.getStatus()) == null) {
            webUtility.doThrowResponseException(HttpStatus.BAD_REQUEST, "Invalid status value");
        }
        User existingUser = this.userRepository.getOne(request.getId());
        if (existingUser == null) {
            webUtility.doThrowResponseException(HttpStatus.NOT_FOUND, "Data is not found");
        }
        existingUser.setEmail(request.getEmail());
        existingUser.setPhoneNo(request.getPhoneNo());
        existingUser.setFullname(request.getFullname());
        existingUser.setStatus(request.getStatus());
        existingUser.setCreatedBy(loggedUser);
        Role existingRole = this.roleRepository.getOne(request.getRoleId());
        existingUser.setRole(existingRole);
        this.userRepository.save(existingUser);
        return true;
    }

    @Override
    public Boolean delete(Long id, Long loggedUser) {
        User existingUser = this.userRepository.getOne(id);
        if (existingUser == null) {
            webUtility.doThrowResponseException(HttpStatus.NOT_FOUND, "Data is not found");
        }
        existingUser.setIsDeleted(true);
        existingUser.setUpdatedBy(loggedUser);
        this.userRepository.save(existingUser);
        return true;
    }

    @Override
    public Page<UserDto> getUsers(Pageable pageable, Specification specs) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserDto getUserById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserDto getUserByUsername(String username) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserLoginDto signIn(UserLoginRequest request, Authentication authentication) {
        String userLoginCache = this.cacheUtility.get("USERLOGIN", request.getUsername());
        UserLoginDto userLoginDto = new UserLoginDto();
        if (!StringUtils.isEmpty(userLoginCache)) {
            try {
                userLoginDto = new ObjectMapper().readValue(userLoginCache, UserLoginDto.class);
                return userLoginDto;
            } catch (Exception e) {
                log.error("signIn.e = {}", e.getMessage());
                webUtility.doThrowResponseException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
            }
        }
        authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Boolean isAuthenticated = authentication.isAuthenticated();
        if (!isAuthenticated) {
            webUtility.doThrowResponseException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
        }
        log.info("signIn.isAuthenticated for {} = {}", request, isAuthenticated);
        User existingUser = this.userRepository.getUserByUsername(request.getUsername());
        String jwtToken = this.userUtility.getNewJwt(authentication);
        userLoginDto.setId(existingUser.getId());
        userLoginDto.setFullname(existingUser.getFullname());
        userLoginDto.setLoggedTime(new Date().getTime());
        userLoginDto.setUsername(existingUser.getUsername());
        userLoginDto.setRoleId(existingUser.getRole().getId());
        userLoginDto.setIsRemember(request.getIsRemember());
        Set<Permission> permissions = existingUser.getRole().getPermissions();
        List<String> permissionList = new ArrayList<>();
        for (Permission permission : permissions) {
            if (!permissionList.contains(permission.getApi()) && permission.getParentId() != null) {
                permissionList.add(permission.getApi());
            }
        }
        userLoginDto.setPermissions(permissionList);
        userLoginDto.setToken(jwtToken);
        log.info("signIn.userLoginDto {}", userLoginDto);
        log.info("signIn.jwtToken {}", jwtToken);
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.cacheUtility.set("USERLOGIN", userLoginDto.getUsername(),
                    mapper.writeValueAsString(userLoginDto), this.eplanUserSessionLogin);
        } catch (JsonProcessingException e) {
            this.webUtility.doThrowResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return userLoginDto;
    }

    @Override
    public UserDto getFetchToDto(User mapper) {
        UserDto userDto = new UserDto();
        userDto.setId(mapper.getId());
        userDto.setEmail(mapper.getEmail());
        userDto.setFullname(mapper.getFullname());
        userDto.setStatus(UserConstants.USER_STATUS.valueOf(mapper.getStatus()).getMsg());
        userDto.setIsDeleted(mapper.getIsDeleted());
        userDto.setRoleId(mapper.getRole().getId());
        return userDto;
    }

    @Override
    public Boolean signOut(Long currentUserId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getUserCountByEmail(String email) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getUserCountByUsername(String email) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
