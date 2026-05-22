# Agent Guidance

This repository maintains Micronaut Views modules and their documentation. Keep
changes scoped to the issue, preserve module boundaries, and follow the existing
Gradle and Micronaut build conventions.

## Scope

- Put production code in the relevant `views-*` module and documentation under
  `src/main/docs/guide`.
- Update tests with behavior changes; new template engines or renderers need
  focused coverage for rendering, configuration, and native-image-sensitive
  paths when applicable.
- Keep generated build output out of commits.
- Treat branch selection as release policy: bug fixes and compatible
  improvements usually target the default branch, while new enhancements may
  require maintainer approval for the active release branch.

## Verification

- Use the narrowest relevant Gradle task first, such as a module test task or
  documentation check tied to the changed area.
- Run broader checks only when touching shared build logic, public APIs, or
  cross-module behavior.
- If CI fails in an unrelated baseline path, record the target-branch evidence
  and separate baseline work from the PR under review.

## Pull Requests

- Target the repository default branch unless the issue, release policy, or
  maintainer direction requires another branch.
- Explain the verified module and documentation tasks in the PR body.
- Do not merge PRs or cut releases from an agent run.
