import logging

from com.google.template.soy import SoyFileSet
from jakarta.inject import Singleton
from java.io import File
from micronaut.context.annotation import Requires
from micronaut.core.io import ResourceLoader
from micronaut.views import ViewsConfiguration
from micronaut.views.soy import SoyFileSetProvider

LOG = logging.getLogger(__name__)


@Requires(property="spec.name", value="soy")
# tag::clazz[]
@Singleton
class CustomSoyFileSetProvider(SoyFileSetProvider):
    VIEWS = ["home.soy"]

    def __init__(self, views_configuration: ViewsConfiguration, resource_loader: ResourceLoader):
        self.folder = views_configuration.getFolder()
        self.resource_loader = resource_loader

    def provideSoyFileSet(self) -> SoyFileSet:
        builder = SoyFileSet.builder()
        for template in self.VIEWS:
            url = self.resource_loader.getResource(self.folder + "/" + template)
            if url.isPresent():
                try:
                    builder.add(File(url.get().toURI()))
                except Exception:
                    LOG.warning("Exception raised while generating the SoyFileSet for folder %s", self.folder, exc_info=True)
        return builder.build()
# end::clazz[]
