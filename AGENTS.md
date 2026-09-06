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

### Batch roadmap work

When the user explicitly authorizes multiple consecutive roadmap milestones in one task,
treat that requested batch as one task while preserving each milestone's acceptance
criteria separately.

- Implement only the requested number of consecutive incomplete milestones, in roadmap
  order.
- Mark each milestone complete only after all of its own acceptance criteria are met.
- If a later milestone cannot be implemented safely because an earlier milestone exposes
  an architectural or verification problem, stop at the last safely completed milestone
  and report why.
- If any milestone in the batch requires manual review before merge, the whole pull
  request requires manual review.

## Required validation

For ordinary code changes, use the repository's Maven build and test workflow.

At minimum, attempt:

    mvn test
    mvn clean verify

If the repository's effective build uses a different Maven goal, inspect pom.xml and
use the project's configured verification path instead of guessing.

Do not claim a test passed unless it was actually executed successfully. A Codex/local
environment failure caused only by blocked Maven Central access may be reported as an
environment limitation; the Windows pull-request CI remains the authoritative hosted
build/test gate.

## Pull request automation contract

Every Codex pull request must include exactly one of these standalone lines in its body:

    Automation: auto-merge

or

    Automation: manual-review

Use `Automation: auto-merge` only when all acceptance criteria required before merge can
be validated by automated tests/CI and any remaining manual Windows checks are explicitly
non-blocking documentation/follow-up checks. Use `Automation: manual-review` when the task
requires human verification before merge, changes user-visible GUI/input/animation or
runtime behavior that is not adequately covered by tests, has unresolved uncertainty,
or explicitly calls for a manual Windows check before completion.

When uncertain, choose `Automation: manual-review`.

A pull request marked `Automation: auto-merge` is eligible for repository automation only
when all of the following are true:

- it targets `main`;
- it comes from a branch in this repository whose name starts with `codex/`;
- it is not a draft;
- the `Validate pull requests` workflow succeeds for the exact current head commit;
- it is not also marked `Automation: manual-review`;
- it does not carry a `manual-review` label.

The auto-merge workflow must never check out or execute untrusted pull-request code with a
write-capable token. It may merge only the exact commit SHA that successfully completed
the validation workflow. CI failure, a newer unvalidated commit, missing automation
marker, draft state, forked PR, or manual-review marker/label must leave the PR unmerged.

## Autonomous task loop

For each task:

1. Select exactly one task using the priority above. An explicitly authorized batch of
   consecutive roadmap milestones counts as one task.
2. Inspect the relevant existing code, configuration, and tests.
3. Restate the selected milestone(s) and acceptance criteria.
4. Implement only the requested scope.
5. Add/update tests where appropriate.
6. Run `mvn test` and `mvn clean verify` when applicable.
7. Review the diff for unrelated changes.
8. If the task contains roadmap milestones, mark each completed milestone in
   `docs/ROADMAP.md` in the same pull request only after its criteria are satisfied.
9. Decide the PR automation marker using the policy above and include it in the PR body.
10. Report:
    - implementation summary
    - files changed
    - tests/build commands run
    - results
    - PR automation marker selected and why
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
