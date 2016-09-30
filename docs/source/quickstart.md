# Quickstart

The BadgeUp Sponge client is [hosted on GitHub](https://github.com/BadgeUp/sponge-client). If you encounter any problems or find any bugs, please [submit an issue](https://github.com/BadgeUp/sponge-client/issues/new).

The BadgeUp Sponge Client is built on SpongeAPI v5.0.0. Make sure you install a compatible version of Sponge.

## Installation
1. Set up a Sponge server as detailed [here](https://docs.spongepowered.org/master/en/server/getting-started/implementations/index.html).
1. Download the latest version of the BadgeUp Sponge Client [here](https://github.com/BadgeUp/sponge-client/releases/latest).
1. Place the `badgeup-sponge-client.jar` file in the `mods` directory of your server.

## Configuration
After starting the Sponge server with the BadgeUp Sponge client installed, a default configuration file will be generated (see the `config` directory).

1. Create a new BadgeUp API key with the following scopes:
 - `event:create`
 - `achievement:read`
 - `earnedachievement:read`
 - `award:read`
 - `award:create`
 - `criterion:create`
 - `achievement:create`
1. Copy the API key to the `api-key` field of the configuration file.
1. Restart the server.

Read the complete configuration documentation [here](https://docs.badgeup.io/#/sponge-client/configuration).

## Demo Achievements
Once the server is started, run the `/badgeup init` command to pre-load your application with Sponge achievements.

### Meat Lover

#### How to obtain
Eat one of each of the following foods:
 * Raw Porkchop or Cooked Porkchop
 * Raw Chicken or Cooked Chicken
 * Raw Mutton or Cooked Mutton
 * Raw Beef or Steak
 * Raw Rabbit or Cooked Rabbit
 
#### Awards
* 64 Steak

### Vegematic

#### How to obtain
Eat one of each of the following foods:
 * Apple or Golden Apple
 * Mushroom Stew
 * Melon
 * Carrot or Golden Carrot
 * Potato or Baked Potato or Poisonous Potato
 * Beetroot or Beetroot Soup
 
#### Awards
 * Golden Apple
 * Golden Carrot

### Green Thumb

#### How to obtain
Plant 10 saplings
 
#### Awards
 * Rainbow Sheep

### Pyro

#### How to obtain
Light a fire
 
#### Awards
 * 10 TNT
 * Resistance Potion Effect for 2 minutes
 
### Lumberjack

#### How to obtain
Chop 50 logs

#### Awards
* "Chainsaw" Diamond Axe
 - Efficiency 5
 - Unbreaking 3 
