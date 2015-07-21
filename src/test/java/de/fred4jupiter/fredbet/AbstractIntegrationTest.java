package de.fred4jupiter.fredbet;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
// @WebAppConfiguration
@ActiveProfiles(value = {"dev"})
public abstract class AbstractIntegrationTest {

}
