package de.dst.mybatis.domain;

import java.io.Serializable;

public interface IdentifiedObject extends Serializable {

	public String getId();

	public void setId(String id);
}
