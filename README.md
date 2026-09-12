<img alt="Pylon logo" src="src/main/resources/assets/pylon/icon.png" width="128px" />

# Pylon
Pylon allows legacy Minecraft versions to run with LWJGL 3. It replaces the outdated windowing system with SDL 3, and serves as a library for mods.

## Downloads
* [Latest Release](https://github.com/notdevcody/pylon/releases/latest)
* [Latest Nightly](https://nightly.link/notdevcody/pylon/workflows/nightly/main/pylon-nightly.zip)

## Features
### Fixes
- Lag from high-polling-rate mice
- Keys and mouse buttons getting stuck
- Crash when resizing the window on macOS

### Improvements
- System IME support
- More precise mouse input
- Proper clipboard handling
- Reduced display / input overhead
- Dark mode for the window title bar
- Better fullscreen and window resizing

## Developers
Pylon is published to the Clover Client Maven, and can be added as a dependency like so:
```kts
repositories {
    maven("https://maven.cloverclient.com/releases")
}

dependencies {
    modImplementation("pl.tomgirl:pylon:${version}")
}
```
