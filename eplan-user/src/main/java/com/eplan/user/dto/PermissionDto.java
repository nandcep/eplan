package com.eplan.user.dto;

import java.io.Serializable;
import lombok.Data;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Data
public class PermissionDto implements Serializable {
    
    private static final long serialVersionUID = -5021091736971606544L;
    
    private Long id;
    private String name;
    private Long level;
    private Long parentId;
    private String title;
    private String icon;
    private String type;
    private String path;
    private String api;

}