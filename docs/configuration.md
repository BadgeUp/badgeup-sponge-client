# Configuration

The BadgeUp Sponge client provides many configurable properties to tweak various aspects of the BadgeUp integration with Minecraft.

## Default Configuration

This is the default configuration generated in `config/badgeup/badgeup.conf`:

```hocon
badgeup {
    api-key=""
    region=useast1
}
broadcast-achievements=true
fireworks=true
sounds {
    enabled=false
    # See http://minecraft.gamepedia.com/Sounds.json for all sounds
    sound="minecraft:entity.experience_orb.pickup"
}
```

### `badgeup`/`api-key` [Required]
Before using the BadgeUp Sponge client, you **must** fill in the `api-key` field with an API key with at least the following scopes:

* `event:create`
* `achievement:read`
* `award:read`
* `progress:read`

The following scopes are optional, but you must have them if you run the `/badgeup init` command (with the `badgeup.admin.init` permission) to initialize your BadgeUp account with demo Minecraft achievements.

* `award:create`
* `criterion:create`
* `achievement:create`

### `badgeup`/`region` [Optional]
The BadgeUp region to send data to. Defaults to `useast1`.

### `broadcast-achievements` [Optional]
When a player earns an achievement, this determines whether the achievement announcement is sent to all players or just the one that earned the achievement.

### `fireworks` [Optional]
When a player earns an achievement, this determines whether fireworks will be spawned around the player.

### `sounds`/`enabled` [Optional]
When a player earns an achievement, this determines whether sound effects will be played at the player's location.

### `sounds`/`sound` [Optional]
The sound to be played if `sounds`/`enabled` is set to `true`. See all possible sounds [here](http://minecraft.gamepedia.com/Sounds.json) (the first column). Make sure you put `minecraft:` in front of the sound ID.
