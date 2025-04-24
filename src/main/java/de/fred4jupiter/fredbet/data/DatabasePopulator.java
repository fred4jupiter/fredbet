package de.fred4jupiter.fredbet.data;

import java.util.function.Consumer;

public interface DatabasePopulator {

    void executeAsync(Consumer<DatabasePopulator> populatorCallback);

    void createDemoData();

    void createDemoData(DemoDataCreation demoDataCreation);

    void createDemoBetsForAllUsers();

    void createDemoResultsForAllMatches();

    void createDemoUsers(int numberOfDemoUsers);

    void deleteAllBetsAndMatches();
}
