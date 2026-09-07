# DesktopPet Internal roadmap

## Product generations

* **Java edition (repository root): frozen reference implementation.** Do not refactor it as part of Internal work.
* **`internal-poc/`: historical feasibility evidence only.** It is not a production base.
* **`internal/`: active production implementation.** It uses only inbox Windows HTA, JScript, Windows PowerShell, and Win32 APIs.

## Internal V1 Batch A — implemented, pending manual Windows acceptance

Batch A establishes the production vertical slice: a compact color-keyed HTA mascot, one persistent file-coordinated PowerShell/user32 terrain bridge, filtered window-top terrain, deterministic falling/standing/walking physics, landing, support tracking, direct dragging, local settings, debug status, and bounded shutdown.

The implementation is marked **manual review** until the corporate-PC smoke test passes. Linux/Maven validation cannot exercise MSHTA, Windows PowerShell, layered windows, or real user32 window geometry.

## V1 Batch B — future

* Better edge collision
* Jumping
* Throwing and inertia
* Side-of-window interactions
* Hanging and bottom interactions
* Multi-monitor improvements

## V1 Batch C — future

* Natural behavior state machine
* Idle, rest, and sleep
* Behavior memory and repetition suppression
* Mood and needs

## V1 Batch D — future

* Speech bubbles
* Relationship and bond
* Extended persistence
* Multi-pet social interactions

## V1 Batch E — future

* Settings UI
* Performance tuning
* Stability hardening
* Packaging and distribution documentation
