package de.fred4jupiter.fredbet.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = FredbetConstants.BASE_PACKAGE, importOptions = {ImportOption.DoNotIncludeTests.class})
public class LayerDependencyRulesTest {

    @ArchTest
    private void controllers_should_not_access_repositories(JavaClasses classes) {
        noClasses().that().areAnnotatedWith(Controller.class)
                .should().accessClassesThat().haveSimpleNameContaining("Repository").check(classes);
    }

    @ArchTest
    private void services_or_compoments_should_not_access_controllers(JavaClasses classes) {
        noClasses().that().areAnnotatedWith(Service.class).or().areAnnotatedWith(Component.class)
                .should().accessClassesThat().areAnnotatedWith(Controller.class).check(classes);
    }

    @ArchTest
    private void persistence_should_not_access_services(JavaClasses classes) {
        noClasses().that().haveSimpleNameContaining("Repository")
                .should().accessClassesThat().areAnnotatedWith(Service.class).check(classes);
    }
}
