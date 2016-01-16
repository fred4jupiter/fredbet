package de.fred4jupiter.fredbet;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(value = { FredBetProfile.DEV, FredBetProfile.FONGO, FredBetProfile.INTEGRATION_TEST })
public abstract class AbstractMongoEmbeddedTest {

	@Rule
	public MongoDbRule mongoDbRule = MongoDbRuleBuilder.newMongoDbRule().defaultSpringMongoDb("demo-test");

	/**
	 *
	 * nosql-unit requirement
	 *
	 */
	@SuppressWarnings("unused")
	@Autowired
	private ApplicationContext applicationContext;
}
