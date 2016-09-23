# Configuration

The BadgeUp Sponge client provides many configurable properties to tweak various aspects of the BadgeUp integration with Minecraft.

## Default Configuration

This is the default configuration generated in `config/badgeup-sponge-client/badgeup-sponge-client.conf`:

```hocon
badgeup {
    api-key=""
    region=useast1
}
broadcast-achievements=true
```

## Property Explanation

 * `badgeup`/`api-key`: [Required] Before using the BadgeUp Sponge client, you **must** fill in the `api-key` field with an API key with at least the `event:create` scope.
 * `badgeup`/`region`: [Optional] The BadgeUp region to send data to. Defaults to `useast1`.
 * `broadcast-achievements`: [Optional] When a player earns an achievement, this determines whether the achievement announcement is sent to all players or just the one that earned the achievement.
