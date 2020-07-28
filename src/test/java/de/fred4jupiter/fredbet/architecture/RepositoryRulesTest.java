package de.fred4jupiter.fredbet.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = FredbetConstants.BASE_PACKAGE)
public class RepositoryRulesTest {

    @ArchTest
    static final ArchRule only_repositories_may_use_the_EntityManager =
            noClasses().that().areAnnotatedWith(Service.class).or().areAnnotatedWith(Component.class)
                    .should().accessClassesThat().areAssignableTo(EntityManager.class)
                    .as("Only Repositories may use the " + EntityManager.class.getSimpleName());
}
