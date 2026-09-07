# DesktopPet Internal feasibility PoC

This directory is a self-contained, offline, text-only feasibility probe. It does **not** port the Java application and does not assert that HTA is an adequate deployment target merely because it launches. The mascot artwork is original CSS/HTML geometric test art created for this probe; it contains no Little LUMI material.

## Techniques and expected limitations

* `DesktopPet_Internal_PoC.hta` uses only the installed HTA/MSHTML JScript host. HTA attributes request no caption, no border, and no taskbar entry. Corporate policy or Windows configuration can override taskbar behavior, so it must be observed manually.
* CSS/HTML shapes provide the visible mascot and two animation states, so no mascot image file is required. A bounded 100 ms `window.setInterval` moves the native HTA window and reverses at `screen.availWidth`. No busy loop or persistent worker/thread is used.
* Screen coordinates implement dragging without requiring mouse capture. The target corporate HTA empirically reports `document.setCapture` as unsupported, so the PoC never calls capture methods on `document`. It feature-detects optional `setCapture`/`releaseCapture` only on the mascot element and works without them. Mouseup, focus loss, optional `losecapture`, and Escape all end dragging through the same recovery path. Diagnostics remain available from the **Diagnostics** tag, and **Exit** always closes the window.
* `Scripting.FileSystemObject` rewrites and reloads `data/settings.ini`, under this copied directory only. It stores only `speech=true` and `testBond=57`; it does not use the registry or personal data.
* A small original transparent PNG made only for this PoC is embedded directly in the HTA as a base64 `data:image/png` URI. It tests PNG alpha without adding a binary repository file or generating anything at runtime. Its transparent pixels expose the magenta HTML stage, but ordinary HTA window composition remains opaque: they do **not** expose the desktop below the native window. The PoC therefore reports `NOT_SUPPORTED`, rather than presenting document-level PNG alpha as true per-pixel window transparency. No helper/native component is used.
* On explicit request only, the probe tries the built-in COM ProgIDs `UIAutomationClient.CUIAutomation`, then `CUIAutomation`. It calls `GetRootElement`, `CreateTrueCondition`, and `FindAll(TreeScope_Children)`, then attempts `CurrentName`, `CurrentBoundingRectangle`, `CurrentIsEnabled`, and `CurrentIsOffscreen` for at most 20 desktop children. Whether UI Automation's COM values are dispatchable to the installed HTA/JScript engine, and whether corporate security permits access, are deliberately left for the target-PC test.
* `WINDOW_GEOMETRY_SUPPORTED` is shown only when at least one real bounding rectangle is returned. Child enumeration without a script-readable rectangle is `WINDOW_ENUMERATION_ONLY`; failure or no children is `NOT_SUPPORTED`. WMI, process lists, and `AppActivate` are not used as substitutes.
* **Confirmed target result:** UI Automation COM activation fails for both `UIAutomationClient.CUIAutomation` and `CUIAutomation` with “cannot create automation server object.” That failed path remains available in the diagnostics for reproducibility.
* **Confirmed target result:** Windows PowerShell runs in FullLanguage mode, `Add-Type` and P/Invoke to the installed `user32.dll` work, and `EnumWindows`/`IsWindowVisible`/`GetWindowText`/`GetWindowRect` return real Notepad, Excel, and browser geometry.

## Embedded PowerShell bridge tests

The **Test PowerShell window bridge** button uses the corporate-tested `WScript.Shell.Exec` launch path for the Windows-installed `%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe`. The returned `WshScriptExec` object is immediately discarded: the HTA never accesses `Status`, `ExitCode`, `StdOut`, `StdErr`, or `Terminate`. `WScript.Shell.Run(command, 0, true)` is also not used because the target HTA reports that synchronous mode as incompatible. The command retains `-NoLogo -NoProfile -NonInteractive -EncodedCommand`; its text lives inside the HTA and is encoded as UTF-16LE base64 by JScript. There is no `.ps1`, execution-policy change, elevation, or bundled executable.

