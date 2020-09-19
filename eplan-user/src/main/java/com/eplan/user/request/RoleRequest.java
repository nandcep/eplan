package com.eplan.user.request;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Data
public class RoleRequest implements Serializable {

    private static final long serialVersionUID = 5310575068816121911L;

    private Long id;
    private String name;
    private String description;
    private Boolean isDeleted;
    private Integer status;
    private List<Long> permissionIds;

}