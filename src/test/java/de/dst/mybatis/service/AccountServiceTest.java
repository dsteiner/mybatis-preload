package de.dst.mybatis.service;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.dst.mybatis.domain.Account;
import de.dst.mybatis.domain.AccountField;
import de.dst.mybatis.domain.FieldType;
import de.dst.mybatis.domain.Ghost;
import de.dst.mybatis.domain.Signon;
import de.dst.mybatis.domain.SignonGhost;
import de.dst.mybatis.preload.Preload;
import de.dst.mybatis.preload.PreloadFindMethod;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class AccountServiceTest {
	@Autowired
	AccountService accountService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void loadFieldType() {
		FieldType ft = accountService
				.getFieldType("8791fd99-efca-347c-eeb9-52988a95c453");
		Assertions.assertNotNull(ft);
		Assertions.assertFalse(ft instanceof Ghost);
	}

	@Test
	public void loadAccount() {
		Account account = accountService.getAccount("j2ee");
		Assertions.assertNotNull(account);
		Signon signon = account.getSignon();
		Assertions.assertNotNull(signon);
		Assertions.assertTrue(signon instanceof SignonGhost);
		Assertions.assertNotNull(signon.getId());
		logger.debug("loaded Account: " + account.toString());
	}

	@Test
	public void loadSignonByUsername() {
		Signon signon = accountService.getSignonByUsername("j2ee");
		Assertions.assertNotNull(signon);
		Assertions.assertFalse(signon instanceof Ghost);
		logger.debug("loaded Account: " + signon.toString());
	}

	@Test
	public void loadExtraFields() {
		List<AccountField> accountFields = accountService
				.getExtraFields("bb1ad573-19b8-9cd8-68fb-0e6f684df992");
		Validate.notEmpty(accountFields);
		logger.debug("loaded Account-Fields: " + accountFields);
	}

	@Test
	public void preloadSignon() {

		PreloadFindMethod preloadFindMethod = new PreloadFindMethod(
				"accountService", "getSignon", String.class);
		Preload preload = new Preload(Account.class, "signon",
				preloadFindMethod);
		Account account = accountService.getAccount("j2ee", preload);
		Validate.notNull(account.getSignon());
		Assertions.assertFalse(account.getSignon() instanceof Ghost);
	}

	@Test
	public void preloadExtraFields() {

		PreloadFindMethod preloadFindMethod = new PreloadFindMethod(
				"accountService", "getExtraFields", String.class);
		Preload preload = new Preload(Account.class, "extraFields",
				preloadFindMethod);
		Account account = accountService.getAccount("j2ee", preload);
		List<AccountField> extraFields = account.getExtraFields();
		Validate.notEmpty(extraFields);

		AccountField af1 = extraFields.get(0);
		Assertions.assertTrue(af1.getType() instanceof Ghost);
	}

	@Test
	public void preloadExtraFieldsWithType() {
		PreloadFindMethod preloadFindMethodFieldType = new PreloadFindMethod(
				"accountService", "getFieldType", String.class);
		doTestWithPreloadFinderMethod(preloadFindMethodFieldType);
	}

	@Test
	public void preloadExtraFieldsWithTypeBulkLoadingFinder() {

		PreloadFindMethod preloadFindMethodFieldType = new PreloadFindMethod(
				"accountService", "getFieldTypesByIdList" /* , List.class */);
		doTestWithPreloadFinderMethod(preloadFindMethodFieldType);
	}

	private void doTestWithPreloadFinderMethod(
			PreloadFindMethod preloadFindMethodFieldType) {

		/* since method name is unique -> no argument class is needed */
		PreloadFindMethod preloadFindMethod = new PreloadFindMethod(
				"accountService", "getExtraFields" /* , String.class */);

		Preload preloadAccountType = new Preload(Account.class, "extraFields",
				preloadFindMethod);
		Preload preloadFieldType = new Preload(AccountField.class, "type",
				preloadFindMethodFieldType);
		Account account = accountService.getAccount("j2ee", preloadAccountType,
				preloadFieldType);
		List<AccountField> extraFields = account.getExtraFields();
		Validate.notEmpty(extraFields);
		AccountField af1 = extraFields.get(0);
		Assertions.assertFalse(af1.getType() instanceof Ghost);
	}
}
