package de.fred4jupiter.fredbet.data;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncDatabasePopulator {

    private final DatabasePopulator databasePopulator;

    public AsyncDatabasePopulator(DatabasePopulator databasePopulator) {
        this.databasePopulator = databasePopulator;
    }

    @Async
    public void createRandomMatches() {
        databasePopulator.createRandomMatches();
    }

    @Async
    public void createDemoBetsForAllUsers() {
        databasePopulator.createDemoBetsForAllUsers();
    }

    @Async
    public void createDemoUsers(int numberOfDemoUsers) {
        databasePopulator.createDemoUsers(numberOfDemoUsers);
    }

    @Async
    public void createDemoResultsForAllMatches() {
        databasePopulator.createDemoResultsForAllMatches();
    }

    @Async
    public void createTestDataForAll() {
        databasePopulator.createRandomMatches();
        databasePopulator.createDemoBetsForAllUsers();
        databasePopulator.createDemoResultsForAllMatches();
    }
}
