package com.pwc.mc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROJECTS_USERS_TB")
public class ProjectsUsersVO {
	@Id
	@GeneratedValue
	@Column(name = "ID", nullable = false)
	private long id;
	private long userId;
	private long projectId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	@Override
	public String toString() {
		return "UsersProjectsVO [id=" + id + ", userId=" + userId + ", projectId=" + projectId + "]";
	}

}
