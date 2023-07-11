package io.micronaut.docs.soy

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.template.soy.shared.SoyCssRenamingMap
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Nullable
import io.micronaut.views.soy.SoyNamingMapProvider
import jakarta.inject.Singleton

import javax.validation.constraints.NotNull

@Requires(property = "spec.name", value = "RewriteMapProviderSpec")
//tag::clazz[]
@Singleton
class RewriteMapProvider implements SoyNamingMapProvider {
    /** Filename for the JSON class renaming map. */
    private static final String RENAMING_MAP_NAME = "styles/renaming-map.json"

    /** Naming map to use for classes. */
    private static SoyCssRenamingMap CSS_RENAMING_MAP = null

    /**
     * Load JSON embedded in a JAR resource into a naming map for Soy rendering.
     *
     * @param mapPath URL to the JAR resource we should load.
     */
    @Nullable
    private static Map<String, String> loadMapFromJSON(URL mapPath) {
        try {
            return new ObjectMapper().readValue(mapPath, new TypeReference<Map<String, String>>() { })
        } catch (Throwable thr) {
            //handle `JsonMappingException` and `IOException`, if, for instance, you're using Jackson
            throw new RuntimeException(thr)
        }
    }

    static {
        final URL mapPath =
                RewriteMapProvider.class.getClassLoader().getResource(RENAMING_MAP_NAME)
        if (mapPath != null) {
            // load the renaming map
            final Map<String, String> cssRenamingMap = loadMapFromJSON(mapPath)
            if (cssRenamingMap != null) {
                CSS_RENAMING_MAP = new SoyCssRenamingMap() {
                    @Nullable @Override
                    public String get(@NotNull String className) {
                        // (or whatever logic you need to rewrite the class)
                        return cssRenamingMap.get(className)
                    }
                }
            }
        }
    }

    /**
     * Provide a CSS renaming map to Soy/Micronaut.
     *
     * @return Inflated Soy CSS renaming map.
     */
    @Nullable @Override SoyCssRenamingMap cssRenamingMap() {
        return CSS_RENAMING_MAP
    }
}
//end::clazz[]
