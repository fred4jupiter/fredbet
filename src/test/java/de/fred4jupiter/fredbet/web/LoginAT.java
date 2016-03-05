package de.fred4jupiter.fredbet.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.fred4jupiter.fredbet.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest("server.port:0")
@ActiveProfiles("dev")
public class LoginAT {

	private static final Logger LOG = LoggerFactory.getLogger(LoginAT.class);

	@Value("${local.server.port}")
	private int serverPort;

	private WebDriver driver = new HtmlUnitDriver();

	private String getURL() {
		return "http://localhost:" + serverPort;
	}

	@Test
	public void login() {
		LOG.debug("try to call URL=" + getURL());

		driver.get(getURL());

		WebElement header = driver.findElement(By.tagName("h1"));
		assertEquals("Login", header.getText());

		WebElement userNameElement = driver.findElement(By.id("username"));
		assertNotNull(userNameElement);
		userNameElement.sendKeys("admin");

		WebElement passwordElement = driver.findElement(By.id("password"));
		passwordElement.sendKeys("admin");

		passwordElement.submit();
		assertEquals("Spiele", driver.getTitle());
	}
}
