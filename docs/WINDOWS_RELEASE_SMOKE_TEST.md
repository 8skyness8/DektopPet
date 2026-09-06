# DesktopPet V2 Windows 10/11 release smoke test

Run every section on Windows 10 and Windows 11 from a **newly extracted** release ZIP,
not the development tree. Record OS, JDK/build SHA, observations, and Task Manager values.

## A. Startup

1. Confirm `DesktopPet.exe` starts, the chooser works, a mascot appears, and the tray icon appears.
2. Repeat with `Start-DesktopPet.cmd` (if distributed) from an extraction path containing spaces.

## B. Natural behavior

1. Observe one DevPet continuously for at least 10 minutes.
2. Confirm there is no rapid repetition loop, rest/exploration/attention vary, and speech is not spammy.

## C. User relationship

1. Pet repeatedly, drag once, and throw once; record the displayed/script-visible bond if debugging it.
2. Rapidly click for at least 30 seconds and confirm bond remains bounded and cooldown prevents farming.
3. Exit normally, restart DesktopPet, and confirm the relationship remains valid and retains its value.
4. Confirm `conf/relationships.properties` contains only encoded image-set keys, bond values, and event timestamps.

## D. Social behavior

1. Spawn two mascots and wait for one coherent notice/greeting/together/disengage encounter.
2. Spawn five mascots; confirm exclusive pairs, no all-pet lock, and no endless reciprocal greetings.
3. During separate encounters dismiss, drag, and throw one participant; confirm both release immediately,
   transient speech clears, safety behavior proceeds, and the remaining mascot resumes ordinary behavior.

## E. Existing window behavior

1. Land on Notepad (or another ordinary window), then move and resize it.
2. Minimize and close it; confirm fall/recovery. Exercise window-side climb and window-bottom hang.
3. Repeat on mixed-DPI/multiple monitors, including negative desktop coordinates when available.

## F. Settings

1. Toggle speech bubbles, restart, and confirm persistence; explicitly save `false`, restart, and confirm it remains false.
2. Set `NaturalBehavior=false`, restart, and confirm optional selection approaches legacy behavior while drag,
   throw, fall, and other safety paths still work. Restore it to `true` and restart.

## G. Resource usage

1. Observe 1, 5, and 10 mascots for several minutes each in Task Manager.
2. Confirm there is no obvious runaway CPU, memory, or thread growth and social scans remain intermittent.

## H. Packaging

1. Build with JDK 25 using `mvn clean verify`; confirm `target/DesktopPet_1.0.22.zip` exists.
2. Test both `DesktopPet.exe` and `Start-DesktopPet.cmd` directly from the fresh extraction.
3. Confirm licenses/notices remain present and no network login, credentials, telemetry, or SDK is requested.

## Known limitations / pass gate

- Social movement uses existing configured Shimeji actions; there is no pathfinding and pets do not teleport.
- Relationship state is shared by image-set folder identity, not by individual clone, user account, or device.
- A failed relationship-file write keeps the bounded in-memory value for the process and retries on a later event.
- Milestone 8 remains unchecked until every applicable item above is recorded as passed on real Windows.
