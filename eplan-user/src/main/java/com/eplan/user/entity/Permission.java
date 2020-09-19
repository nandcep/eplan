package com.eplan.user.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Data
@Entity
@Table (name = "permissions", indexes = {
	@Index(name = "PERMISSIONS_INDX_0", columnList = "name"),
	@Index(name = "PERMISSIONS_INDX_1", columnList = "api"),
	@Index(name = "PERMISSIONS_INDX_2", columnList = "parent_id"),
	@Index(name = "PERMISSIONS_INDX_3", columnList = "title"),
	@Index(name = "PERMISSIONS_INDX_4", columnList = "path"),
	@Index(name = "PERMISSIONS_INDX_5", columnList = "type")
})
public class Permission extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7760914673829150700L;

    @Id
	private Long id;

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "level")
	private Long level;

	@Column(name = "parent_id")
	private Long parentId;

	@Column(name = "title")
	private String title;

	@Column(name = "icon")
	private String icon;

	@Column(name = "api")
	private String api;

	@Column(name = "path")
	private String path;

	@Column(name = "type")
	private Integer type;

}