# Copilot Instructions – Fredbet

Fredbet is a Spring Boot football betting web application.
- **Base package**: `de.fred4jupiter.fredbet`
- **Java version**: 25
- **Spring Boot version**: 4.0.4
- **Build tool**: Maven

---

## Package Structure

The project follows a **feature-based (domain-driven) package layout**:

```
de.fred4jupiter.fredbet
├── admin/              # Administration services
├── betting/            # Betting domain logic + repositories
│   └── repository/     # Repositories specific to betting
├── calendar/           # iCal export
├── config/             # Spring @Configuration classes
├── crests/             # Team crest images
├── data/               # Test data generation helpers
├── domain/             # Shared domain types
│   ├── entity/         # JPA entities
│   └── builder/        # Builder classes for entities
├── event/              # Login/session event handlers
├── excel/              # Excel import/export
├── image/              # Image handling
├── imexport/           # Data import/export
├── info/               # Info pages
├── integration/        # External API integration (football-data.org)
├── match/              # Match domain logic + repository
├── pdf/                # PDF export
├── pointcourse/        # Point-course logic
├── points/             # Points calculation
├── props/              # Configuration properties, constants, profiles
├── ranking/            # Ranking calculation
├── registration/       # Self-registration feature
├── security/           # Spring Security setup, permissions, user groups
├── settings/           # Runtime settings
├── standings/          # League standings
├── statistic/          # Statistics
├── team/               # Team management
├── teambundle/         # Team bundle management
├── user/               # User management
├── util/               # Shared utilities
└── web/                # All @Controller classes, Commands, Mappers
    └── <feature>/      # Web layer per feature (matches, bet, user, …)
```

---

## Naming Conventions

### Classes

| Type | Suffix / Rule | Annotation |
|---|---|---|
| Service | `*Service` | `@Service` |
| Repository (JPA) | `*Repository` | extends `JpaRepository` |
| Custom repo interface | `*RepositoryCustom` | – (package-private interface) |
| Custom repo implementation | `*RepositoryImpl` | – |
| Controller | `*Controller` | `@Controller` |
| Configuration | `*Config` | `@Configuration` |
| Component / Mapper | descriptive name | `@Component` |
| Command (web DTO) | `*Command` | – |
| Command Mapper | `*CommandMapper` | `@Component` |
| Domain Event | `*Event` (record) | – |
| Builder | `*Builder` | – |
| Exception | descriptive + `Exception` | – |
| Utility / Constants | `*Constants`, `*Names`, `*Profile` | `final` class, private constructor |
| Properties record | `*Properties` | `@ConfigurationProperties` |
| Permission interface | `FredBetPermission` | `interface` with `PERM_*` string constants |
| User group enum | `FredBetUserGroup` | – |

> **Interfaces must NOT have names containing the word "Interface"** (enforced by ArchUnit).

### Fields

- Constants: `UPPER_SNAKE_CASE` (`public static final` or `private static final`)
- Instance fields: `camelCase`
- Logger: always `private static final Logger LOG = LoggerFactory.getLogger(ClassName.class);`

### Database

- Table names: `UPPER_CASE` (e.g., `MATCHES`, `APP_USER`)
- Column names: `UPPER_CASE` with underscores (e.g., `MATCH_ID`, `KICK_OFF_DATE`)

---

## Architectural Rules (enforced by ArchUnit)

These rules are **hard constraints** verified by tests in `src/test/java/.../architecture/`:

1. **Controllers** must NOT directly access Repositories.
2. **Services and Components** must NOT access Controllers.
3. **Repositories** must NOT access Services.
4. Only **Repositories** may use `EntityManager`.
5. **No field injection** – always use **constructor injection**.
6. **No `System.out` / `System.err`** – use SLF4J.
7. **No generic exceptions** (`RuntimeException`, `Exception`) – throw specific exceptions.
8. **No JodaTime** – use `java.time`.
9. **No `java.util.logging`** – use SLF4J.
10. Logger fields must be `private static final`.
11. Interface names must NOT end with or contain `Interface`.
12. Classes whose simple name ends with `Config` must be annotated with `@Configuration`.

---

## Dependency Injection

- Always use **constructor injection** (never field injection with `@Autowired`).
- Dependencies are declared as `private final` fields and injected via the constructor.

