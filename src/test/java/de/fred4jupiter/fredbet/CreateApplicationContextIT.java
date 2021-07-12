package de.fred4jupiter.fredbet;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;


@IntegrationTest
public class CreateApplicationContextIT {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void createApplicationContext() {
        assertThat(applicationContext).isNotNull();
    }
}
