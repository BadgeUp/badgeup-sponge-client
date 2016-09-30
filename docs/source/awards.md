# Awards

The BadgeUp Sponge client has the ability to interpret and grant four different types of awards:

1. [Item Awards](#item-awards)
2. [Monetary Awards](#monetary-awards)
3. [Entity Awards](#entity-awards)
4. [Potion Effect Awards](#potion-effect-awards)

Every award's data includes at least the following fields:

* `type`: The type of award; can be either `item`, `monetary`, `entity`, or `potion`.
* `autoRedeem`: Optional boolean. Determines whether the award is automatically redeemed upon receipt of an achievement.

## Text Formatting

Many awards allow you to specify custom text such as the name of an item or entity. When used, they can be either a string formatted [as described here](http://minecraft.gamepedia.com/Formatting_codes), using the ampersand (&) as the formatting character; or a JSON object following the schema [described here](http://minecraft.gamepedia.com/Commands#Raw_JSON_Text).

## Item Awards

This type of award gives the player an `ItemStack` defined by the following award data:

* `type`: `item`
* `itemType`: Required. Must be the ID of one of the items described [here](http://minecraft-ids.grahamedgecombe.com/).
* `quantity`: Optional (defaults to 1). Must be a positive integer of at most 64.
* `displayName`: Optional. Must be of the format [described above](#text-formatting).
* `lore`: Optional. This is an array of text of the format [described above](#text-formatting).
* `enchantments`: Optional. This is an array of objects following the following schema:
 * `id`: Required. Must be of the form `minecraft:enchantment` and be one of the enchantments [listed here](http://minecraft.gamepedia.com/Enchanting/ID).
 * `level`: Required. Must be a positive integer.
* `durability`: Optional. Must be an integer.
* `color`: Optional. Must be one of: `white`, `orange`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, or `black`.

### Example Item Award Data

```json
{
    "type": "item",
    "itemType": "minecraft:diamond_sword",
    "displayName": "&6The Best Sword",
    "lore": [
        "&4It will destroy your enemies",
        "&5And wear out really quickly"
    ],
    "enchantments": [
        {
            "id": "minecraft:sharpness",
            "level": 10
        }
    ],
    "durability": 1
}
```

```json
{
    "type": "item",
    "itemType": "minecraft:wool",
    "quantity": 10,
    "displayName": "&6The Best Wool",
    "lore": [
        "&3It looks awesome",
        "&2Really colorful"
    ],
    "color": "cyan"
}
```

## Monetary Awards

This type of award deposits some amount of money into the player's account. The award data should contain the following:

* `type`: `monetary`
* `amount`: Required. Must be positive.
* `currency`: Optional. This is the ID of a custom currency used on your server. If not specified, the default currency will be used.

### Example Monetary Award Data

```json
{
    "type": "monetary",
    "amount": 999999,
    "currency": "gems"
}
```

## Entity Awards

This type of award spawns an entity near the player in a position specified by you. The award data should contain the following:

* `type`: `entity`
* `entityType`: Required. Must be of the form `minecraft:entity` and be the ID of one of the entities listed [here](http://minecraft-ids.grahamedgecombe.com/entities).
* `displayName`: Optional. Must be of the format [described above](#text-formatting).
* `color`: Optional. Used for coloring sheep. Must be one of: `white`, `orange`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, or `black`.
* `position`: Optional (defaults to the player's position). Takes the following form, where the tilde (~) indicates a relative position.

```json
{
    "x": "~10",
    "y": "100",
    "z": "~-20"
}
```

For example, the above position would specify an x-coordinate of 10 blocks in the positive x-direction relative to the player, a y-coordinate of 100, and a z-coordinate of 20 blocks in the negative z-direction relative to the player.

### Example Entity Award Data

```json
{
    "type": "entity",
    "entityType": "minecraft:sheep",
    "position": {
        "x": "~",
        "y": "~5",
        "z": "~"
    },
    "color": "red"
}
```

## Potion Effect Awards

This type of award gives a particular potion effect to the player. The award data should contain the following:

* `type`: `potion`
* `potionEffectType`: Required. Must be one of the status effects listed [here](http://minecraft.gamepedia.com/Data_values#Status_effects).
* `duration`: Required. Must be a positive integer.
* `amplifier`: Optional (defaults to 1). This is the time in ticks (20 ticks = 1 second); must be a positive integer.

### Example Entity Award Data

```json
{
    "type": "potion",
    "potionEffectType": "minecraft:speed",
    "duration": 30000,
    "amplifier": 2
}
```