Each request deletes stale `data/bridge_result.txt`, `data/bridge_error.txt`, and `data/bridge_done.txt`. On success PowerShell finishes the requested result write and then writes `SUCCESS` to the done file. On failure it writes a plain exception to the error file and then writes `FAILED` to the done file. The done marker is always the final bridge write, so the HTA does not read partial output. No CLIXML is parsed or displayed.

The C# helper performs `EnumWindows` and all filtering/geometry collection itself and returns TSV containing `HWND`, title, left, top, width, and height. PowerShell is only a thin launcher that writes that string to `bridge_result.txt`. The HTA displays at most 20 rows and reports `PASS` only after parsing at least one complete numeric geometry row. PowerShell catches exceptions and writes only `Exception.ToString()` as human-readable plain text to `bridge_error.txt`; progress, warning, and information streams are set to `SilentlyContinue`.

After launch, a bounded HTA timer checks **only** for `bridge_done.txt` every 100 ms. It never inspects the process object. A `bridgeBusy` flag refuses overlaps until the done marker is processed or 20 seconds elapse. At timeout the PoC reports `TIMEOUT`/`MISSING` and clears the flag; it deliberately does not attempt unreliable process termination. Operations are therefore intentionally short and bounded. There is no continuous window enumeration or persistent bridge architecture.

The PowerShell source uses `Add-Type` only in its child process to declare P/Invoke signatures for `EnumWindows`, `IsWindowVisible`, `GetWindowText`, `GetWindowRect`, `GetWindowThreadProcessId`, `GetWindowLong`, `SetWindowLong`, and `SetLayeredWindowAttributes` from the built-in `user32.dll`. A standard `kernel32.dll` `SetLastError` declaration makes style-call error checking unambiguous. No binary is imported or added to the repository.

The **Test color-key transparency** button no longer uses unreliable `FindWindow(title)`. JScript supplies the current HTA screen position and client size. C# enumerates top-level windows, keeps only windows owned by an `mshta` process, and accepts a candidate only when left/top are within 40 pixels and width/height within 80 pixels. Exactly one candidate is required: zero yields `NOT_FOUND`, and multiple yield `AMBIGUOUS`; neither case modifies a window. A confident match is reported as `FOUND hwnd=...`, receives `WS_EX_LAYERED` through `GetWindowLong`/`SetWindowLong`, and is passed to `SetLayeredWindowAttributes` with the obvious magenta key. Automatic mascot movement pauses during identification so its coordinates remain stable.

`COLORKEY_SUPPORTED` means that the API call returned success. The corporate-PC test also visually confirmed that the magenta area became transparent and desktop/application content was visible behind it: visual confirmation is **PASS**. This remains binary color-key transparency, **not** true per-pixel alpha. **Restore Opaque** safely identifies the window again and removes `WS_EX_LAYERED`; Alt+F4 and the diagnostic Exit button remain available. If process launch is denied, the result is `BLOCKED`; file, identification, API, or bridge errors produce `FAILED` with plain error text.

## Current architecture feasibility

* **A. PET_CORE:** HTA launch **PASS**; animation **PASS**; drag after the capture compatibility fix **PASS**; local persistence **PASS**.
* **B. APPLICATION_TERRAIN:** UI Automation COM unavailable; PowerShell FullLanguage **PASS**; `Add-Type`/user32 **PASS**; manual `EnumWindows`/`GetWindowRect` **PASS**; `Exec` launching PowerShell **PASS**; PowerShell creating `bridge_result.txt` **PASS**; real Notepad/Excel/browser geometry in that file **PASS**. `Exec.Status` monitoring is incompatible, and `WScript.Shell.Run` synchronous mode is incompatible. Automatic file-only completion classification remains pending retest.
* **C. VISUAL_INTEGRATION:** native HTA per-pixel alpha unsupported; Win32 color-key transparency was visually confirmed **PASS** on the corporate PC and must be regression-tested through the new file-only completion mechanism. Color-keying must not be described as per-pixel alpha.

## Exact corporate-PC manual procedure

