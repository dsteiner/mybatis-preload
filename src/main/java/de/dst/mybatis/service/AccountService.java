package de.dst.mybatis.service;

import java.util.List;

import de.dst.mybatis.domain.Account;
import de.dst.mybatis.domain.AccountField;
import de.dst.mybatis.domain.Signon;
import de.dst.mybatis.preload.Preload;

public interface AccountService {

	public Account getAccount(String username, Preload... preloads);

	public Signon getSignon(String id);

	public Signon getSignonByUsername(String string);

	public List<AccountField> getExtraFields(String refId);
}