```java
@Service
public class BettingService {

    private static final Logger LOG = LoggerFactory.getLogger(BettingService.class);

    private final BetRepository betRepository;
    private final MatchService matchService;

    public BettingService(BetRepository betRepository, MatchService matchService) {
        this.betRepository = betRepository;
        this.matchService = matchService;
    }
}
```

---

## Entity Conventions

- JPA entities live in `domain/entity/`.
- **No Lombok** – write explicit getters, setters, `equals`, `hashCode`, `toString`.
- Use `EqualsBuilder`, `HashCodeBuilder`, `ToStringBuilder` from **Apache Commons Lang 3**.
- Use `GenerationType.IDENTITY` for ID generation.
- Enum columns use `@Enumerated(EnumType.STRING)`.

```java
@Entity
@Table(name = "MATCHES")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATCH_ID")
    private Long id;

    @Override
    public boolean equals(Object obj) { /* EqualsBuilder */ }

    @Override
    public int hashCode() { /* HashCodeBuilder */ }

    @Override
    public String toString() { /* ToStringBuilder */ }
}
```

---

## Repository Conventions

- Interfaces extend `JpaRepository<Entity, Long>`.
- Custom queries use JPQL text blocks for multi-line readability.
- Custom repository logic: create a package-private interface `*RepositoryCustom` and implement it in `*RepositoryImpl`.
- Use `@Modifying` + `@Query` for update/delete operations.

```java
public interface BetRepository extends JpaRepository<Bet, Long>, BetRepositoryCustom {

    @Query("""
        select new de.fred4jupiter.fredbet.SomeResult(b.userName, sum(b.points))
        from Bet b
        group by b.userName
        """)
    List<SomeResult> queryPoints();

    @Modifying
    @Query("update Bet b set b.userName = :newUsername where b.userName = :oldUsername")
    void renameUser(@Param("oldUsername") String old, @Param("newUsername") String newName);
}
```

---

## Service Conventions

- Annotate with `@Service` and `@Transactional` at class level where appropriate.
- Services must NOT access Controllers or the web layer.

---

## Web Layer Conventions

- Controllers are annotated with `@Controller` and `@RequestMapping`.
- View names are stored as `private static final String VIEW_*` constants.
- Web DTOs are named `*Command`.
- Mapping between entities and commands is done in `*CommandMapper` components (`@Component`).
- Controllers delegate to Services; they do NOT call Repositories directly.
- Thymeleaf templates reside in `src/main/resources/templates/<feature>/`.

```java
@Controller
@RequestMapping("/matches")
public class MatchController {

    private static final String VIEW_LIST_MATCHES = "matches/list";

    private final MatchCommandMapper matchCommandMapper;

    public MatchController(MatchCommandMapper matchCommandMapper) {
        this.matchCommandMapper = matchCommandMapper;
    }

    @GetMapping
    public String listAllMatches(Model model) {
        model.addAttribute("allMatches", matchCommandMapper.findMatches(MatchService::findAllMatches));
        return VIEW_LIST_MATCHES;
    }
}
```

---

## Configuration Classes

- All `@Configuration` classes live in `config/` and are named `*Config`.
- Application-specific properties are defined as `record` annotated with `@ConfigurationProperties(prefix = "fredbet")`.

```java
@ConfigurationProperties(prefix = "fredbet")
public record FredbetProperties(String defaultLanguage, Integer thumbnailSize, ...) {}
```

---

## Constants and Utility Classes

- Utility / constants classes are `final` with a `private` constructor.
- Constants use `UPPER_SNAKE_CASE`.

```java
public final class FredbetConstants {
    private FredbetConstants() {}

    public static final String BASE_PACKAGE = "de.fred4jupiter.fredbet";
    public static final String DEFAULT_TIMEZONE = "Europe/Berlin";
}
```

---

## Security Conventions

- Permissions are `public static final String` constants prefixed with `PERM_` in the `FredBetPermission` interface.
- User groups are defined in `FredBetUserGroup`.
- URL-level security is configured in `SecurityConfig`.
- Method-level security uses `@PreAuthorize` with permission strings.
- Password encoding: BCrypt.

---

## Builder Pattern

- Builders live in `domain/builder/`.
- Builders are `final` classes with a **private constructor**.
- Instantiation via static factory method `create()`.
- Fluent setter methods are prefixed with `with`.
- Terminal method is `build()`.

```java
public final class BetBuilder {
    private BetBuilder() {}

    public static BetBuilder create() { return new BetBuilder(); }

    public BetBuilder withUserName(String userName) { ... return this; }
    public BetBuilder withMatch(Match match) { ... return this; }

    public Bet build() { ... }
}
```

