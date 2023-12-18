package io.micronaut.views.fields.thymeleaf;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages({
        "io.micronaut.views.fields.tck",
})
@IncludeClassNamePatterns("io.micronaut.views.fields.tck.FormCompletedFileUploadRenderTest")
@SuiteDisplayName("Fieldset TCK for Thymeleaf")
class ThymeleafSuite {
}
