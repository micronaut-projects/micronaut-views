package io.micronaut.doc.soy

import com.google.template.soy.SoyFileSet
import io.micronaut.context.annotation.Requires
import io.micronaut.core.io.ResourceLoader
import io.micronaut.views.ViewsConfiguration
import io.micronaut.views.soy.SoyFileSetProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Requires(property = "spec.name", value = "soy")
//tag::clazz[]
@Singleton
class CustomSoyFileSetProvider implements SoyFileSetProvider {
    private static final Logger LOG = LoggerFactory.getLogger(CustomSoyFileSetProvider.class)

    private static final List<String> VIEWS = [
            "home.soy"
    ]

    private final String folder
    private final ResourceLoader resourceLoader

    CustomSoyFileSetProvider(ViewsConfiguration viewsConfiguration, ResourceLoader resourceLoader) {
        this.folder = viewsConfiguration.getFolder()
        this.resourceLoader = resourceLoader
    }

    @Override
    SoyFileSet provideSoyFileSet() {
        final SoyFileSet.Builder builder = SoyFileSet.builder()
        for (String template : VIEWS) {
            String path = "${folder}${template}"
            resourceLoader.getResource(path).ifPresent(url -> {
                try {
                    builder.add(new File(url.toURI()))
                } catch (URISyntaxException e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("URISyntaxException raised while generating SoyFileSet for folder {}", folder, e)
                    }
                }
            })
        }
        builder.build()
    }
}
//end::clazz[]