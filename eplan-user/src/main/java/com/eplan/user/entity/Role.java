package com.eplan.user.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Data
@Entity
@Table (name = "roles", indexes = {
    @Index(name = "ROLES_INDX_0", columnList = "name"),
    @Index(name = "ROLES_INDX_1", columnList = "description"),
    @Index(name = "ROLES_INDX_2", columnList = "status")
})
public class Role extends BaseEntity implements Serializable  {

    private static final long serialVersionUID = -7956613494382722612L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name="description", length = 255)
    private String description;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "status")
    private Integer status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="role_permission",joinColumns= {@JoinColumn(name="role_id")},inverseJoinColumns= {@JoinColumn(name="permission_id")})
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Permission> permissions = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
        joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    private Set<User> users;
    
}