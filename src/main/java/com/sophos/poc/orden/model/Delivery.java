package com.sophos.poc.orden.model;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType("delivery")
public class Delivery {

	private DeliveryMethod deliveryMethod;

	public DeliveryMethod getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public Delivery(DeliveryMethod deliveryMethod) {
		super();
		this.deliveryMethod = deliveryMethod;
	}
	
	public Delivery() {}



	
}
