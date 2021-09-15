package com.pwc.mc.model;

import java.util.List;

public class WebResponseBody {

	private UserVO user;
	private List<UserVO> users;
	private List<DepartmentVO> departments;
	private List<ProjectVO> projects;
	private String result;
	private String resultDescription;

	public UserVO getUser() {
		return user;
	}

	public void setUser(UserVO user) {
		this.user = user;
	}

	public List<UserVO> getUsers() {
		return users;
	}

	public void setUsers(List<UserVO> users) {
		this.users = users;
	}

	public List<DepartmentVO> getDepartments() {
		return departments;
	}

	public void setDepartments(List<DepartmentVO> departments) {
		this.departments = departments;
	}

	public List<ProjectVO> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectVO> projects) {
		this.projects = projects;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

	@Override
	public String toString() {
		return "WebResponseBody [user=" + user + ", result=" + result + ", resultDescription=" + resultDescription
				+ "]";
	}

}
