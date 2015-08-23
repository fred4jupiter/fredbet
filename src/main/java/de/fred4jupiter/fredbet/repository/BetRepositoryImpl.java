package de.fred4jupiter.fredbet.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

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
		Aggregation aggregation = newAggregation(group("userName").sum("points").as("total"),
				project("total").and("userName").previousOperation(), sort(Sort.Direction.DESC, "total"));

		// Convert the aggregation result into a List
		AggregationResults<UsernamePoints> aggregationResults = mongoTemplate.aggregate(aggregation, Bet.class, UsernamePoints.class);
		return aggregationResults.getMappedResults();
	}
}
