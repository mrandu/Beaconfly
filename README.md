# BeaconFly

BeaconFly is a Paper server plugin that lets players fly while they have Haste II or higher. Players can opt out with the `/beaconfly` command.

## Requirements

- Java 21
- Paper 1.21.7

## Install

1. Build the project with `./mvnw.cmd clean package` on Windows, or `./mvnw clean package` on macOS/Linux.
2. Copy `target/beaconfly-1.3-rc1.jar` into the server's `plugins` directory.
3. Start or restart the server.

## Usage

- `/beaconfly` displays the plugin version and whether flight is enabled for you.
- `/beaconfly on` enables BeaconFly for you.
- `/beaconfly off` disables BeaconFly for you.
- `/beaconfly reload` reloads sound settings (console or operators with `beaconfly.reload`).

The status, `on`, and `off` commands are player-only and available to everyone by default. The `reload` command works from the console and requires the `beaconfly.reload` permission, granted to operators by default. Player opt-outs are kept in memory and reset when the plugin or server restarts; BeaconFly is enabled by default.

Flight eligibility is checked every five seconds. Creative and Spectator players are left unchanged. Flight state changes are announced in chat and play the configured sound.

## Configuration

The generated `plugins/BeaconFly/config.yml` controls the flight sound:

```yaml
flight-sound:
	sound: minecraft:block.note_block.chime
	pitch: 0.6
```

`sound` is a namespaced Minecraft sound key, and `pitch` must be between `0.5` and `2.0`. Invalid values fall back to the defaults. Run `/beaconfly reload` as an operator, or from the console, to apply changes without restarting the server.

## Build

The Maven Wrapper pins Maven 3.9.11 and downloads it when needed. Run `./mvnw.cmd clean package` on Windows, or `./mvnw clean package` on macOS/Linux. Maven resolves the Paper API dependency and packages the plugin, while `javac` compiles the Java source. The project targets Java 21; an IDE or Maven can use Minecraft's bundled JDK as its compiler.

Calling `javac` directly also requires the Paper API and its dependencies on the classpath, followed by packaging the compiled classes together with `plugin.yml`. The Paper API is provided by the server at runtime and is not bundled in the plugin JAR.

## Version history

See [CHANGELOG.md](CHANGELOG.md).

## License

BeaconFly is licensed under the GNU General Public License v3.0 or any later version (GPL-3.0-or-later). See [LICENSE](LICENSE).