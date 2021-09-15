package com.pwc.mc.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROJECT_TB")
public class ProjectVO {

	@Id
    @GeneratedValue
	@Column(name = "project_id",nullable = false)
	private Long projectId;
	@Column(name = "project_name",nullable = false)
	private String projectName;
	@Embedded
	private List<UserVO> assignedEmployers = new ArrayList<UserVO>();

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<UserVO> getAssignedEmployers() {
		return assignedEmployers;
	}

	public void setAssignedEmployers(List<UserVO> assignedEmployers) {
		this.assignedEmployers = assignedEmployers;
	}

	@Override
	public String toString() {
		return "ProjectVO [projectId=" + projectId + ", projectName=" + projectName + ", assignedEmployers="
				+ assignedEmployers.toString() + "]";
	}

}
