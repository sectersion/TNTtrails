# TNTtrails

Figure out where your TNT shots are actually going!

TNTtrails is a client-side [Fabric](https://fabricmc.net/) mod for Minecraft that draws a fading trail behind every primed TNT entity, so you can see the exact arc it flew before it detonated. Each explosion is also marked in-world for a few seconds afterward. Handy for dupers, cannon builders, and anyone tuning TNT-based redstone contraptions who's tired of guessing where a shot landed.

## Features

- Renders a smoothed, color-graded trail (red → orange) along each TNT entity's flight path
- Marks explosion locations with a short-lived cuboid highlight
- Trails automatically fade and expire after ~15 seconds
- Purely client-side — no server installation required, safe to use on any server that allows client mods

## Requirements

- Minecraft 26.2
- [Fabric Loader](https://fabricmc.net/use/) 0.19.3+
- [Fabric API](https://modrinth.com/mod/fabric-api)
- Java 25+

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.2.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) and place it in your `mods` folder.
3. Download the latest TNTtrails release (or build it yourself, see below) and place the jar in your `mods` folder.
4. Launch the game with the Fabric profile.

## Building from source

```bash
git clone https://github.com/sectersion/TNTtrails.git
cd TNTtrails
./gradlew build
```

The compiled jar will be in `build/libs/`.

For IDE setup, see the [Fabric documentation](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up).

## How it works

A tracker keeps a short-lived history of every TNT entity's position, sampled each tick and pruned after 15 seconds. On each client render pass, the mod draws smoothed line segments between consecutive positions, colored by how far along the trail (and how fresh) each segment is, plus a translucent marker where each explosion occurred.

## License

Released under [CC0-1.0](LICENSE) — do whatever you want with it.
