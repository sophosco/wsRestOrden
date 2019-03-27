package com.sophos.poc.model.shoppingcart;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import com.fasterxml.jackson.annotation.JsonProperty;

@UserDefinedType("images")
public class GaleriaProductoDTO {

	@JsonProperty("small")
	private String small = null;

	@JsonProperty("medium")
	private String medium = null;

	@JsonProperty("big")
	private String big = null;

	public String getSmall() {
		return small;
	}

	public void setSmall(String small) {
		this.small = small;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public String getBig() {
		return big;
	}

	public void setBig(String big) {
		this.big = big;
	}

	public GaleriaProductoDTO(String small, String medium, String big) {
		super();
		this.small = small;
		this.medium = medium;
		this.big = big;
	}

	

}
