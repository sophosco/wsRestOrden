package com.sophos.poc.orden.model;

public class OrdersResponse {
	
	private String approvalCode;

	public String getApprovalCode() {
		return approvalCode;
	}

	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
	}

	public OrdersResponse(String approvalCode) {
		super();
		this.approvalCode = approvalCode;
	}
	
	
}
