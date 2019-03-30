package com.sophos.poc.orden.model.security;

import java.util.Date;

public class ResponseInfo {
	
	private String system;
	private Date responseDate;
	
	public ResponseInfo(String system,Date responseDate ) {
		this.system = system;
		this.responseDate = responseDate;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public Date getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(Date responseDate) {
		this.responseDate = responseDate;
	}
	

}
