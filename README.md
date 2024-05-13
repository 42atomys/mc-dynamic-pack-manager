# Fabric Resource Pack Refresher

## Overview

Fabric Resource Pack Refresher is a mod for Minecraft that allows server administrators to refresh the resource pack on a client's game without the need for the client to relog. This can be particularly useful for servers that frequently update their resource packs.

## Requirements

- Minecraft version 1.20.1 or higher
- Fabric Loader version 0.11.3 or higher
- Fabric API (compatible version with the Minecraft version being used)

## Usage Commands

The mod provides a command to refresh the resource pack for all players or specific players. The commands can be executed from the server console or by players with the appropriate permissions.

- To refresh the resource pack for all players: `/rpr <packname>`
- To refresh the resource pack for a specific player: `/rpr <packname> <player>`

For building, running, and other operations, refer to the `Makefile` commands:

- `make build` to build the project.
- `make run` to run the project.
- `make clean` to clean the build artifacts.
- `make test` to run the tests.
- `make package` to package the mod.

## Configuration

The mod's configuration can be found in `src/main/java/codes/atomys/resourcepackrefresher/ResourcePackConfig.java`. It allows for specifying multiple resource packs with options such as:

- `packname`: The name of the resource pack.
- `url`: The URL where the resource pack can be downloaded.
- `required`: Whether the resource pack is required.
- `hasPrompt`: Whether the player should be prompted to download the resource pack.
- `message`: The message to display in the prompt.

Example configuration:

```json5
{
	// Should all criteria be automatically revoked next time the command is executed.
	"autoRevoke": true,
	// List of all the resourcepacks configurations to use
	"packs": [
		{
			"packname": "atomys",
			"url": "https://example.com/resource_pack.zip",
			"required": true,
			"hasPrompt": true,
			"message": "You need to download the resource pack to play on this server !"
		}
	]
}
```


## Development with DevContainer

This project supports development with a DevContainer to provide a consistent development environment. To get started:

1. Ensure you have Docker installed and running on your system.
2. Install the Visual Studio Code and the Remote - Containers extension.
3. Open the project folder in Visual Studio Code, and when prompted, click on "Reopen in Container". Alternatively, you can open the Command Palette (`Ctrl+Shift+P`) and select "Remote-Containers: Open Folder in Container...".

The DevContainer configuration is located in `.devcontainer/devcontainer.json`.

## How to update to the new minecraft version

1. Go to https://fabricmc.net/develop/ 
2. Take the new values for `gradle.properties`
3. Test build locally with `make build`
4. Push the changes

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.
