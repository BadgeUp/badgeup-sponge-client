# Quickstart

The BadgeUp Sponge client is [hosted on GitHub](https://github.com/BadgeUp/sponge-client). If you encounter any problems or find any bugs, please [submit an issue](https://github.com/BadgeUp/sponge-client/issues/new).

## Installation
1. Set up a Sponge server as detailed [here](https://docs.spongepowered.org/master/en/server/getting-started/implementations/index.html).
1. Download the latest version of the BadgeUp Sponge Client [here](https://github.com/BadgeUp/sponge-client/releases/latest).
1. Place the `badgeup-sponge-client.jar` file in the `mods` directory of your server.

## Configuration
After starting the Sponge server with the BadgeUp Sponge client installed, a default configuration file will be generated (see the `config` directory).

1. Create a new BadgeUp API key with these scopes: `event:create`, `achievement:read`, `earnedachievement:read`, and `award:read`.
1. Copy the API key to the `api-key` field of the configuration file.

Read the complete configuration documentation [here](https://docs.badgeup.io/#/sponge-client/configuration).
