package com.pwc.mc.model;

import org.springframework.security.core.GrantedAuthority;

public class AuthoritiesVO implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	private String authority;

	public AuthoritiesVO(String authority) {
		super();
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		// TODO Auto-generated method stub
		return authority;
	}

	@Override
	public String toString() {
		return "Authorities [authority=" + authority + "]";
	}
}
