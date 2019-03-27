package com.sophos.poc.orden.model;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType("country")
public class Country {
	
	private String name;
	private String code;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Country(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}
	
	

}
