from dataclasses import dataclass

from micronaut.core.annotation import Introspected


@Introspected
@dataclass
class Message:
    id: int
    name: str
    content: str
