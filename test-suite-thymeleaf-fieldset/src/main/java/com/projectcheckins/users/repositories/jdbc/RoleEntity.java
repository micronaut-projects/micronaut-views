package com.projectcheckins.users.repositories.jdbc;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.*;
import jakarta.validation.constraints.NotBlank;

@MappedEntity("role")
public record RoleEntity(@Id @GeneratedValue Long id, @NotBlank  String authority) {
}
