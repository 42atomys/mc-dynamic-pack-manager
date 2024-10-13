<p align="center">
  <h1 align="center">Dynamic Pack Refresher</h1>
</p>    
<p align="center">
  <a href="https://modrinth.com/mod/dynamic-pack-manager" target="_blank"><img src="https://img.shields.io/modrinth/dt/dynamic-pack-manager?style=flat&amp;logo=modrinth&amp;label=Modrinth%20Download&amp;link=https%3A%2F%2Fmodrinth.com%2Fmod%2Fdynamic-pack-manager" alt="Modrinth Downloads"></a>
  <a href="https://modrinth.com/mod/dynamic-pack-manager" target="_blank"><img src="https://img.shields.io/modrinth/game-versions/dynamic-pack-manager?style=flat&amp;logo=modrinth&amp;label=Modrinth%20Game%20Version&amp;link=https%3A%2F%2Fmodrinth.com%2Fmod%2Fdynamic-pack-manager" alt="Modrinth Game Version"></a>
  <a href="https://modrinth.com/mod/dynamic-pack-manager" target="_blank"><img src="https://img.shields.io/modrinth/v/dynamic-pack-manager?style=flat&amp;logo=modrinth&amp;label=Modrinth%20Version&amp;link=https%3A%2F%2Fmodrinth.com%2Fmod%2Fdynamic-pack-manager" alt="Modrinth Version"></a>
  <br />
  <img src="https://img.shields.io/github/stars/42atomys/mc-dynamic-pack-manager?style=flat&logo=github&color=blueviolet" alt="GitHub Repo stars">
  <img src="https://img.shields.io/github/contributors/42Atomys/mc-dynamic-pack-manager?style=flat&logo=github&color=blueviolet" alt="GitHub contributors">
  <a href="https://github.com/sponsors/42atomys" target="_blank"><img src="https://img.shields.io/github/sponsors/42Atomys?style=flat&logo=github&color=blueviolet" alt="GitHub Repo sponsors"></a>
</p>

# Overview

Dynamic Pack Manager is a Minecraft mod that allows server administrators to refresh the resource pack on a client's game without requiring the client to relog. This is particularly useful for servers that frequently update their resource packs.

# Usage

The mod provides commands to manage all resource packs on the server, either for all players or specific players. These commands can be executed from the server console or by players with the appropriate permissions.

Key behaviors of the mod:

- When a player joins the server, the mod checks if they have the required resource pack installed. If not, the mod sends the resource pack to the player.
- If the resource pack is mandatory, the player will be kicked from the server until the resource pack is installed.
- If the resource pack is optional, the player can join the server without it.
- When the resource pack is updated, an admin can send the updated pack to all connected players or specific ones.

The UUID of the mod is used by the client to determine if the resource pack is already installed. If the UUID matches the one already installed, the client will not re-download the pack, preventing unnecessary downloads.

The mod comes with one default dynamic pack, the `dynamicpack_languages` resource pack. This pack contains the language files for the mod. It is required to use the mod, and while it can be removed, doing so will disable translations for the mod commands.

## Commands

### `/dynamicpack list`

Lists all resource packs currently available on the server.

### `/dynamicpack add <name> <url> <required> (message)`

Adds a new resource pack to the server:
- `name`: The name of the dynamic pack.
- `url`: The URL of the resource pack. The URL is verified to ensure it points to a valid pack (syntax errors in the pack itself are not checked).
- `required`: A boolean that specifies whether the resource pack is required to join the server.
- `message`: An optional message displayed to players when the resource pack is added on their first join.

### `/dynamicpack remove <name>`

Removes a resource pack from the server.

### `/dynamicpack update <name> [name|required|message] <value>`

Updates a resource pack on the server:
- `name`: The name of the dynamic pack.
- `name|required|message`: The field to update.
- `value`: The new value for the specified field.

### `/dynamicpack send <targets> <name> (message)`

Sends a resource pack to a specific player or group:
- `targets`: The player(s) to send the resource pack to (use `@a` to send to all players).
- `name`: The name of the dynamic pack.
- `message`: An optional message shown to players if they don’t already have the resource pack.

### `/dynamicpack reloadconfig`

Reloads the mod's configuration file. *Useful when making manual changes to the config file instead of using commands.*

# Support and Sponsorship

If you enjoy using this mod and would like to support its development, please consider sponsoring via [GitHub Sponsors](https://github.com/sponsors/42atomys) or [Patreon](https://patreon.com/42atomys). Your support helps improve and maintain this project, enabling more frequent updates and new features.
