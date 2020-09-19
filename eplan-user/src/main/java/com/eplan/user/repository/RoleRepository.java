package com.eplan.user.repository;

import java.util.List;

import com.eplan.user.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    
    @Query(nativeQuery = true, value = "select * from roles where is_deleted=false and status = 0")
    public List<Role> getActiveRoles();

}