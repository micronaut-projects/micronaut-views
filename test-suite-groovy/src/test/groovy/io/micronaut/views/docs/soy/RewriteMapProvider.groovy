package io.micronaut.views.docs.soy

import com.google.template.soy.shared.SoyCssRenamingMap
import io.micronaut.context.annotation.Requires
import io.micronaut.core.io.ResourceLoader
import io.micronaut.core.type.Argument
import io.micronaut.json.JsonMapper
import io.micronaut.views.soy.SoyNamingMapProvider
import jakarta.inject.Singleton
import org.jspecify.annotations.Nullable

@Requires(property = "spec.name", value = "soy")
//tag::clazz[]
@Singleton
class RewriteMapProvider implements SoyNamingMapProvider {
    /** Filename for the JSON class renaming map. */
    private static final String RENAMING_MAP_NAME = "renaming-map.json"

    private final Map<String, String> cssRenamingMap

    RewriteMapProvider(ResourceLoader resourceLoader, JsonMapper jsonMapper) {
        // load the renaming map embedded as a resource
        InputStream json = resourceLoader.getResourceAsStream(RENAMING_MAP_NAME).orElse(null)
        this.cssRenamingMap = json == null ? [:] :
                json.withCloseable { jsonMapper.readValue(it, Argument.mapOf(String, String)) }
    }

    /**
     * Provide a CSS renaming map to Soy/Micronaut.
     *
     * @return Inflated Soy CSS renaming map.
     */
    @Nullable
    @Override
    SoyCssRenamingMap cssRenamingMap() {
        // (or whatever logic you need to rewrite the class)
        return { String className -> cssRenamingMap[className] } as SoyCssRenamingMap
    }
}
//end::clazz[]
