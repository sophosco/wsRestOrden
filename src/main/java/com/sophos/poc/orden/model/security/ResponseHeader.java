package com.sophos.poc.orden.model.security;

import java.io.Serializable;

public class ResponseHeader implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private ResponseHeader responseInfo;
	private Status status;
	private ResponsePayload responsePayload;
	
	public ResponseHeader() {}

	public ResponseHeader getResponseInfo() {
		return responseInfo;
	}

	public void setResponseInfo(ResponseHeader responseInfo) {
		this.responseInfo = responseInfo;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ResponsePayload getResponsePayload() {
		return responsePayload;
	}

	public void setResponsePayload(ResponsePayload responsePayload) {
		this.responsePayload = responsePayload;
	}

	public ResponseHeader(ResponseHeader responseInfo, Status status, ResponsePayload responsePayload) {
		super();
		this.responseInfo = responseInfo;
		this.status = status;
		this.responsePayload = responsePayload;
	}
	
	

}

