# Quickstart

The BadgeUp Sponge client is [hosted on GitHub](https://github.com/BadgeUp/sponge-client). If you encounter any problems, find any bugs, or would like a feature, please [submit an issue](https://github.com/BadgeUp/sponge-client/issues/new).

The BadgeUp Sponge Client is built on SpongeAPI v7.0.0. Make sure you install a compatible version of Sponge.

## BadgeUp Setup
1. [Create a BadgeUp account](https://www.badgeup.io/)
1. Create a new BadgeUp API key with the following scopes:

* `event:create`
* `achievement:read`
* `award:read`
* `award:create`
* `criterion:create`
* `achievement:create`
* `progress:read`

## Installation
1. Set up a Sponge server as detailed [here](https://docs.spongepowered.org/master/en/server/getting-started/implementations/index.html).
1. Download the latest version of the BadgeUp Sponge Client [here](https://github.com/BadgeUp/sponge-client/releases/latest).
1. Place the `badgeup-sponge-client.jar` file in the `mods` directory of your server.
1. Start the server to let configuration files generate. The BadgeUp Sponge Client configuration file is at `config/badgeup/badgeup.conf`.
1. Copy the BadgeUp API key (created above) to the `api-key` field of the configuration file.
1. Reload the server (`/sponge plugins reload`).

Read the complete configuration documentation [here](https://docs.badgeup.io/sponge-client/configuration).

## Demo Achievements
Once the server is started, run `/badgeup init` (requires the `badgeup.admin.init` permission) to pre-load your application with Sponge achievements.

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
* Time Machine (sets time to day)

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
  * Efficiency 5
  * Unbreaking 3

### Moneybags

#### How to obtain
Drop a gold ingot

#### Awards
* $100,000

### One Man's Trash

#### How to obtain
Pick up 100 rotten flesh

#### Awards
* Zombie Head

### Bane of the Undead

#### How to obtain
Kill any combination of 10 skeletons and zombies

#### Awards
* "Zombie Ripper" Diamond Sword
  * Smite 5
