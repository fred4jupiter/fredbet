package de.fred4jupiter.fredbet.data;

public interface DatabasePopulator {
    void createRandomMatches();

    void createDemoBetsForAllUsers();

    void createDemoResultsForAllMatches();

    void createDemoUsers(int numberOfDemoUsers);

    void deleteAllBetsAndMatches();
}
