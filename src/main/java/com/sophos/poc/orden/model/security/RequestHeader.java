package com.sophos.poc.orden.model.security;

import java.io.Serializable;

public class RequestHeader  implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public RequestHeader() {}
	
	public RequestHeader(String token) {
		super();
		this.token = token;
	}

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
