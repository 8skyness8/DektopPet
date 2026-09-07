# DesktopPet Internal feasibility PoC

This directory is a self-contained, offline, text-only feasibility probe. It does **not** port the Java application and does not assert that HTA is an adequate deployment target merely because it launches. The mascot artwork is original CSS/HTML geometric test art created for this probe; it contains no Little LUMI material.

## Techniques and expected limitations

* `DesktopPet_Internal_PoC.hta` uses only the installed HTA/MSHTML JScript host. HTA attributes request no caption, no border, and no taskbar entry. Corporate policy or Windows configuration can override taskbar behavior, so it must be observed manually.
* CSS/HTML shapes provide the visible mascot and two animation states, so no mascot image file is required. A bounded 100 ms `window.setInterval` moves the native HTA window and reverses at `screen.availWidth`. No busy loop or persistent worker/thread is used.
* MSHTML mouse capture and screen coordinates implement dragging. Diagnostics remain available from the **Diagnostics** tag, and **Exit** always closes the window.
* `Scripting.FileSystemObject` rewrites and reloads `data/settings.ini`, under this copied directory only. It stores only `speech=true` and `testBond=57`; it does not use the registry or personal data.
* A small original transparent PNG made only for this PoC is embedded directly in the HTA as a base64 `data:image/png` URI. It tests PNG alpha without adding a binary repository file or generating anything at runtime. Its transparent pixels expose the colored/checkered HTML stage, but ordinary HTA window composition remains opaque: they do **not** expose the desktop below the native window. The PoC therefore reports `NOT_SUPPORTED`, rather than presenting document-level PNG alpha as true per-pixel window transparency. No helper/native component is used.
* On explicit request only, the probe tries the built-in COM ProgIDs `UIAutomationClient.CUIAutomation`, then `CUIAutomation`. It calls `GetRootElement`, `CreateTrueCondition`, and `FindAll(TreeScope_Children)`, then attempts `CurrentName`, `CurrentBoundingRectangle`, `CurrentIsEnabled`, and `CurrentIsOffscreen` for at most 20 desktop children. Whether UI Automation's COM values are dispatchable to the installed HTA/JScript engine, and whether corporate security permits access, are deliberately left for the target-PC test.
* `WINDOW_GEOMETRY_SUPPORTED` is shown only when at least one real bounding rectangle is returned. Child enumeration without a script-readable rectangle is `WINDOW_ENUMERATION_ONLY`; failure or no children is `NOT_SUPPORTED`. WMI, process lists, and `AppActivate` are not used as substitutes.

## Exact corporate-PC manual procedure

1. Copy the entire `internal-poc` directory to a writable local folder on the corporate PC. Keep the HTA and `data` directory together. There is no image directory or binary asset.
2. Open Notepad, Excel, and Chrome or Edge, if those applications are installed. Keep each visible and non-minimized, arrange them at noticeably different positions and sizes, and note their approximate bounds.
3. Double-click `DesktopPet_Internal_PoC.hta`. If policy blocks it or displays a security prompt, record the complete message; do not weaken security settings.
4. Confirm that the small mascot and diagnostics appear. Check visually for a normal title bar/border and inspect the taskbar for an HTA entry.
5. Look around the mascot's transparent outside pixels. Confirm that they show the colored/checkered stage rather than the real desktop or application underneath. Record the displayed transparency classification.
6. Select **Hide diagnostics**, then **Diagnostics** to restore it. The compact mode should remain recoverable.
7. Select **Stop** and verify the window and frame stop changing; select **Start**, verify frame animation and horizontal movement, and wait for reversal at a screen edge. Confirm the tick counter increments at roughly ten ticks per second while running and remains stable while stopped.
8. Click the mascot and confirm the click counter increments. Drag it by the mascot/stage, release it, and verify normal movement resumes. Use **Diagnostics** if the panel is hidden.
9. Select **Retest local file**. Confirm `PASS (write/read/reload)`, then open `data/settings.ini` and verify it contains exactly the two non-personal settings. Confirm no file was intentionally written elsewhere.
10. Select **Test application windows** once. Confirm the request counter increments once; it must not continue incrementing. Copy the UI Automation status, application-terrain classification, complete error text, and table.
11. Compare table names and rectangles with the visible Notepad, Excel, and browser windows. A named item without numeric `Left`, `Top`, `Width`, and `Height` does not pass application terrain. Record each application's bounds or `NOT_FOUND`.
12. Select **Exit** and confirm the HTA closes. Copy the completed result template and diagnostic text back for assessment.

## Result template

```text
HTA_START:
BORDERLESS:
TASKBAR_HIDDEN:
TRANSPARENCY:
ANIMATION:
DRAG:
LOCAL_FILE_WRITE:
UIAUTOMATION_COM:
WINDOW_GEOMETRY:
NOTEPAD_BOUNDS:
EXCEL_BOUNDS:
BROWSER_BOUNDS:
ERROR_MESSAGES:
```

## Feasibility decision (keep independent)

Report each area separately after the corporate-PC run; do not infer untested results.

* **A. PET_CORE:** HTA launch, rendering, animation, click/drag interaction, and local persistence.
* **B. VISUAL_INTEGRATION:** borderless behavior, taskbar behavior, and transparency. This implementation already identifies true per-pixel transparency as unsupported by HTA alone; the target run verifies the actual presentation.
* **C. APPLICATION_TERRAIN:** actual external application bounding rectangles. This area passes only if the diagnostic table returns credible screen rectangles for application windows; COM activation or element names alone are insufficient.

The current repository/Linux environment cannot execute an HTA or validate target corporate policy. All runtime outcomes above therefore require the prescribed manual Windows test before any overall feasibility conclusion.
