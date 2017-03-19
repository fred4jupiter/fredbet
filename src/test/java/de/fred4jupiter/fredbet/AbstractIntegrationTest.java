package de.fred4jupiter.fredbet;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.fred4jupiter.fredbet.props.FredBetProfile;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles(value = { FredBetProfile.DEV, FredBetProfile.INTEGRATION_TEST })
public abstract class AbstractIntegrationTest {

}
