package com.sophos.poc.orden.model.security;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestPayload  implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty( value="Id")
	private String Id;
	
	private Object payload;

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public RequestPayload() {}
	
	public RequestPayload(String id, Object payload) {
		super();
		Id = id;
		this.payload = payload;
	}

}
