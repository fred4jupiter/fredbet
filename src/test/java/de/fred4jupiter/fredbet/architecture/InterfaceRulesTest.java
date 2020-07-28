package de.fred4jupiter.fredbet.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import de.fred4jupiter.fredbet.props.FredbetConstants;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = FredbetConstants.BASE_PACKAGE)
public class InterfaceRulesTest {

    @ArchTest
    static final ArchRule interfaces_should_not_have_names_ending_with_the_word_interface =
            noClasses().that().areInterfaces().should().haveNameMatching(".*Interface");

    @ArchTest
    static final ArchRule interfaces_should_not_have_simple_class_names_containing_the_word_interface =
            noClasses().that().areInterfaces().should().haveSimpleNameContaining("Interface");

}
