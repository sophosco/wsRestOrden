package com.sophos.poc.orden.model.security;

public class ResponsePayload {
	
	private boolean verify;
	
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
