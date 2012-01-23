package de.dst.mybatis.domain;


@SuppressWarnings("serial")
public class AbstractIdentifiedObject implements IdentifiedObject {
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
