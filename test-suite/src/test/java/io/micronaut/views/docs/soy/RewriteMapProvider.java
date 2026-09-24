package io.micronaut.views.docs.soy;

import com.google.template.soy.shared.SoyCssRenamingMap;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.type.Argument;
import io.micronaut.json.JsonMapper;
import io.micronaut.views.soy.SoyNamingMapProvider;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@Requires(property = "spec.name", value = "soy")
//tag::clazz[]
@Singleton
public class RewriteMapProvider implements SoyNamingMapProvider {
    /** Filename for the JSON class renaming map. */
    private static final String RENAMING_MAP_NAME = "renaming-map.json";

    private final Map<String, String> cssRenamingMap;

    RewriteMapProvider(ResourceLoader resourceLoader, JsonMapper jsonMapper) throws IOException {
        // load the renaming map embedded as a resource
        try (InputStream json = resourceLoader.getResourceAsStream(RENAMING_MAP_NAME).orElse(null)) {
            this.cssRenamingMap = json == null ? Collections.emptyMap() :
                    jsonMapper.readValue(json, Argument.mapOf(String.class, String.class));
        }
    }

    /**
     * Provide a CSS renaming map to Soy/Micronaut.
     *
     * @return Inflated Soy CSS renaming map.
     */
    @Nullable
    @Override
    public SoyCssRenamingMap cssRenamingMap() {
        // (or whatever logic you need to rewrite the class)
        return cssRenamingMap::get;
    }
}
//end::clazz[]
