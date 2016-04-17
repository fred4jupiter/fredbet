package de.fred4jupiter.fredbet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PointsCalculationEventListener implements ApplicationListener<MatchFinishedEvent>{

	private static final Logger LOG = LoggerFactory.getLogger(PointsCalculationEventListener.class);
	
	@Autowired
	private PointsCalculationService pointsCalculationService;
	
	@Override
	public void onApplicationEvent(MatchFinishedEvent event) {
		LOG.debug("match={} has finished. Calculating points for bets...", event.getMatch());
		pointsCalculationService.calculatePointsFor(event.getMatch());
	}

}
