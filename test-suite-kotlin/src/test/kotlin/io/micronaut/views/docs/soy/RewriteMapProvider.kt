package io.micronaut.views.docs.soy

import com.google.template.soy.shared.SoyCssRenamingMap
import io.micronaut.context.annotation.Requires
import io.micronaut.core.io.ResourceLoader
import io.micronaut.core.type.Argument
import io.micronaut.json.JsonMapper
import io.micronaut.views.soy.SoyNamingMapProvider
import jakarta.inject.Singleton

@Requires(property = "spec.name", value = "soy")
//tag::clazz[]
@Singleton
class RewriteMapProvider(resourceLoader: ResourceLoader, jsonMapper: JsonMapper) : SoyNamingMapProvider {

    private val cssRenamingMap: Map<String, String> =
        // load the renaming map embedded as a resource
        resourceLoader.getResourceAsStream(RENAMING_MAP_NAME).map { json ->
            json.use { jsonMapper.readValue(it, Argument.mapOf(String::class.java, String::class.java)) }
        }.orElse(emptyMap())

    /**
     * Provide a CSS renaming map to Soy/Micronaut.
     *
     * @return Inflated Soy CSS renaming map.
     */
    override fun cssRenamingMap(): SoyCssRenamingMap {
        // (or whatever logic you need to rewrite the class)
        return SoyCssRenamingMap { className -> cssRenamingMap[className] }
    }

    companion object {
        /** Filename for the JSON class renaming map. */
        private const val RENAMING_MAP_NAME = "renaming-map.json"
    }
}
//end::clazz[]
