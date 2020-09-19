package com.eplan.gateway.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Data
public class UserLoginDto implements Serializable {

    private static final long serialVersionUID = 1769784875394821331L;

    private Long id;
    private String username;
    private String fullname;
    private Long loggedTime;
    private Boolean isRemember;
    private Long roleId;
    private List<String> permissions;
    private String token;
    
}
