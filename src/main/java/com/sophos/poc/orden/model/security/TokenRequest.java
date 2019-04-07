package com.sophos.poc.orden.model.security;

import java.io.Serializable;

public class TokenRequest implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private RequestHeader requestHeader;
	private RequestPayload requestPayload;
	
	public RequestHeader getRequestHeader() {
		return requestHeader;
	}
	public void setRequestHeader(RequestHeader requestHeader) {
		this.requestHeader = requestHeader;
	}
	public RequestPayload getRequestPayload() {
		return requestPayload;
	}
	public void setRequestPayload(RequestPayload requestPayload) {
		this.requestPayload = requestPayload;
	}
	public TokenRequest(RequestHeader requestHeader, RequestPayload requestPayload) {
		super();
		this.requestHeader = requestHeader;
		this.requestPayload = requestPayload;
	}
	public TokenRequest() {}
	
	

}
