package de.dst.mybatis.domain;

public class FieldTypeGhost extends AbstractIdentifiedObject implements
		FieldType, Ghost {

	private static final long serialVersionUID = 1L;

	public String getPattern() {
		throw new IllegalStateException(
				"This instance is a Ghost -> forgot preloading?");
	}

	public void setPattern(String pattern) {
		throw new IllegalStateException(
				"This instance is a Ghost -> forgot preloading?");
	}

}
