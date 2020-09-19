package com.eplan.user.request;

import java.io.Serializable;

import lombok.Data;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 120349567951652824L;
    
    private String username;
    private String password;
    private Boolean isRemember;
    
}