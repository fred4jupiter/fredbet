package de.fred4jupiter.fredbet.data;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class FakeDataPopulator {

    private final Faker faker = new Faker(Locale.getDefault());

    public Boolean nextRandomBoolean() {
        return this.faker.random().nextBoolean();
    }

    public String nextRandomUsername() {
        return this.faker.name().username();
    }
}
