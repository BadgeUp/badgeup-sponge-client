# Events

All events sent to BadgeUp by the Sponge client are derived directly from events fired from Sponge. Note that only events caused by players will be sent.

## Event Keys

Event keys are derived from the class name of the Sponge event causing the event to be sent. From the class name (with child classes), the key is determined by replacing all periods with colons, removing "Event", and converting to lowercase. For example, the Sponge event `InteractBlockEvent.Primary` would produce a BadgeUp event key of `interactblock:primary`.

In certain cases, extra commonly-used information is appended to the end of event keys for convenience:

* `changeblock:<break|place>:<block type>`: When breaking or placing blocks, the block's ID is appended to the key to allow you to easily keep track of block change metrics on a per-block basis.
  * Ex: `changeblock:break:minecraft:dirt`
* `useitemstack:<finish|stop>:<item type>`: When using an item stack, the item's ID is appended to the key to allow you to easily keep track of item usage metrics on a per-item basis.
  * Ex: `useitemstack:finish:minecraft:mutton`
  * Ex: `useitemstack:stop:minecraft:bow`
* `dropitem:dispense:<item type>`: When dropping an item, the item's ID is appended to the key to allow you to easily keep track of item drop metrics on a per-item basis.
  * Ex: `dropitem:dispense:minecraft:gold_ingot`
* `changeinventory:pickup:<item type>`: When picking up an item, the item's ID is appended to the key to allow you to easily keep track of item collection metrics on a per-item basis.
  * Ex: `changeinventory:pickup:minecraft:gold_ingot`
* `destructentity:death:<entity category>:<entity ID>`: When killing entities, their category ("player", "boss", "hostile", or "passive") as well as their ID (UUID for players, `minecraft:*` for all others) will be appended to the event key.
  * Ex: `destructentity:death:passive:minecraft:chicken`
* `spawnentity:<entity ID>`: When spawning entities (like throwing eggs or shooting arrows), the entity ID is appended to the event key
  * Ex: `spawnentity:minecraft:egg`

## Disabled Events

There are several Sponge events that do not directly result in a BadgeUp event being produced in the interest of reducing redundancy or increasing performance:

* `MoveEntityEvent`: Since this event can be fired several times in quick succession, it is not automatically sent to BadgeUp. Instead, the [`distance`](#-distance-) event described below should be used instead to track player movement.

## Auxiliary Events

There are several events we provide for convenience or in substitution for a disabled Sponge event.

### `distance`

Tracks distance in blocks traveled by a player.

**Sample Event Data**:

``` json
{
    "distance": 20,
    "path": [
        {
            "x": 0,
            "y": 0,
            "z": 0
        }, {
            "x": 0,
            "y": 1,
            "z": 0
        }
        ...
    ]
}
```

### `death:<damage type>`

Tracks when a player is killed. The event key includes how the player was killed, which can be any of:
* attack
* contact
* custom
* drown
* explosive
* fall
* fire
* generic
* hunger
* magic
* projectile
* suffocate
* void
* sweeping_attack
* magma
