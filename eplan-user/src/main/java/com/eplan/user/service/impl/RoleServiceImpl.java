package com.eplan.user.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.eplan.user.dto.PermissionDto;
import com.eplan.user.dto.RoleDto;
import com.eplan.user.entity.Permission;
import com.eplan.user.entity.Role;
import com.eplan.user.repository.RoleRepository;
import com.eplan.user.request.RoleRequest;
import com.eplan.user.service.PermissionService;
import com.eplan.user.service.RoleService;
import com.eplan.user.util.UserConstants;
import com.eplan.user.util.WebUtility;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private WebUtility webUtility;

    private void doValidateRequest(final RoleRequest request){
        if(StringUtils.isEmpty(request.getName())){
            log.error("Role name is required = {}", request);
            webUtility.doThrowResponseException(HttpStatus.BAD_REQUEST, "Role name is required");
        }
        if(StringUtils.isEmpty(request.getDescription())){
            log.error("Role description is required = {}", request);
            webUtility.doThrowResponseException(HttpStatus.BAD_REQUEST, "Role description is required");
        }
        if(request.getPermissionIds().isEmpty()){
            log.error("Role permission(s) is required = {}", request);
            webUtility.doThrowResponseException(HttpStatus.BAD_REQUEST, "Role permission(s) is required");
        }
    }

    private Set<Permission> getPermissions(final List<Long> menuIds) {
        log.info("getPermissions.menuIds = {}", menuIds);
        final Set<Permission> permissions = new HashSet<>();
        for (final Long menuId : menuIds) {
            final Permission assignedPermission = new Permission();
            assignedPermission.setId(Long.valueOf(menuId));
            permissions.add(assignedPermission);
            for (final PermissionDto childMenu :this.permissionService.getPermissionByParentId(menuId)) {
                final Permission assignedChildPermission = new Permission();
                assignedChildPermission.setId(Long.valueOf(childMenu.getId()));
                permissions.add(assignedChildPermission);
            }
        }
        return permissions;
    }

    @Override
    public Boolean create(final RoleRequest request, final Long loggedUser) {
        this.doValidateRequest(request);
        Role newRole = new Role();
        newRole.setName(request.getName());
        newRole.setDescription(request.getDescription());
        newRole.setIsDeleted(false);
        newRole.setStatus(UserConstants.ROLE_STATUS.ACTIVE.getValue());
        newRole.setPermissions(getPermissions(request.getPermissionIds()));
        newRole.setCreatedBy(loggedUser);
        newRole = this.roleRepository.save(newRole);
        return newRole.getId() != null;
    }

    @Override
    public Boolean update(final RoleRequest request, final Long loggedUser) {
        this.doValidateRequest(request);
        if(request.getStatus() == null){
            webUtility.doThrowResponseException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        Role existingRole = this.roleRepository.getOne(request.getId());
        if(existingRole == null){
            webUtility.doThrowResponseException(HttpStatus.NOT_FOUND, "Data is not found");
        }
        existingRole.setName(request.getName());
        existingRole.setDescription(request.getDescription());
        existingRole.setUpdatedBy(loggedUser);
        existingRole.setPermissions(getPermissions(request.getPermissionIds()));
        existingRole.setStatus(request.getStatus());
        existingRole = this.roleRepository.save(existingRole);
        return true;
    }

    @Override
    public Boolean delete(final Long id, final Long loggedUser) {
        Role existingRole = this.roleRepository.getOne(id);
        if(existingRole == null){
            webUtility.doThrowResponseException(HttpStatus.NOT_FOUND, "Data is not found");
        }
        existingRole.setIsDeleted(false);
        existingRole.setUpdatedBy(loggedUser);
        existingRole = this.roleRepository.save(existingRole);
        return true;
    }

    @Override
    public Page<RoleDto> getRoles(final Pageable pageable, final Specification specs) {
        log.info("RoleService.getRoles [start]");
        final Page<Role> pageRole = this.roleRepository.findAll(specs, pageable);
        final Page<RoleDto> result = (pageRole.isEmpty()) ? null : pageRole.map(mapper -> {
            return this.getFetchToDto(mapper);
        });
        log.info("RoleService.getRoles [end]");
        return result;
    }

    @Override
    public RoleDto getRoleById(Long id) {
        Role existingRole = this.roleRepository.getOne(id);
        if(existingRole == null){
            webUtility.doThrowResponseException(HttpStatus.NOT_FOUND, "Data is not found");
        }
        return this.getFetchToDto(existingRole);
    }

    @Override
    public List<RoleDto> getActiveRoles() {
        log.info("RoleService.getActiveRoles [start]");
        final List<RoleDto> result = this.roleRepository.getActiveRoles().stream().map(mapper -> {
            return this.getFetchToDto(mapper);
        }).collect(Collectors.toList());
        log.info("RoleService.getActiveRoles [end]");
        return (result.isEmpty()) ? null : result;
    }

    @Override
    public RoleDto getFetchToDto(final Role mapper) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(mapper.getId());
        roleDto.setName(mapper.getName());
        roleDto.setIsDeleted(mapper.getIsDeleted());
        roleDto.setStatus(UserConstants.ROLE_STATUS.valueOf(mapper.getStatus()).getMsg());
        roleDto.setDescription((mapper.getDescription()));
        List<PermissionDto> menus = mapper.getPermissions().stream().map(menuMapper -> {
            PermissionDto permissionDto = new PermissionDto();
            permissionDto.setId(menuMapper.getId());
            permissionDto.setIcon(menuMapper.getIcon());
            permissionDto.setPath(menuMapper.getPath());
            permissionDto.setTitle(menuMapper.getTitle());
            permissionDto.setApi(menuMapper.getApi());
            permissionDto.setName(menuMapper.getName());
            permissionDto.setLevel(menuMapper.getLevel());
            return permissionDto;
        }).collect(Collectors.toList());
        roleDto.setPermissions(menus);
        log.info("fetchToDto : {}", roleDto);
        return roleDto;
    }
    
}