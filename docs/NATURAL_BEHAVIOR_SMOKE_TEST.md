# DesktopPet V2 natural-behavior Windows smoke test

Run these checks on Windows 10 and Windows 11 with generated DevPet sprites and the
production `conf/actions.xml` and `conf/behaviors.xml`.

1. Start DesktopPet with no settings file. Open **Settings**, confirm **Speech bubbles**
   is checked, uncheck it, save and restart. Confirm it remains unchecked and no bubbles
   appear. Re-enable it and restart again.
2. Watch one DevPet for ten minutes. Confirm ordinary choices vary, repeated optional
   observations/rests are separated, and idle behavior remains active after cooldowns.
3. Pet the head hotspot several times. Confirm affection changes, the configured response
   plays, at most one bubble is shown, phrases vary, and rapid petting does not spam text.
4. Run at least three pets. Confirm they do not speak simultaneously and that greeting
   behavior remains occasional rather than continuous.
5. Observe walks/runs in both directions. Confirm the mascot faces travel before sustained
   movement and no transition teleports it.
6. Place the mascot on an application window top, side, and bottom. Move/resize/minimize/
   close the window during interactions. Confirm it releases or falls before unrelated
   poses and still lands safely on window tops or the desktop floor.
7. Drag, throw, catch/land, climb screen edges, and trigger breeding. Confirm these forced
   and lifecycle paths are unchanged and always override ordinary natural selection.
8. Repeat on a mixed-DPI multi-monitor layout, including a monitor with negative desktop
   coordinates. Confirm window support and screen-edge behavior remain bounded and smooth.
