from dataclasses import dataclass

from micronaut.core.annotation import Introspected


@Introspected
@dataclass
class Author:
    id: int
    title: str
