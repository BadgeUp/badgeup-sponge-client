# Events

All events sent to BadgeUp by the Sponge client are derived directly from events fired from Sponge. Note that only events caused by players will be sent.

## Event Keys

Event keys are derived from the class name of the Sponge event causing the event to be sent. From the class name (with child classes), the key is determined by replacing all periods with colons, removing "Event", and converting to lowercase. For example, the Sponge event `InteractBlockEvent.Primary` would produce a BadgeUp event key of `interactblock:primary`.

In certain cases, extra commonly-used information is appended to the end of event keys for convenience:

 * `changeblock:<break|place>:<block type>`: When breaking or placing blocks, the block's ID is appended to the key to allow you to easily keep track of block change metrics on a per-block basis.

## Disabled Events

There are several Sponge events that do not directly result in a BadgeUp event being produced in the interest of reducing redundancy or increasing performance:

* `MoveEntityEvent`: Since this event can be fired several times in quick succession, it is not automatically sent to BadgeUp. Instead, the [`playerpath`](#-playerpath-) and [`distance`](#-distance-) events described below should be used instead to track player movement.

## Auxiliary Events

There are several events we provide for convenience or in substitution for a disabled Sponge event.

### `playerpath`

Tracks the path a player takes through the world.

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

### `distance`

Tracks distance in blocks traveled by a player.
