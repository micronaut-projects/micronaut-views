from com.google.template.soy.shared import SoyCssRenamingMap
from jakarta.inject import Singleton
from java.lang import String
from micronaut.context.annotation import Executable, Requires
from micronaut.core.io import ResourceLoader
from micronaut.core.type import Argument
from micronaut.json import JsonMapper
from micronaut.views.soy import SoyNamingMapProvider


# tag::clazz[]
class CssRenamingMap(SoyCssRenamingMap):

    def __init__(self, renaming_map: dict[str, str]):
        self.renaming_map = renaming_map

    def get(self, className: str) -> str | None:
        # (or whatever logic you need to rewrite the class)
        return self.renaming_map.get(className)
# end::clazz[]


@Requires(property="spec.name", value="soy")
# tag::clazz[]
@Singleton
class RewriteMapProvider(SoyNamingMapProvider):
    RENAMING_MAP_NAME = "renaming-map.json"  # Filename for the JSON class renaming map.

    def __init__(self, resource_loader: ResourceLoader, json_mapper: JsonMapper):
        # load the renaming map embedded as a resource
        json = resource_loader.getResourceAsStream(self.RENAMING_MAP_NAME).orElse(None)
        self.css_renaming_map = {} if json is None else dict(json_mapper.readValue(json, Argument.mapOf(String, String)))

    @Executable
    def cssRenamingMap(self) -> SoyCssRenamingMap:
        return CssRenamingMap(self.css_renaming_map)
# end::clazz[]
