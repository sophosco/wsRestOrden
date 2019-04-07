package com.sophos.poc.orden.model.security;

import java.io.Serializable;

public class ResponsePayload implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private boolean verify;
	
	public ResponsePayload() {}
	
	public ResponsePayload(boolean verify ) {
		this.verify = verify;
	}

	public boolean isVerify() {
		return verify;
	}

	public void setVerify(boolean verify) {
		this.verify = verify;
	}
	
	

}
