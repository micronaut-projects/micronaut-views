package io.micronaut.views.docs.soy;

import com.google.template.soy.SoyFileSet;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.soy.SoyFileSetProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import java.io.File;
import java.net.URISyntaxException;

@Requires(property = "spec.name", value = "soy")
//tag::clazz[]
@Singleton
public class CustomSoyFileSetProvider implements SoyFileSetProvider {
    private static final Logger LOG = LoggerFactory.getLogger(CustomSoyFileSetProvider.class);
    private static final String[] VIEWS = {
            "home.soy"
    };
    private final String folder;
    private final ResourceLoader resourceLoader;

    public CustomSoyFileSetProvider(ViewsConfiguration viewsConfiguration, ResourceLoader resourceLoader) {
        this.folder = viewsConfiguration.getFolder();
        this.resourceLoader = resourceLoader;
    }

    @Override
    public SoyFileSet provideSoyFileSet() {
        final SoyFileSet.Builder builder = SoyFileSet.builder();
        for (final String template : VIEWS) {
            resourceLoader.getResource(folder + "/" + template).ifPresent(url -> {
                try {
                    builder.add(new File(url.toURI()));
                } catch (URISyntaxException e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("URISyntaxException raised while generating the SoyFileSet for folder {}",
                                folder, e);
                    }
                }
            });
        }
        return builder.build();
    }
}
//end::clazz[]
