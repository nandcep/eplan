package com.eplan.user.service;

import java.util.List;
import com.eplan.user.dto.RoleDto;
import com.eplan.user.entity.Role;
import com.eplan.user.request.RoleRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
public interface RoleService {

    public Boolean create(RoleRequest request, Long loggedUser);
    public Boolean update(RoleRequest request, Long loggedUser);
    public Boolean delete(Long id, Long loggedUser);
    public Page<RoleDto> getRoles(Pageable pageable, Specification specs);
    public RoleDto getRoleById(Long id);
    public List<RoleDto> getActiveRoles();
    public RoleDto getFetchToDto(Role mapper);
    
}