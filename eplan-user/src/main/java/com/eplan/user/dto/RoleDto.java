package com.eplan.user.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Data
public class RoleDto implements Serializable {

    private static final long serialVersionUID = -3615782053458034546L;
    
    private Long id;
    private String name;
    private String description;
    private Boolean isDeleted;
    private String status;
    private Long createdDate;
    private String createdBy;
    private Long updatedDate;
    private String updatedBy;
    private List<PermissionDto> permissions;
    
}