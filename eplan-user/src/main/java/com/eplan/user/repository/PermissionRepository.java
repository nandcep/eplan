package com.eplan.user.repository;

import java.util.List;
import com.eplan.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    
    @Query(value="select p.* from permissions p inner join role_permission rp on p.id = rp.permission_id where rp.role_id = :roleId and (p.parent_id is null or p.type = 0) and (p.level = 0 or p.type=0)", nativeQuery = true)
    public List<Permission> getPermissionListByRoleId(@Param("roleId") Long roleId);

    @Query(value="select p.* from permissions p where parent_id is null and level = 0", nativeQuery = true)
    public List<Permission> getActiveMenus();

    @Query(value="select p.* from permissions p where p.parent_id = :parentId", nativeQuery = true)
    public List<Permission> getPermissionByParentId(@Param("parentId") Long parentId);

}