package de.dst.mybatis.domain;

public class AccountField extends AbstractIdentifiedObject {
	private static final long serialVersionUID = 1L;
	private FieldType type;
	private String text;

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "AccountField: id=" + getId() + " type=" + type + " text="
				+ text;
	}

}
