# DesktopPet roadmap

This file is DesktopPet's repository-local fallback task source. GitHub Issues remain
supported and take priority when their full requirements are available, as described in
[`AGENTS.md`](../AGENTS.md). When roadmap-driven work is authorized, select the first
incomplete milestone below and implement only that milestone in one pull request. Mark
it complete in the same pull request after every acceptance criterion is satisfied.

## Completed foundation

- [x] Reproducible Windows Maven build.
- [x] DesktopPet branding.
- [x] Original DevPet development mascot.
- [x] JUnit 5 test foundation.
- [x] Windows pull-request CI using `mvn --batch-mode clean verify`.

## Future milestones

### 1. [x] Windows integration abstraction for desktop/window information

- Introduce a platform-neutral interface for reading desktop bounds, work-area
  bounds, and top-level window snapshots without exposing JNA types to callers.
  - Define an immutable window snapshot containing a stable native identifier, bounds,
    title, visibility, and minimized state.
  - Add a Windows implementation that maps existing JNA calls into the neutral model;
    retain a safe non-Windows implementation so startup remains cross-platform.
  - Route no mascot behavior to the new API yet.
  - Add non-GUI unit tests for the model and for mapping/filter-independent logic.
  - Document any Windows behavior that still requires manual verification.

Manual verification remains required on Windows 10 and Windows 11 to confirm that the
virtual-desktop and primary work-area coordinates reflect mixed-DPI, taskbar, and
negative-coordinate multi-monitor layouts, and that enumerated window titles, bounds,
visibility, and minimized state agree with representative native applications. The new
API is not used by mascot behavior yet, as required by this milestone.

### 2. [x] Visible top-level window discovery and filtering

- Discover application windows through the abstraction and return only usable,
  visible top-level windows.
  - Exclude minimized, cloaked, zero-area, tool/owned, DesktopPet-owned, shell, and
    otherwise non-interactive windows using explicitly documented filter rules.
  - Keep discovery/filter logic separate from mascot behavior and terrain generation.
  - Add unit tests for every filter rule using synthetic snapshots.
  - Manually verify representative Windows 10 or Windows 11 applications when possible.

Manual verification remains required on Windows 10 and Windows 11. Confirm that common
applications are returned while minimized applications, UWP/cloaked windows, tool and
owned popups, DesktopPet windows, the shell, disabled windows, and zero-area windows are
excluded. This environment cannot perform that native Windows check; all filter rules are
covered by platform-independent synthetic tests.

### 3. [x] Pure window terrain / rectangle-edge model

- Convert discovered window rectangles into a platform-independent terrain model.
  - Represent top, bottom, left, and right edges with deterministic coordinates and a
    link to the source window identifier.
  - Define and test edge containment, nearest-edge lookup, overlap, and ordering rules,
    including negative desktop coordinates and overlapping windows.
  - Keep all geometry free of JNA and GUI dependencies.
  - Do not connect the terrain to mascot movement in this milestone.

The platform-neutral terrain model creates deterministically ordered top, bottom, left,
and right edges from eligible snapshots, retaining each source window identifier. Pure
unit tests cover containment, overlap, nearest-edge selection, negative coordinates,
overlapping windows, ordering, and eligibility filtering. The model itself has no JNA or
GUI dependencies; its movement integration is introduced separately by milestone 4.

### 4. [x] Mascot landing on application-window top edges

- Allow a falling mascot to land and stand on eligible application-window top
  edges while preserving existing desktop-floor landing behavior.
  - Use the terrain model rather than direct native-window calls in physics code.
  - Choose the first crossed top edge deterministically and prevent tunneling through it.
  - Ignore ineligible or stale windows safely.
  - Add non-GUI regression tests for landing calculations and fallback-to-floor cases.
  - Manually verify landing on common applications on Windows 10 or Windows 11.

Falling now uses a pure swept-segment calculation against eligible top-edge terrain,
selecting the first crossed edge deterministically and retaining the existing work-area
floor as its fallback. Tests cover fast-fall tunneling, diagonal crossings, deterministic
overlap resolution, stale/ineligible snapshots, upward movement, and floor fallback.
Manual verification remains required on Windows 10 and Windows 11 to confirm landing and
standing on common application windows, including overlapping windows, while ordinary
desktop-floor landing remains unchanged.

### 5. [x] Tracking moved/resized windows

- Keep occupied window terrain synchronized when its source window moves, resizes,
  minimizes, closes, or changes eligibility.
  - Refresh snapshots at a bounded rate without blocking the mascot update loop.
  - Carry a standing mascot with a moved/resized supporting top edge when valid, and
    make it fall safely when support disappears.
  - Add deterministic tests for snapshot changes and support invalidation.
  - Document and manually check responsiveness and resource usage on Windows.

Eligible-window discovery now runs on one daemon worker, at most four times per second,
and publishes immutable terrain without waiting in the mascot loop. A standing anchor is
carried proportionally when its source top edge moves or resizes; loss of eligible support
causes existing bordered actions to transition safely to falling. See
[`WINDOW_INTERACTION_SMOKE_TEST.md`](WINDOW_INTERACTION_SMOKE_TEST.md) for required Windows checks.

### 6. [x] Side/ceiling interactions such as climbing/hanging

- Add configuration-driven mascot interactions with application-window side and
  bottom edges.
  - Extend terrain queries so behavior code can distinguish top, side, and bottom edges.
  - Provide at least one climb transition and one hang transition using existing action
    and behavior mechanisms rather than character-specific Java logic.
  - Preserve ordinary screen-edge interactions when no window edge is eligible.
  - Test pure edge selection and transition preconditions; list manual animation checks.

Window side and bottom edges now participate in the existing wall and ceiling border APIs.
Configuration supplies a side-climb transition and a bottom-edge hang transition while
the original work-area wall/ceiling fallbacks remain unchanged.

### 7. [x] Richer behavior, state, and personality

- Add a small, configuration-driven personality/state increment with deterministic
  non-GUI tests where feasible and no character-specific engine coupling.

A generic bounded integer personality store and configured `IncrementState` action let
the occasional curious-observation behavior accumulate curiosity without mascot-specific
engine logic. The configured cap also controls when that behavior remains eligible.

### 8. [x] Direct user interaction

- Add one focused interaction such as clicking or petting, including clear input
  behavior, configuration-driven mascot response, and manual GUI verification steps.

A head hotspot uses the existing primary-button hotspot mechanism to select a configured
pet response: affection increments, the mascot spins its head, and then returns to standing.
Manual input and animation checks are listed in the Windows smoke-test document.

### 9. [ ] Lightweight bubbles, sounds, and presentation

- Add one lightweight, optional presentation increment that respects mute/settings,
  uses original or compatibly licensed content, and does not obstruct desktop use.

### 10. [ ] Multiple-pet interactions

- Add one bounded interaction between multiple pets with deterministic coordination
  logic, safe behavior when a participant disappears, and non-GUI tests where feasible.

### 11. [ ] Settings, display handling, persistence, and product quality

- Improve one cohesive product-quality area spanning settings/persistence or
  multi-monitor/fullscreen handling, preserving backward-compatible defaults and
  documenting Windows 10/11 manual checks.

### 12. [ ] Packaging, productization, and eventual Steam integration

- Define and implement one self-contained packaging/productization increment toward
  a distributable Windows application; treat Steam integration as an eventual follow-up
  requiring its own explicit requirements and credentials-free validation.
