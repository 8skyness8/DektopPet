# AGENTS.md — DesktopPet

## Project goal

DesktopPet is a Windows-first desktop pet application derived from the open-source
Shimeji-Desktop project.

Upstream:
https://github.com/DalekCraft2/Shimeji-Desktop

The project will preserve the useful Shimeji mascot/behavior engine while gradually
adding original DesktopPet behavior, UI, character assets, tests, and product features.

## Development rules

1. The selected task and its acceptance criteria define the scope of each change.
2. Do not implement features outside the selected task unless they are strictly required
   to satisfy an acceptance criterion.
3. Before changing existing Shimeji behavior, inspect the current implementation and
   reuse existing mechanisms where practical.
4. Prefer small, focused changes over broad refactors.
5. Do not perform unrelated cleanup or formatting changes.
6. Keep engine code and character assets/configuration separated.
7. Prefer XML/configuration-driven behavior when the existing engine already supports it;
   do not hard-code character-specific behavior in Java without a clear reason.
8. Add or update automated tests for logic that can reasonably be tested without a GUI.
9. After code changes, run the relevant tests and the full Maven verification/build.
10. A task is not complete if the project no longer builds or existing tests fail.
11. Preserve upstream copyright notices, LICENSE.txt, third-party attribution, and
    compatible license information.
12. Do not copy Little LUMI character art, text, sounds, names, branding, or other
    proprietary assets.
13. Target Windows 10 and Windows 11 first. Do not intentionally break upstream
    cross-platform behavior unless an Issue explicitly authorizes a Windows-only change.
14. Avoid introducing new dependencies unless the current implementation cannot
    reasonably satisfy the Issue without them.
15. When adding a dependency, explain why it is needed and prefer actively maintained,
    permissively licensed libraries.
16. Never commit generated build output, local IDE settings, credentials, API keys,
    or machine-specific paths unless the repository already intentionally tracks them.
17. Keep public behavior backward-compatible with the current project unless the Issue
    explicitly requests a breaking change.
18. For bug fixes, add a regression test when feasible.
19. For OS/window/physics logic, separate pure calculation from OS integration whenever
    practical so calculations can be tested without launching the full GUI.
20. Report exactly what was changed, which tests were run, their results, and any known
    limitation remaining after each task.

## Task selection

Use the first applicable source in this priority order:

1. An explicit user request with sufficient requirements.
2. A repository-local task or roadmap milestone explicitly named by the user.
3. A designated GitHub Issue when its full contents and acceptance criteria are available.
4. Otherwise, when roadmap-driven work is authorized, the next incomplete milestone in
   `docs/ROADMAP.md`, in document order.

The roadmap is a fallback local source, not a replacement for GitHub Issues. If only a
GitHub Issue number is provided and its contents cannot be retrieved, do not invent its
contents; request the full issue and acceptance criteria instead. Implement exactly one
roadmap milestone per task and pull request unless the user explicitly asks for more. Do
not invent new roadmap work after every roadmap milestone is complete.

## Required validation

For ordinary code changes, use the repository's Maven build and test workflow.

At minimum, attempt:

    mvn test
    mvn clean verify

If the repository's effective build uses a different Maven goal, inspect pom.xml and
use the project's configured verification path instead of guessing.

Do not claim a test passed unless it was actually executed successfully.

## Autonomous task loop

For each task:

1. Select exactly one task using the priority above.
2. Inspect the relevant existing code, configuration, and tests.
3. Restate the selected milestone and its acceptance criteria.
4. Implement only the requested scope.
5. Add/update tests where appropriate.
6. Run `mvn test` and `mvn clean verify` when applicable.
7. Review the diff for unrelated changes.
8. If the task is a roadmap milestone, mark it complete in `docs/ROADMAP.md` in the same
   pull request once all of its acceptance criteria are satisfied.
9. Report:
   - implementation summary
   - files changed
   - tests/build commands run
   - results
   - remaining manual Windows checks and other limitations, if any

## Architecture direction

Long-term dependency direction should tend toward:

    OS integration
        ↓
    window/desktop model
        ↓
    terrain/collision
        ↓
    physics
        ↓
    behavior/state
        ↓
    mascot presentation

Do not force this architecture prematurely. Refactor toward it only when an Issue
requires functionality that benefits from the separation.
