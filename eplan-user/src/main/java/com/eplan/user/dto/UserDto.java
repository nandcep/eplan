package com.eplan.user.dto;

import java.io.Serializable;
import lombok.Data;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Data
public class UserDto implements Serializable {
    
    private static final long serialVersionUID = -1244007504388725793L;

    private Long id;
    private String username;
    private String fullname;
    private String email;
    private String phoneNo;
    private String createdBy;
    private Long createdDate;
    private String updatedBy;
    private Long updatedDate;
    private Boolean isDeleted;
    private String status;
    private Long roleId;
    
}