package io.micronaut.views.fields.rocker;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@IncludeClassNamePatterns("io.micronaut.views.fields.tck.InputHiddenViewRenderTest")
@SelectPackages({
        "io.micronaut.views.fields.tck",
})
@SuiteDisplayName("Fieldset TCK for Freemarker")
public class FreemarkerSuite {
}
