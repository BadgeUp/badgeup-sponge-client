# Awards

The BadgeUp Sponge client has the ability to interpret and grant four different types of awards:

1. [Item Awards](#item-awards)
2. [Monetary Awards](#monetary-awards)
3. [Entity Awards](#entity-awards)
4. [Potion Effect Awards](#potion-effect-awards)

## Text Formatting

Many awards allow you to specify custom text, such as the name of an item or entity. When used, they can be either a string formatted [as described here](http://minecraft.gamepedia.com/Formatting_codes), using the ampersand (&) as the formatting character; or a JSON object following the schema [described here](http://minecraft.gamepedia.com/Commands#Raw_JSON_Text).

## Item Awards

This type of award gives the player an `ItemStack` defined by the following award data:

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