---

## Domain Events

- Domain events are **records** and live in the feature package they belong to.
- Named `*Event` (e.g., `MatchGoalsChangedEvent`).

```java
public record MatchGoalsChangedEvent(Match match) {}
```

---

## Thymeleaf Templates

- Template files use **lowercase-dashed** or plain lowercase naming.
- Shared layout: `_layout.html` (underscore prefix indicates shared/partial files).
- Navigation: `_navigation.html`.
- Reusable fragments: `templates/fragments/`.
- Templates extend the layout via Thymeleaf Layout Dialect: `layout:decorate="~{_layout}"`.
- Content area: `layout:fragment="content"`.
- Spring Security integration via `xmlns:sec="http://www.thymeleaf.org/extras/spring-security"`.
- HTMX used for dynamic interactions (via `htmx-spring-boot`).

---

## Database Migrations

- Database schema is managed with **Liquibase**.
- Changelog entry point: `src/main/resources/db/changelog/liquibase-changelog.xml`.
- DDL auto is set to `validate`; schema changes must go through Liquibase.

---

## Internationalization (i18n)

Message files are in `src/main/resources/msgs/` and follow these naming patterns:

| File pattern | Purpose |
|---|---|
| `TranslationMessages_<lang>.properties` | General UI messages |
| `TeamKey_<lang>.properties` | Team name translations |
| `ValidationMessages_<lang>.properties` | Bean validation messages |

Supported languages: `de`, `en`, `es`, `fr`, `nl`, `pl`, `cs`, `ca`, `sv`.

---

## Testing Conventions

### File Naming

| Suffix | Type | Included in build |
|---|---|---|
| `*UT` | Unit test | ✅ |
| `*IT` | Integration test | ✅ |
| `*Test` | Architecture / other | ✅ |
| `*MT` | Manual test | ❌ excluded |

### Test Suites

- `UnitTestSuite` – runs all `*UT` tests.
- `IntegrationTestSuite` – runs all `*IT` tests.
- `UnitAndIntegrationTestSuite` – runs both.

### Integration Tests

- Use the meta-annotation `@TransactionalIntegrationTest`:
  - Loads full Spring context (`@SpringBootTest`)
  - Activates profile `integration_test`
  - Wraps each test in a transaction that is **rolled back** after the test.
- Dependencies are injected with `@Autowired` in integration tests.

```java
@TransactionalIntegrationTest
public class BettingServiceIT {

    @Autowired
    private BettingService bettingService;

    @Test
    public void shouldDoSomething() { ... }
}
```

### Unit Tests

- Use plain JUnit 5 + Mockito (no Spring context).
- Mockito is run as a Java agent (see `pom.xml`).
- Use AssertJ for assertions.

### Architecture Tests

- Use **ArchUnit** (`archunit-junit5`).
- Annotate with `@AnalyzeClasses(packages = FredbetConstants.BASE_PACKAGE)`.
- Rules are defined as `@ArchTest` fields or methods.

---

## Spring Profiles

Profiles are defined as constants in `FredBetProfile`:

| Constant | Profile name | Purpose |
|---|---|---|
| `FredBetProfile.DEV` | `dev` | Local development with H2 |
| `FredBetProfile.INTEGRATION_TEST` | `integration_test` | Integration tests |
| `FredBetProfile.H2` | `h2` | Embedded H2 database |

Profile-specific config files: `application-dev.yml`, `application-postgres.yml`, `application-mysql.yml`, `application-maria.yml`.

---

## Caching

- Cache names are `public static final String` constants in `CacheNames`.
- All cache names must be registered in `CacheConfig`.

---

## Frontend Libraries (via WebJars)

| Library | Purpose |
|---|---|
| Bootstrap 5 | Layout and components |
| Bootswatch | Switchable Bootstrap themes |
| Bootstrap Icons | Icon set |
| Font Awesome 5 | Additional icons |
| jQuery | DOM manipulation |
| HTMX (htmx-spring-boot 5) | Server-driven dynamic UI |

---

## Logging

- Use **SLF4J** (`org.slf4j.Logger` / `LoggerFactory`).
- Logger declaration must be: `private static final Logger LOG = LoggerFactory.getLogger(ClassName.class);`
- Never use `System.out`, `System.err`, or `java.util.logging`.

