package de.fred4jupiter.fredbet.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = FredbetConstants.BASE_PACKAGE)
public class NamingConventionTest {

    @ArchTest
    static ArchRule services_should_be_postfixed =
            classes().that().areAnnotatedWith(Service.class).should().haveSimpleNameEndingWith("Service");

    @ArchTest
    static ArchRule repositories_should_be_named_with_repository =
            classes().that().areAssignableTo(JpaRepository.class).should().haveSimpleNameEndingWith("Repository");

    @ArchTest
    static ArchRule controllers_should_be_postfixed =
            classes().that().areAnnotatedWith(Controller.class).should().haveSimpleNameEndingWith("Controller");

}
