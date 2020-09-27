package de.fred4jupiter.fredbet.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.springframework.context.annotation.Configuration;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = FredbetConstants.BASE_PACKAGE)
public class ConfigurationRulesTest {

    @ArchTest
    private void configClassesShouldBeAnnotatedWithConfigurationAnnotation(JavaClasses classes) {
        classes().that().haveSimpleNameEndingWith("Config")
                .should().beAnnotatedWith(Configuration.class).check(classes);
    }
}
