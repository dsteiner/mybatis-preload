package de.dst.mybatis.domain;

import java.util.List;

public class Account extends AbstractIdentifiedObject {

	private static final long serialVersionUID = 1L;

	private String id;

	private String firstName;
	private String lastName;

	private Signon signon;

	private List<AccountField> extraFields;

	public void setExtraFields(List<AccountField> extraFields) {
		this.extraFields = extraFields;
	}

	public Signon getSignon() {
		return signon;
	}

	public void setSignon(Signon signon) {
		this.signon = signon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "\nACCOUNT:\nid=" + getId() + " lastname: " + getLastName()
				+ " extraFields: " + getExtraFields() + "\n";
	}

	public List<AccountField> getExtraFields() {
		return extraFields;
	}
}
