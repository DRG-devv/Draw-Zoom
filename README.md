# Draw Zoom

[

![Modrinth](https://img.shields.io/badge/Modrinth-Draw%20Zoom-1bd96a)

](https://modrinth.com/mod/draw-zoom)

Zoom in while drawing a bow or trident, with adjustable strength, speed, and trident charge time.

## Features

- Zooms smoothly as you draw a bow or trident, then returns to normal on release
- Adjustable zoom strength, zoom speed, and trident charge time
- Settings screen in Mod Menu, with info boxes explaining each setting
- Your FOV setting is respected and never changed
- Client-side only, so no server install is needed

## Requirements

- Minecraft Java Edition 1.21.11
- [Fabric Loader](https://fabricmc.net/)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Mod Menu](https://modrinth.com/mod/modmenu) (optional, for the settings screen)

## Installation

1. Download the zip from the [latest release](../../releases/latest) and extract it.
2. Place `draw-zoom-1.0.0.jar` in your `.minecraft/mods` folder, along with Fabric API.
3. Launch the game with Fabric 1.21.11.

Or download it directly from [Modrinth](https://modrinth.com/mod/draw-zoom).

## Settings

Open Mod Menu, select **Draw Zoom**, and tap the settings button. You can also edit `.minecraft/config/bowzoom.properties` directly.

- **Zoom strength:** How far the view narrows at full draw. 1.0 is no zoom, lower is stronger.
- **Zoom speed:** How fast the zoom moves in and out. Higher is faster.
- **Trident charge ticks:** How long you hold a trident before it reaches full zoom. Lower is faster.

## License

MIT
