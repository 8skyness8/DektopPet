# DesktopPet Internal V1 Batch A manual smoke test

Run this test on the confirmed corporate Windows 10/11 PC from a writable local copy of the complete `internal/` directory. Do not change execution policy or elevate. The expected bridge refresh is about 400 ms, and shutdown can take slightly longer than one refresh.

1. Launch `DesktopPet_Internal.hta`.
2. Verify its background is transparent and no magenta rectangle remains. Verify the mascot is borderless, compact, and absent from the taskbar where the host supports that request. If transparency setup fails, verify the pet remains usable rather than crashing.
3. Verify the mascot walks and falls. Hover it to reveal the small info and exit controls; verify Alt+F4 remains usable (relaunch if used).
4. Open Notepad or Excel (an ordinary visible browser or application window is also valid).
5. Position that application window below the pet.
6. Drag and release the pet above the application if needed. Verify it falls and lands with its feet on the application window's top edge, then stands or walks there.
7. Move the supporting application window.
8. Verify the supported pet follows that same window and stays on its top edge.
9. Move or resize the application so the pet no longer overlaps its top edge.
10. Verify the pet falls instead of teleporting to an unrelated window.
11. Drag the mascot and release it. Verify dragging starts only on the mascot; mouse release, focus loss, and Escape each stop dragging.
12. Verify it resumes falling physics and can land again.
13. Select the small **x** control (or press Alt+F4) to exit DesktopPet.
14. In `data/session_*`, verify `status.txt` changes to `stopped` within a reasonable bounded time (normally under two seconds; allow up to 15 seconds for stale-heartbeat protection if the HTA was forcibly terminated).
15. In Task Manager, verify no persistent orphan `powershell.exe` associated with DesktopPet remains. Do not terminate unrelated corporate PowerShell processes.

## Optional debug check

Select the small **i** control. Confirm the compact overlay reports state, x/y, supporting HWND, usable terrain count, bridge heartbeat age, and bridge status. Select **i** again before continuing normal use. The debug preference should survive restart in `data/settings.ini`.

## Record

Record pass/fail for every numbered step, Windows version/display scaling, monitor arrangement, bridge status text, approximate shutdown time, and any corporate security message. A failure in transparency, real-window landing, support tracking, or orphan prevention blocks acceptance.
