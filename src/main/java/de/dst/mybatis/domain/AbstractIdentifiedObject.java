package de.dst.mybatis.domain;

import java.io.Serializable;


@SuppressWarnings("serial")
public abstract class AbstractIdentifiedObject implements IdentifiedObject,
		Serializable {
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
