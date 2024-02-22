package io.micronaut.views.docs.soy

import com.google.template.soy.SoyFileSet
import io.micronaut.context.annotation.Requires
import io.micronaut.core.io.ResourceLoader
import io.micronaut.views.ViewsConfiguration
import io.micronaut.views.soy.SoyFileSetProvider
import jakarta.inject.Singleton
import java.io.File
import java.net.URISyntaxException
import java.net.URL
import org.slf4j.LoggerFactory

@Requires(property = "spec.name", value = "soy")
//tag::clazz[]
@Singleton
class CustomSoyFileSetProvider(
    private val resourceLoader: ResourceLoader,
    viewsConfiguration: ViewsConfiguration
) : SoyFileSetProvider {

    private val folder: String = viewsConfiguration.folder

    override fun provideSoyFileSet(): SoyFileSet {
        val builder = SoyFileSet.builder()
        for (template in VIEWS) {
            resourceLoader.getResource("$folder/$template").ifPresent { url: URL ->
                try {
                    builder.add(File(url.toURI()))
                } catch (e: URISyntaxException) {
                    if (LOG.isWarnEnabled) {
                        LOG.warn("URISyntaxException raised while generating the SoyFileSet for folder {}", folder, e)
                    }
                }
            }
        }
        return builder.build()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CustomSoyFileSetProvider::class.java)
        private val VIEWS = arrayOf("home.soy")
    }
}
//end::clazz[]
