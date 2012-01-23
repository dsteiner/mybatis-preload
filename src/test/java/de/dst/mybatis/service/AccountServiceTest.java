package de.dst.mybatis.service;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.dst.mybatis.domain.Account;
import de.dst.mybatis.domain.AccountField;
import de.dst.mybatis.domain.Signon;
import de.dst.mybatis.domain.SignonGhost;
import de.dst.mybatis.preload.Preload;
import de.dst.mybatis.preload.PreloadFindMethod;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class AccountServiceTest {
	@Autowired
	AccountService accountService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void loadAccount() {
		Account account = accountService.getAccount("j2ee");
		Assert.assertNotNull(account);
		Signon signon = account.getSignon();
		Assert.assertNotNull(signon);
		Assert.assertTrue(signon instanceof SignonGhost);
		Assert.assertNotNull(signon.getId());
		logger.debug("loaded Account: " + account.toString());
	}

	@Test
	public void loadSignonByUsername() {
		Signon signon = accountService.getSignonByUsername("j2ee");
		Assert.assertNotNull(signon);
		Assert.assertFalse(signon instanceof SignonGhost);
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
				"accountService", "getSignon", String.class, "id");
		Preload preload = new Preload(Account.class, "signon",
				preloadFindMethod);
		Account account = accountService.getAccount("j2ee", preload);
		Validate.notNull(account.getSignon());
		Assert.assertFalse(account.getSignon() instanceof SignonGhost);
	}
}
