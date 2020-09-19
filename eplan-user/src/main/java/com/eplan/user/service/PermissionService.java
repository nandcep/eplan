package com.eplan.user.service;

import java.util.List;

import com.eplan.user.dto.PermissionDto;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
public interface PermissionService {
    
    public List<PermissionDto> getMenuList();
    public List<PermissionDto> getMenuListByRoleId(Long roleId);
    public List<PermissionDto> getPermissionByParentId(Long parentId);

}
