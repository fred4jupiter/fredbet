package de.fred4jupiter.fredbet.service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.junit.runner.RunWith;
import org.springframework.stereotype.Controller;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "de.fred4jupiter.fredbet")
public class AchitectureUT {

	@ArchTest
	public static void controllerHasToResideInWebPackage(JavaClasses classes) {
		classes().that().areAnnotatedWith(Controller.class).should().resideInAPackage("de.fred4jupiter.fredbet.web..").check(classes);
	}
}
