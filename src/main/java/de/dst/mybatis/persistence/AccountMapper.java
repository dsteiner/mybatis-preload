package de.dst.mybatis.persistence;

import java.util.List;

import de.dst.mybatis.domain.Account;
import de.dst.mybatis.domain.AccountField;
import de.dst.mybatis.domain.FieldType;
import de.dst.mybatis.domain.Signon;

public interface AccountMapper {

	Account getAccountByUsername(String username);

	Signon getSignonByUsername(String username);

	List<AccountField> getAccountFieldsByAccountId(String refId);

	Signon getSignonByById(String signonId);

	FieldType getFieldType(String typeId);

	List<FieldType> getFieldTypesByIds(List<String> idList);
}
