package com.sap.hana.cloud.samples.benefits.persistence.model.keys;

import java.io.Serializable;

public class UserPointsPrimaryKey implements Serializable{

	private static final long serialVersionUID = -5731646423339854523L;
	
	private long user;
	private long campaign;
	
	public UserPointsPrimaryKey() {
	}

	public UserPointsPrimaryKey(Long user, Long campaign) {
		this.user = user;
		this.campaign = campaign;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (campaign ^ (campaign >>> 32));
		result = prime * result + (int) (user ^ (user >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserPointsPrimaryKey other = (UserPointsPrimaryKey) obj;
		if (campaign != other.campaign)
			return false;
		if (user != other.user)
			return false;
		return true;
	}

}