1. Copy the entire `internal-poc` directory to a writable local folder on the corporate PC. Keep the HTA and `data` directory together. There is no image directory or binary asset.
2. Open Notepad, Excel, and Chrome or Edge, if those applications are installed. Keep each visible and non-minimized, arrange them at noticeably different positions and sizes, and note their approximate bounds.
3. Double-click `DesktopPet_Internal_PoC.hta`. If policy blocks it or displays a security prompt, record the complete message; do not weaken security settings.
4. Confirm that the small mascot and diagnostics appear. Check visually for a normal title bar/border and inspect the taskbar for an HTA entry.
5. Look around the mascot's transparent outside pixels. Confirm that they show the magenta stage rather than the real desktop or application underneath. Record the displayed transparency classification.
6. Select **Hide diagnostics**, then **Diagnostics** to restore it. The compact mode should remain recoverable.
7. Select **Stop** and verify the window and frame stop changing; select **Start**, verify frame animation and horizontal movement, and wait for reversal at a screen edge. Confirm the tick counter increments at roughly ten ticks per second while running and remains stable while stopped.
8. Press the mascot, move the cursor, and confirm `DRAG_STATE` reads `DRAGGING`. Release the mouse and verify the HTA immediately stops following the cursor and the state returns to `IDLE`. Dragging must begin only from the mascot, not the empty stage or diagnostics.
9. Click the mascot without dragging, confirm the click counter increments, and verify no script error appears. Start another drag, press Escape, and verify dragging cancels and reports `IDLE`. Also verify switching focus during a drag cancels it. Record `MOUSE_CAPTURE`; `NOT_SUPPORTED` is an expected and supported mode.
10. Select **Retest local file**. Confirm `PASS (write/read/reload)`, then open `data/settings.ini` and verify it contains exactly the two non-personal settings. Confirm no file was intentionally written elsewhere.
11. Select **Test application windows** once. Confirm the request counter increments once; it must not continue incrementing. Copy the UI Automation status, application-terrain classification, complete error text, and table.
12. Compare table names and rectangles with the visible Notepad, Excel, and browser windows. A named item without numeric `Left`, `Top`, `Width`, and `Height` does not pass application terrain. Record each application's bounds or `NOT_FOUND`.
13. Select **Clear diagnostic errors**, then **Test PowerShell window bridge** once. Confirm exactly one invocation is added. Within 20 seconds, require `BRIDGE_DONE: SUCCESS`, `BRIDGE_RESULT_FILE: FOUND`, `BRIDGE_ERROR: none`, `VALID_WINDOW_ROWS` greater than zero, and `POWERSHELL_BRIDGE: PASS`. Confirm the table contains credible HWND/title/rectangle rows and no raw `#< CLIXML` appears. If it fails, copy the plain error and inspect the three bridge text files under `data`. Clicking a PowerShell action again while this request is busy must not launch another process.
14. Select **Test color-key transparency**. Require `HTA_NATIVE_WINDOW` to report exactly one `FOUND hwnd=...`; stop if it says `NOT_FOUND` or `AMBIGUOUS`. Confirm only the magenta regions of this HTA become transparent, the mascot remains visible, other windows are unchanged, and diagnostics/Alt+F4 remain recoverable. Record the exact classification. Select **Restore Opaque** and confirm the magenta background returns. Do not report true per-pixel alpha.
15. Select **Exit** and confirm the HTA closes. Copy the completed result template and diagnostic text back for assessment.

## Result template

```text
HTA_START:
BORDERLESS:
TASKBAR_HIDDEN:
TRANSPARENCY:
ANIMATION:
DRAG:
DRAG_STATE:
MOUSE_CAPTURE:
LOCAL_FILE_WRITE:
UIAUTOMATION_COM:
WINDOW_GEOMETRY:
POWERSHELL_BRIDGE:
POWERSHELL_INVOCATIONS:
BRIDGE_DONE:
BRIDGE_RESULT_FILE:
BRIDGE_ERROR:
VALID_WINDOW_ROWS:
HTA_NATIVE_WINDOW:
TRANSPARENT_WINDOW:
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
