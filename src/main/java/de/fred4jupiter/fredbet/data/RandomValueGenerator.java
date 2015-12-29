package de.fred4jupiter.fredbet.data;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RandomValueGenerator {

	public Integer generateRandomValue() {
		return generateRandomValueInRange(1, 10);
	}
	
	public Integer generateRandomValueInRange(int min, int max) {
		Random rn = new Random();
		int range = max - min + 1;
		return rn.nextInt(range) + min;
	}
}
