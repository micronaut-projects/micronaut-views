from dataclasses import dataclass
from typing import Annotated

from jakarta.validation.constraints import Max, Min, NotBlank, NotNull, Size
from micronaut.serde.annotation import Serdeable


@Serdeable
@dataclass
class BookSave:
    title: Annotated[str, Size(min=2, max=255), NotBlank]
    pages: Annotated[int | None, Min(1), Max(21450), NotNull]
