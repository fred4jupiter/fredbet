package de.fred4jupiter.fredbet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;


@Configuration
@EnableMongoRepositories
@Profile("mongo-embedded")
public class MongoEmbeddedConfig extends AbstractMongoConfiguration{

	@Override
	protected String getDatabaseName() {
		return "demo-test";
	}

	@Bean
	@Override
	public Mongo mongo() throws Exception {
		// uses fongo for in-memory tests
        return new Fongo("mongo-test").getMongo();
	}
	
	

}
