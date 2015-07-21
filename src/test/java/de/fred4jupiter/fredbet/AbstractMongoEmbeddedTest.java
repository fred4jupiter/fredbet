package de.fred4jupiter.fredbet;

import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder;

@ActiveProfiles(value = {"dev", "mongo-embedded"})
public abstract class AbstractMongoEmbeddedTest extends AbstractIntegrationTest{

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
