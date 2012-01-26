package de.dst.mybatis.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.dst.mybatis.domain.Account;
import de.dst.mybatis.domain.AccountField;
import de.dst.mybatis.domain.FieldType;
import de.dst.mybatis.domain.Signon;
import de.dst.mybatis.persistence.AccountMapper;
import de.dst.mybatis.preload.Preload;
import de.dst.mybatis.preload.PreloadHelper;
import de.dst.mybatis.service.AccountService;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountMapper accountMapper;
	private Account result;

	@Autowired
	private PreloadHelper preloadHelper;

	public Account getAccount(String username, Preload... preloads) {
		result = accountMapper.getAccountByUsername(username);
		preloadHelper.preload(result, preloads);
		return result;
	}

	public Signon getSignon(String signonId) {
		return accountMapper.getSignonByById(signonId);
	}

	public List<AccountField> getExtraFields(String refId) {
		return accountMapper.getAccountFieldsByAccountId(refId);
	}

	public Signon getSignonByUsername(String userName) {
		return accountMapper.getSignonByUsername(userName);
	}

	public FieldType getFieldType(String typeId) {
		return accountMapper.getFieldType(typeId);
	}

}
