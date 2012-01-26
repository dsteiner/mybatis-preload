package de.dst.mybatis.domain;

public class FieldTypeImpl extends AbstractIdentifiedObject implements
		FieldType {

	private static final long serialVersionUID = 1L;
	private String pattern;

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
