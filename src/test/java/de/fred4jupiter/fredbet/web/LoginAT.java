package de.fred4jupiter.fredbet.web;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.fred4jupiter.fredbet.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class LoginAT extends FluentTest {

	private static final Logger LOG = LoggerFactory.getLogger(LoginAT.class);

	@LocalServerPort
	private int serverPort;

	@Autowired
	private MessageUtil messageUtil;

	private String getURL() {
		return "http://localhost:" + serverPort;
	}

	@Override
	public WebDriver getDefaultDriver() {
		return new HtmlUnitDriver();
	}

	@Test
	public void login() {
		LOG.debug("try to call URL={}", getURL());

		goTo(getURL());

		fill("#username").with("admin");
		fill("#password").with("admin");

		submit("#loginSubmitBtn");
		assertThat(title(), equalTo(messageUtil.getMessageFor("all.matches")));
	}
}
