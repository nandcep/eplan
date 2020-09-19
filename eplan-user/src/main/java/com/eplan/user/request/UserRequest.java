package com.eplan.user.request;

import lombok.Data;
import java.io.Serializable;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Data
public class UserRequest implements Serializable {
    
    private static final long serialVersionUID = 2643247599313623757L;

    private Long id;
    private String username;
    private String email;
    private String phoneNo;
    private String fullname;
    private String password;
    private Boolean isDeleted;
    private Integer status;
    private Long roleId;

}