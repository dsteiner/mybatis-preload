package de.dst.mybatis.domain;

public class SignonGhost extends AbstractIdentifiedObject implements Signon {

	private void throwGhostException() {
		throw new GhostException(
				"this Signon instance is a ghost - missed preloading?");
	}

	public String getUsername() {
		throwGhostException();
		return null;
	}

	public void setUsername(String username) {
		throwGhostException();

	}

	public String getPassword() {
		throwGhostException();
		return null;
	}

	public void setPassword(String password) {
		throwGhostException();

	}

}
