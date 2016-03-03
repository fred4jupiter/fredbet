package de.fred4jupiter.fredbet;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class CreateApplicationContextTest extends AbstractIntegrationTest{

	@Autowired
	private ApplicationContext applicationContext;
	
	@Test
	public void createApplicationContext() {
		assertNotNull(applicationContext);
	}
}
