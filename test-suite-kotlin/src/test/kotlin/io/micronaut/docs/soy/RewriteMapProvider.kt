package io.micronaut.docs.soy

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.template.soy.shared.SoyCssRenamingMap
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Nullable
import io.micronaut.views.soy.SoyNamingMapProvider
import jakarta.inject.Singleton
import java.net.URL

@Requires(property = "spec.name", value = "RewriteMapProviderTest")
//tag::clazz[]
@Singleton
class RewriteMapProvider : SoyNamingMapProvider {

    /**
     * Provide a CSS renaming map to Soy/Micronaut.
     *
     * @return Inflated Soy CSS renaming map.
     */
    @Nullable
    override fun cssRenamingMap(): SoyCssRenamingMap {
        return CSS_RENAMING_MAP!!
    }

    companion object {
        /** Filename for the JSON class renaming map.  */
        private const val RENAMING_MAP_NAME = "styles/renaming-map.json"

        /** Naming map to use for classes.  */
        private var CSS_RENAMING_MAP: SoyCssRenamingMap? = null

        /**
         * Load JSON embedded in a JAR resource into a naming map for Soy rendering.
         *
         * @param mapPath URL to the JAR resource we should load.
         */
        @Nullable
        private fun loadMapFromJSON(mapPath: URL): Map<String?, String?>? {
            return try {
                ObjectMapper().readValue<Map<String?, String?>?>(
                    mapPath,
                    object : TypeReference<Map<String?, String?>?>() {})
            } catch (thr: Throwable) {
                //handle `JsonMappingException` and `IOException`, if, for instance, you're using Jackson
                throw RuntimeException(thr)
            }
        }

        init {
            val mapPath = RewriteMapProvider::class.java.classLoader.getResource(RENAMING_MAP_NAME)
            if (mapPath != null) {
                // load the renaming map
                val cssRenamingMap = loadMapFromJSON(mapPath)
                if (cssRenamingMap != null) {
                    CSS_RENAMING_MAP =
                        SoyCssRenamingMap { className -> // (or whatever logic you need to rewrite the class)
                            cssRenamingMap[className]
                        }
                }
            }
        }
    }
}
