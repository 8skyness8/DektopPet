# Windows smoke test: roadmap milestones 5–8

Run these checks on Windows 10 and Windows 11 before merging. Start DesktopPet with one
DevPet and leave **Pause Animations** disabled.

1. Open Notepad, place DevPet above it, and let it fall onto the window's top edge. Move
   Notepad slowly, then resize it from both sides. Confirm DevPet follows the top edge
   without jitter and remains horizontally on the resized edge.
2. Minimize Notepad while DevPet stands on it; restore it, close it, and repeat while
   moving it quickly. Confirm DevPet falls safely whenever support disappears and never
   freezes or jumps to a stale window. Repeat with a window that becomes ineligible, such
   as a disabled or tool window if one is available.
3. Leave Task Manager visible for two minutes while repeatedly moving and resizing two
   ordinary windows. Confirm the `DesktopPet-window-terrain` worker does not grow in
   count, DesktopPet remains responsive, and CPU usage returns near idle when movement
   stops. Discovery is intentionally bounded to one in-flight refresh and four starts per
   second.
4. Put DevPet on an eligible application's left or right side and wait for the configured
   **ClimbWindowSide** transition. Confirm it climbs approximately 80 pixels and releases
   into the normal fall. Verify ordinary left/right screen-wall climbing still works when
   no application edge is present.
5. Put DevPet on the bottom edge of an eligible application and wait for
   **HangFromWindowBottom**. Confirm it visibly hangs, traverses along the bottom edge,
   and falls safely if the window is minimized, closed, moved away, or becomes ineligible.
   Verify ordinary screen-ceiling behavior still works away from application windows.
6. Leave DevPet standing long enough to see **CuriousObservation**: it looks upward and
   returns to its normal behavior. Repeat until curiosity reaches its configured cap of
   10 and confirm the observation no longer starts in that mascot session.
7. While DevPet is standing, hover over its head (the cursor should become a hand) and
   press and release the primary mouse button without dragging. Confirm the mascot spins
   its head and returns to standing. Repeat several times and confirm dragging outside
   the hotspot and secondary-clicking for the context menu retain their previous behavior.
8. Finally, let DevPet fall directly to the desktop floor and exercise all four physical
   screen edges. Confirm floor landing and existing screen-edge transitions are unchanged.
