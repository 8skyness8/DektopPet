# AGENTS.md — DesktopPet

## Project goal

DesktopPet is a Windows-first desktop pet application derived from the open-source
Shimeji-Desktop project.

Upstream:
https://github.com/DalekCraft2/Shimeji-Desktop

The project will preserve the useful Shimeji mascot/behavior engine while gradually
adding original DesktopPet behavior, UI, character assets, tests, and product features.

## Development rules

1. The GitHub Issue and its Acceptance Criteria define the scope of each task.
2. Do not implement features outside the current Issue unless they are strictly required
   to satisfy an Acceptance Criterion.
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
    limitation remaining after each Issue.

## Required validation

For ordinary code changes, use the repository's Maven build and test workflow.

At minimum, attempt:

    mvn test
    mvn package

If the repository's effective build uses a different Maven goal, inspect pom.xml and
use the project's configured verification path instead of guessing.

Do not claim a test passed unless it was actually executed successfully.

## Working style

For each Issue:

1. Read the Issue and Acceptance Criteria.
2. Inspect the relevant existing code/configuration.
3. State a concise implementation plan.
4. Implement only the requested scope.
5. Add/update tests where appropriate.
6. Run validation.
7. Review the diff for unrelated changes.
8. Report:
   - implementation summary
   - files changed
   - tests/build commands run
   - results
   - remaining limitations, if any

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
