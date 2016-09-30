# Quickstart

The BadgeUp Sponge client is [hosted on GitHub](https://github.com/BadgeUp/sponge-client). If you encounter any problems or find any bugs, please [submit an issue](https://github.com/BadgeUp/sponge-client/issues/new).

The BadgeUp Sponge Client is built on SpongeAPI v5.0.0. Make sure you install a compatible version of Sponge.

## BadgeUp Setup
1. [Create a BadgeUp account](https://www.badgeup.io/)
1. Create a new BadgeUp API key with the following scopes:
 - `event:create`
 - `achievement:read`
 - `earnedachievement:read`
 - `award:read`
 - `award:create`
 - `criterion:create`
 - `achievement:create`

## Installation
1. Set up a Sponge server as detailed [here](https://docs.spongepowered.org/master/en/server/getting-started/implementations/index.html).
1. Download the latest version of the BadgeUp Sponge Client [here](https://github.com/BadgeUp/sponge-client/releases/latest).
1. Place the `badgeup-sponge-client.jar` file in the `mods` directory of your server.
1. Start the server to let configuration files generate. The BadgeUp Sponge Client configuration file is at `config/badgeup-sponge-client/badgeup-sponge-client.conf`.
1. Copy the BadgeUp API key (created above) to the `api-key` field of the configuration file.
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
