package com.eplan.user.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import com.eplan.user.dto.PermissionDto;
import com.eplan.user.entity.Permission;
import com.eplan.user.repository.PermissionRepository;
import com.eplan.user.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    private PermissionDto getFetchToDto(Permission mapper){
        PermissionDto dto = new PermissionDto();
        dto.setName(mapper.getName());
        dto.setApi(mapper.getApi());
        dto.setId(mapper.getId());
        dto.setParentId(mapper.getParentId() != null ? mapper.getParentId() : null);
        dto.setTitle(mapper.getTitle());
        dto.setPath(mapper.getPath());
        dto.setIcon(mapper.getIcon());
        dto.setLevel(mapper.getLevel());
        log.info("fetchToDto : {}", dto);
        return dto;
    }

    @Override
    public List<PermissionDto> getMenuList() {
        log.info("MenuService.getMenuList [start]");
        List<PermissionDto> result = this.permissionRepository.getActiveMenus().stream().map(obj -> {
            return this.getFetchToDto(obj);
        }).collect(Collectors.toList());
        log.info("MenuService.getMenuList [end]");
        return result;
    }

    @Override
    public List<PermissionDto> getMenuListByRoleId(Long roleId) {
        log.info("MenuService.getMenuListByRoleId [start]");
        List<PermissionDto> result = this.permissionRepository.getPermissionListByRoleId(roleId).stream().map(obj -> {
            return this.getFetchToDto(obj);
        }).collect(Collectors.toList());
        log.info("MenuService.getMenuListByRoleId [end]");
        return result;
    }

    @Override
    public List<PermissionDto> getPermissionByParentId(Long parentId) {
        log.info("MenuService.getPermissionByParentId [start]");
        List<PermissionDto> result = this.permissionRepository.getPermissionByParentId(parentId).stream().map(obj -> {
            return this.getFetchToDto(obj);
        }).collect(Collectors.toList());
        log.info("MenuService.getPermissionByParentId [end]");
        return result;
    }
    
}
