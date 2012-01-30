package de.dst.mybatis.service;

import java.util.List;

import de.dst.mybatis.domain.Account;
import de.dst.mybatis.domain.AccountField;
import de.dst.mybatis.domain.FieldType;
import de.dst.mybatis.domain.Signon;
import de.dst.mybatis.preload.Preload;

public interface AccountService {

	Account getAccount(String username, Preload... preloads);

	Signon getSignon(String id);

	Signon getSignonByUsername(String string);

	List<AccountField> getExtraFields(String refId);

	FieldType getFieldType(String typeId);

	List<FieldType> getFieldTypesByIdList(List<String> idList);
}
