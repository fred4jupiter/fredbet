package de.fred4jupiter.fredbet.data;

import java.util.function.Consumer;

public interface DatabasePopulator {

    void executeAsync(Consumer<DatabasePopulator> populatorCallback);

    void createDemoData(DemoDataCreation demoDataCreation);

    void createDemoUsers(int numberOfDemoUsers);

    void deleteAllBetsAndMatches();
}
