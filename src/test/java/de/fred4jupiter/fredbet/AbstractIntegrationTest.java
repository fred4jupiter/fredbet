package de.fred4jupiter.fredbet;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(value = { FredBetProfile.DEV, FredBetProfile.INTEGRATION_TEST })
public abstract class AbstractIntegrationTest {

}
