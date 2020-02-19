package de.fred4jupiter.fredbet;

import de.fred4jupiter.fredbet.props.FredBetProfile;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles(value = {FredBetProfile.DEV, FredBetProfile.INTEGRATION_TEST})
public abstract class AbstractIntegrationTest {

}
