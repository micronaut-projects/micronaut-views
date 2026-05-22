# Agent Guidance

This file applies to the whole `micronaut-views` repository. Keep changes
focused on Micronaut Views behavior, tests, and documentation; do not fold in
unrelated company, workflow, or template-package policy.

## Repository Shape

- The default development branch is `6.0.x`; confirm the intended target branch
  before opening a pull request.
- Runtime modules are named `views-*`, for example `views-core`,
  `views-thymeleaf`, `views-freemarker`, `views-handlebars`, `views-soy`,
  `views-jte`, `views-jstachio`, `views-pebble`, `views-rocker`, `views-htmx`,
  `views-turbo`, `views-react`, `views-velocity`, and `views-fieldset`.
- Cross-engine behavior is usually tested in `test-suite*` projects. GraalVM
  coverage lives under `test-suite-graal:*`.
- User guide sources live in `src/main/docs/guide`. Update `toc.yml` when adding
  a new guide page.
- Some root files and GitHub workflows are synchronized from the Micronaut
  project template. Avoid local edits to synchronized files unless the task
  explicitly requires a repo-specific override.

## Code Conventions

- Follow nearby Micronaut patterns before introducing new abstractions.
- Use `jakarta.inject` APIs, constructor injection where practical, and
  `@ConfigurationProperties` for configuration models.
- Use JSpecify annotations from `org.jspecify.annotations` for new or changed
  nullability contracts. New Java packages should include `package-info.java`
  with `@NullMarked` unless the surrounding package style clearly differs.
- Mark non-user-facing APIs with `@io.micronaut.core.annotation.Internal`.
  Mark unstable user-facing APIs with `@Experimental`.
- Preserve binary compatibility for public APIs. Prefer adding overloads or
  deprecating with replacement guidance over changing existing signatures.
- Keep renderer-specific dependencies and tests in the matching `views-*`
  module unless behavior is intentionally shared through `views-core`.

## Views-Specific Notes

- Template engines have separate modules; avoid coupling one engine module to
  another unless the repository already has that dependency.
- Security, CSRF, CSP, HTMX, Turbo, and fieldset behavior is user-visible.
  Changes there should include focused tests and guide updates when behavior or
  configuration changes.
- React support includes Java and JavaScript resources. When editing React
  rendering assets, check `views-react/README.md` and the `views-react/src/test/js`
  fixtures.
- GraalVM behavior is important for renderers. If a change affects runtime
  initialization, resource loading, reflection, or template discovery, consider
  the relevant `test-suite-graal:*` project.

## Verification

Use the narrowest Gradle task that proves the change, then expand if shared
behavior or public API is affected. Common examples:

```bash
./gradlew :views-core:test
./gradlew :views-thymeleaf:test
./gradlew :test-suite:test
./gradlew check
./gradlew docs
```

For docs-only or instruction-only changes, record the targeted validation you
performed; a full Gradle build is not automatically required.
