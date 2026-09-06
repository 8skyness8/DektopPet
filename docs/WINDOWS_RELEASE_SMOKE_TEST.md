# Windows 10/11 release smoke test

Run these checks on both Windows 10 and Windows 11 from a clean extracted distribution.

1. Build with JDK 25 using `mvn clean verify`; confirm `target/DesktopPet_1.0.22.zip` exists.
2. Extract the ZIP to a path containing spaces. Double-click `Start-DesktopPet.cmd`; confirm one pet and the tray menu appear without changing the working directory.
3. Set `PresentationBubbles=true` in `conf/settings.properties`, restart, then click the pet's head. Confirm a small **Thanks!** bubble stays within the existing pet window, disappears after about three seconds, takes no focus, and blocks no additional desktop area. Set it to `false`, restart, and confirm no bubble appears. Toggle Sound Effects off and confirm existing configured sounds stop/remain muted.
4. Create a second pet from the pet menu. Leave both on the floor long enough for the occasional greeting; confirm the nearest advertising pet is selected, both face one another, and return to normal behavior. Dismiss either pet during the greeting and confirm the remaining pet continues safely. Confirm one-pet behavior is unchanged.
5. Change several settings, exit from the tray menu, and confirm `conf/settings.properties` is replaced without a leftover `.tmp` file. Restart and confirm the settings persisted. Repeat while installed under a path containing spaces.
6. With two monitors (including a negative-coordinate or mixed-DPI layout if available), move the pets and ordinary application windows between displays. Confirm existing window terrain remains aligned. Toggle Multiscreen off/on and confirm its prior behavior is preserved.
7. Launch once through `DesktopPet.exe` and once through `Start-DesktopPet.cmd`. Confirm bundled notices are present and no credential, network login, Steam client, or Steam SDK is requested.

Steamworks SDK/API, store services, achievements, accounts, telemetry, and credentials are intentionally out of scope. They require a separate future issue and license/security review.
