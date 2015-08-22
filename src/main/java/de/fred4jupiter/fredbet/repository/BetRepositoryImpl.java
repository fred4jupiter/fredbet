package de.fred4jupiter.fredbet.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.previousOperation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import de.fred4jupiter.fredbet.domain.Bet;

class BetRepositoryImpl implements BetRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	public List<UsernamePoints> calculateRanging() {
		Aggregation aggregation = newAggregation(group("userName", "points").sum("points").as("totalPoints"),
				sort(Sort.Direction.DESC, previousOperation(), "totalPoints"), project("userName").and("totalPoints").as("userName"));

		// Convert the aggregation result into a List
		AggregationResults<UsernamePoints> aggregationResults = mongoTemplate.aggregate(aggregation, Bet.class, UsernamePoints.class);
		List<UsernamePoints> mappedResults = aggregationResults.getMappedResults();

		return mappedResults;
	}
}
