package io.micronaut.views.fields.thymleaf;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
//@IncludeClassNamePatterns(
//        "io.micronaut.views.fields.tck.SelectViewRenderTest"
//)
@SelectPackages({
        "io.micronaut.views.fields.tck",
})
@SuiteDisplayName("Fieldset TCK for Thymeleaf")
public class ThymeleafSuite {
}
