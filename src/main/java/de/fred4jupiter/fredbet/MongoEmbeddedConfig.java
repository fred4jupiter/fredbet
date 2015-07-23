package de.fred4jupiter.fredbet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;


@Configuration
@EnableMongoRepositories
@Profile(value = {"mongo-embedded", "default"})
public class MongoEmbeddedConfig extends AbstractMongoConfiguration{

	private static final String MONGO_DB_NAME = "fredbet_db";

	@Override
	protected String getDatabaseName() {
		return MONGO_DB_NAME;
	}

	@Bean
	@Override
	public Mongo mongo()  {
        return new Fongo("mongo-embedded").getMongo();
	}
	
	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongo(), MONGO_DB_NAME);
	}

}
