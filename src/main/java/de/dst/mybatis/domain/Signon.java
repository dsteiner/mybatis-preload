package de.dst.mybatis.domain;

public interface Signon extends IdentifiedObject {
	public String getUsername();

	public void setUsername(String username);

	public String getPassword();

	public void setPassword(String password);
}
