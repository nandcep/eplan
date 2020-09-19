package com.eplan.user.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Data
@Entity
@Table(name = "users", indexes = {
    @Index(name = "USERS_INDX_0", columnList = "username"),
    @Index(name = "USERS_INDX_1", columnList = "email"),
    @Index(name = "USERS_INDX_2", columnList = "fullname"),
    @Index(name = "USERS_INDX_3", columnList = "status"),
    @Index(name = "USERS_INDX_4", columnList = "password")
})
public class User extends BaseEntity implements Serializable  {

    private static final long serialVersionUID = 4785379654016097856L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name="username", length = 30)
    private String username;

    @Column(name="fullname", length = 255)
    private String fullname;

    @Column(name="email", length = 255)
    private String email;

    @Column(name="phone_no", length = 16)
    private String phoneNo;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private Integer status;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "wrong_password")
    private Integer wrongPassword;

    @Column(name = "updated_password_date")
    private Date updatedPasswordDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable (name = "user_role", joinColumns = {
        @JoinColumn (name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
        @JoinColumn(name = "role_id", referencedColumnName = "id") })
    @JsonIgnoreProperties ({ "hibernateLazyInitializer", "handler" })
    @JsonBackReference
    private Role role;

}