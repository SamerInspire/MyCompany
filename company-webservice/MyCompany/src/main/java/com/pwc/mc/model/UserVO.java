package com.pwc.mc.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USER_TB")
public class UserVO {
	@Id
	@GeneratedValue
	@Column(name = "user_id", nullable = false)
	private long userId;
	// @Email(message = "Email should be valid")
	// @NotEmpty(message = "Email cannot be null")
	private String departmentId;
	private String email;
	private String password;
	private String name;
	@Column(name = "phone_no")
	private String phoneNo;
	private String gender;
	private boolean isManager; // this will be setting after sign-up\log-in
	private String address;

	private transient List<AuthoritiesVO> authorities = new ArrayList<AuthoritiesVO>();

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public void setIsManager(boolean isManager) {
		this.isManager = isManager;
	}

	public boolean getIsManager() {
		return isManager;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public void setAdmin(boolean isAdmin) {
		this.isManager = isAdmin;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<AuthoritiesVO> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<AuthoritiesVO> authorities) {
		this.authorities = authorities;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "UserVO [userId=" + userId + ", departmentId " + departmentId + ", email=" + email + ", password="
				+ password + ", name=" + name + ", phoneNo=" + phoneNo + ", gender=" + gender + ", isAdmin=" + isManager
				+ ", address=" + address + "]";
	}

}