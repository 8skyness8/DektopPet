# Fork notes

This is a fork of [Kilkakon's fork](https://kilkakon.com/shimeji/) of
[Shimeji-ee](https://code.google.com/archive/p/shimeji-ee/) that updates the runtime from JRE 6 to JDK 25.
It also contains:

* Bug fixes
* More detailed log messages
* Fixes in the default mascot action/behavior XML files
* More documentation (including comments and documentation from the original Shimeji, now translated through a better
  Google Translate than before)
* Updated dependencies
* Formatting fixes
* Proper DPI scaling
* and a lot more that I don't have the energy to list.

I have also switched the project from Ant to Maven, and ported the `launch4j.xml` file from the original Shimeji to the
Launch4j Maven Plugin.

It also contains Linux support based on code from [asdfman's linux-shimeji](https://github.com/asdfman/linux-shimeji),
macOS support based on code from [nonowarn's shimeji4mac](https://github.com/nonowarn/shimeji4mac), and some inspiration
from [LavenderSnek's ShimejiEE-cross-platform](https://github.com/LavenderSnek/ShimejiEE-cross-platform).

The rest of this file is the original README, albeit ported to Markdown and with slightly more up-to-date information.

# Shimeji-ee: Shimeji English Enhanced

Shimeji-ee is a desktop mascot for Windows, macOS, and Linux that freely wanders and plays around the screen. The mascot
is very configurable; its actions are defined through XML and its animations/images can be (painstakingly) customized.
Shimeji was originally created by Yuki Yamada of
[Group Finity](https://web.archive.org/web/20160901003054/http://www.group-finity.com/Shimeji/). This branch of the
original Shimeji project not only translates the program/source to English, but adds additional enhancements to
Shimeji by Kilkakon and other members of the community.

## Contents

1. [Links](#links)
2. [Requirements](#requirements)
3. [How to Start](#how-to-start)
4. [Basic Configuration](#basic-configuration)
5. [Advanced Configuration](#advanced-configuration)
6. [How to Quit](#how-to-quit)
7. [How to Uninstall](#how-to-uninstall)
8. [Licensing](#licensing)
9. [Trouble Shooting](#trouble-shooting)

## Links

* [Kilkakon's Shimeji homepage](https://kilkakon.com/shimeji/)
* [linux-shimeji repository](https://github.com/asdfman/linux-shimeji)
* [shimeji4mac repository](https://github.com/nonowarn/shimeji4mac)
* [ShimejiEE-cross-platform repository](https://github.com/LavenderSnek/ShimejiEE-cross-platform)
* [Shimeji-ee homepage](https://code.google.com/archive/p/shimeji-ee/)
* [Shimeji homepage (archive)](https://web.archive.org/web/20160901003054/http://www.group-finity.com/Shimeji/)
* [Shimeji mirror download](https://www.vector.co.jp/soft/winnt/amuse/se476479.html)

## Requirements

* Windows Vista or newer / macOS / Linux (X11)
* Java 25 or newer

## How to Start

1. Open the Shimeji-ee JAR file (`Shimeji-ee.jar`).
    * On Windows, you can alternatively open `Shimeji-ee.exe`.
    * On macOS/Linux, you can alternatively open `Shimeji-ee`.
2. Right-click the tray icon for general options.
3. Right-click a Shimeji for options relating to it.

For a tutorial on how to get Shimeji running, watch [this video](https://www.youtube.com/watch?v=S7fPCGh5xxo).

You can also watch the [FAQ](https://www.youtube.com/watch?v=A1y9C1Vbn6Q) if you encounter problems.

You can also join Kilkakon's [Discord server](https://discord.gg/dcJGAn3).

## Basic Configuration

If you want multiple Shimeji types, you must have multiple image sets. Basically, you put different folders with the
correct Shimeji images under the `img` directory.

For example, if you want to add, say, a new Batman Shimeji:

1. Create an `img/Batman` folder.
2. You must have an image set that mimics the contents of `img/Shimeji`. Create and put new versions of `shime1.png` -
   `shime46.png` (with Batman images, of course) in the `img/Batman` folder. The filenames must be the same as the
   `img/Shimeji` files. Refer to `img/Shimeji` for the proper character positions.
3. Start Shimeji-ee. Now Shimeji and Batman will drop. Right-click Batman to perform Batman specific options. Pressing
   "Call Shimeji" in the tray icon menu will randomly create and add either Shimeji or Batman.

When Shimeji-ee starts, one Shimeji for every image set in the `img` folder will be created. If you have too many image
sets, a lot of your computer's memory will be used... so be careful.

Shimeji-ee will ignore all the image sets that are in the `img/unused` folder, so you can hide image sets in there.
There is also a tool, Image Set Chooser, that will let you select image sets at run time. It remembers previous options
via the `conf/settings.properties` file. Don't choose too many at once.

For more information, read through the configuration files in `conf/`. Most options are somewhat complicated, but it's
not too hard to limit the total number of Shimeji or to turn off certain behaviors (hint: set frequency to 0).

## Advanced Configuration

All configuration files are located in the `conf` folders. In general, none of these should need to be touched.

The `logging.properties` file defines how logging errors is done.

The `actions.xml` file specifies the different actions Shimeji can do. When listing images, only include the file name.
More detail on this file will hopefully be added later.

The `behaviors.xml` file specifies when Shimeji performs each action. More detail on this file will hopefully be added
later.

The `settings.properties` file details which Shimeji are active as well as the windows with which they can interact.
These settings can be changed using the program itself.

Each type of Shimeji is configured through:

1. An image set. This is located in `img/[NAME]`. The image set must contain all image files specified in the actions
   file.
2. An actions file. Unless `img/[NAME]/conf/actions.xml` or `conf/[NAME]/actions.xml` exists, `conf/actions.xml` will
   be used.
3. A behaviors file. Unless `img/[NAME]/conf/behaviors.xml` or `conf/[NAME]/behaviors.xml` exists, `conf/behaviors.xml`
   will be used.

When Shimeji-ee starts, one Shimeji for every image set in the `img` folder will be created. If you have too many image
sets, a lot of your computer's memory will be used... so be careful.

Shimeji-ee will ignore all the image sets that are in the `img/unused` folder, so you can hide image sets in there.
There is also a tool, Image Set Chooser, that will let you select image sets at run time. It remembers previous options
via the `conf/settings.properties` file. Don't choose too many at once.

The Image Set Chooser looks for the `shime1.png` image. If it's not found, no image set preview will be shown. Even if
you're not using an image named `shime1.png` in your image set, you should include one for the Image Set Chooser's sake.

Editing an existing configuration is fairly straightforward, but writing a brand-new configuration file is very
time-consuming and requires a lot of trial and error. Hopefully someone will write a guide for it someday, but until
then, you'll have to look at the existing `conf` files to figure it out. Basically, for every behavior, there must be a
corresponding action. Actions and behaviors can be a sequence of other actions or behaviors.

The following actions must be present for the `actions.xml` to be valid:

* ChaseMouse
* Fall
* Dragged
* Thrown

The following behaviors must be present for the `behaviors.xml` to be valid:

* ChaseMouse
* Fall
* Dragged
* Thrown

The icon used for the system tray is `img/icon.png`.

## How to Quit

Right-click the tray icon of Shimeji-ee, and select "Dismiss All".

## How to Uninstall

Delete the unzipped folder.

## Building

The upstream Windows build baseline uses a 64-bit JDK 25 and Maven 3.9. The JDK, rather than a JRE, is required both to
compile the project and by the generated Windows launcher.

From PowerShell or Command Prompt in the repository root, confirm the active tools and run the Maven verification path:

```text
java -version
mvn -version
mvn clean verify
```

`mvn clean verify` compiles the application, runs its automated tests, creates the executable launcher, and assembles
the distribution. A successful build produces these launchable artifacts:

* `target/Shimeji-ee.jar`
* `target/Shimeji-ee.exe`
* `target/Shimeji-ee_1.0.22.zip` (the distributable archive, including the runtime dependencies and configuration)

The version in the ZIP filename follows the project version in `pom.xml` and will change when the project version is
updated. Do not move the JAR or EXE out of the extracted distribution: the `lib`, `conf`, and `img` directories are
required at runtime.

### Windows baseline smoke check

On Windows 10 or Windows 11 with `JAVA_HOME` set to the JDK 25 installation:

1. Extract `target/Shimeji-ee_1.0.22.zip` to a writable directory.
2. Start `Shimeji-ee.exe`. If launcher diagnostics are needed, run `java -jar Shimeji-ee.jar` from a terminal in the
   same directory instead.
3. Confirm the tray icon appears and the bundled mascot appears on the desktop.
4. Confirm the mascot can be dragged and released.
5. Right-click the tray icon and choose **Dismiss All**; confirm the mascot and application exit.

These GUI checks require an interactive Windows desktop and are not covered by a headless Maven build.

IntelliJ IDEA is optional. If using it, open this directory as a Maven project and run the Maven `verify` lifecycle
goal; the outputs are the same files under `target/`.

## Licensing

Programmers may feel free to use the source. The Shimeji-ee source is under the New BSD license.

Shimeji by Yuki Yamada is licensed under the zlib/libpng license.

## Trouble Shooting

For a tutorial on how to get Shimeji running, watch [this video](https://www.youtube.com/watch?v=S7fPCGh5xxo).

You can also watch the [FAQ](https://www.youtube.com/watch?v=A1y9C1Vbn6Q) if you encounter problems.

You can also join Kilkakon's [Discord server](https://discord.gg/dcJGAn3).

Shimeji-ee takes a LOT of time to start if you have a lot of image sets, so give it some time. Try moving all but one
image set from the `img` folder to the `img/unused` folder to see if you have a memory problem.

If the Shimeji-ee icon appears, but no Shimeji appear:

1. Make sure you have the newest version of Shimeji-ee.
2. Make sure you only have image set folders in your `img` directory.
3. Make sure you have Java 25 or newer on your system.
4. If you're somewhat computer savvy, you can try running Shimeji-ee from the command line. Navigate to the Shimeji-ee
   directory and run this command: `"C:\Program Files\Java\jdk-25\bin\java" -jar Shimeji-ee.jar`
5. Try checking the log (`ShimejieeLogX.log`) for errors. If you find a bug (which is very likely), report it on
   Kilkakon's Discord server.
