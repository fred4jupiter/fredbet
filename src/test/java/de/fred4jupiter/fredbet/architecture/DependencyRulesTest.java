package de.fred4jupiter.fredbet.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import de.fred4jupiter.fredbet.props.FredbetConstants;

import static com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;

@AnalyzeClasses(packages = FredbetConstants.BASE_PACKAGE, importOptions = {ImportOption.DoNotIncludeTests.class})
public class DependencyRulesTest {

//    @ArchTest
//    static final ArchRule no_accesses_to_upper_package = NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;
}
