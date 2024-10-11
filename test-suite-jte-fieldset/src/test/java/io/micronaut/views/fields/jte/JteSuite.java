package io.micronaut.views.fields.jte;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages({
        "io.micronaut.views.fields.tck",
})
@IncludeClassNamePatterns({
        "io.micronaut.views.fields.tck.InputHiddenViewRenderTest"
})
@SuiteDisplayName("Fieldset TCK for Jte")
class JteSuite {
}
