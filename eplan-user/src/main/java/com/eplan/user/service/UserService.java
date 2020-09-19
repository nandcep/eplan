package com.eplan.user.service;

import com.eplan.user.dto.UserDto;
import com.eplan.user.dto.UserLoginDto;
import com.eplan.user.entity.User;
import com.eplan.user.request.UserLoginRequest;
import com.eplan.user.request.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
public interface UserService {

    public Boolean create(UserRequest request, Long loggedUser);
    public Boolean update(UserRequest request, Long loggedUser);
    public Boolean delete(Long userId, Long loggedUser);
    public Page<UserDto> getUsers(Pageable pageable, Specification specs);
    public UserDto getUserById(Long userId);
    public UserDto getUserByEmail(String email);
    public UserDto getUserByUsername(String username);
    public UserDto getFetchToDto(User mapper);
    public UserLoginDto signIn(UserLoginRequest request, Authentication authentication);
    public Boolean signOut(Long currentUserId);
    //public Boolean updatePassword(UpdatePasswordRequest request, Long loggedUser);
    //public Boolean updatePassword(UpdatePasswordRequest request);
    //public void doRequestForgotPassword(ForgotPasswordRequest request);
    //public Boolean validateRequestForgotPasswordSession(UpdatePasswordRequest request);
    public Integer getUserCountByEmail(String email);
    public Integer getUserCountByUsername(String email);
    
}