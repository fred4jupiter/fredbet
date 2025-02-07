package de.fred4jupiter.fredbet;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("de.fred4jupiter.fredbet")
@IncludeClassNamePatterns({"de.fred4jupiter.fredbet.*UT"})
public class UnitTestSuite {
}
