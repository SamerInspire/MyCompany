package com.pwc.mc.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DEPARTMENT_TB")
public class DepartmentVO {

	@Id
	@GeneratedValue
	@Column(name = "department_id")
	private Long departmentId;
	@Column(name = "department_name")
	private String departmentName;
	@Embedded
	private List<UserVO> employers;
	@Embedded
	private List<ProjectVO> projects;

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public List<UserVO> getEmployers() {
		return employers;
	}

	public void setEmployers(List<UserVO> employers) {
		this.employers = employers;
	}

	public List<ProjectVO> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectVO> projects) {
		this.projects = projects;
	}

	@Override
	public String toString() {
		return "DepartmentVO [departmentId=" + departmentId + ", departmentName=" + departmentName + ", employers="
				+ employers + ", projects=" + projects + "]";
	}
}
