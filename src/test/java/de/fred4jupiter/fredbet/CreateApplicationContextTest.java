package de.fred4jupiter.fredbet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@IntegrationTest
public class CreateApplicationContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void createApplicationContext() {
        assertNotNull(applicationContext);
    }
}
