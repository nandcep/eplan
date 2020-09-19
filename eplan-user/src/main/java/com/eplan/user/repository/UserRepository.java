package com.eplan.user.repository;

import com.eplan.user.entity.User;
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
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    @Query(value="select count(id) from users where username=:username", nativeQuery = true)
    public Integer getUserCountByUsername(@Param("username") String username);

    @Query(value="select * from users u inner join user_role ur on u.id = ur.user_id where u.username=:username ", nativeQuery = true)
    public User getUserByUsername(@Param("username") String username);

    @Query(value="select * from users u inner join user_role ur on u.id = ur.user_id where u.username=:username and is_deleted=false and status = 0", nativeQuery = true)
    public User getUserByAuthentication(@Param("username") String username);
    
    @Query(value="select count(id) from users where email=:email", nativeQuery = true)
    public Integer getUserCountByEmail(@Param("email") String email);

    @Query(value="select * from users u inner join user_role ur on u.id = ur.user_id where u.email=:email ", nativeQuery = true)
    public User getUserByEmail(@Param("email") String email);

}