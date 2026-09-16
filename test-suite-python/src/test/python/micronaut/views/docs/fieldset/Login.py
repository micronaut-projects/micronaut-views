from dataclasses import dataclass
from typing import Annotated

from jakarta.validation.constraints import NotBlank
from micronaut.core.annotation import Introspected
from micronaut.views.fields.annotations import InputPassword


@Introspected
@dataclass
class Login:
    username: Annotated[str, NotBlank]
    password: Annotated[str, InputPassword, NotBlank]
