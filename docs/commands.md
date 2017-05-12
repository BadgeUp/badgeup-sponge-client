# Commands

## Admin Commands

`/badgeup init`: Pre-loads your BadgeUp account with demo Minecraft achievements.

* Permission: `badgeup.admin.init`

`/badgeup debug <player name>`: Shows you what events are being sent for a particular player. This is useful for determining what events are sent for different in-game actions.

* Permission: `badgeup.admin.debug`

`/badgeup awards create inhand`: Creates an item award from the item you are holding in hand.

* Permission: `badgeup.admin.award.create`

## Player Commands

`/awards`: Lists all unclaimed awards

* Permission: `badgeup.awards.list`

`/progress` or `/achievements`: Shows progress towards all achievements

* Permission: `badgeup.progress`

 ---

`/redeem <id>`: Redeems the award with the specified ID. Not intended to be used directly by players, as they can use the UI in the `/awards` command.

* Permission: `badgeup.awards.redeem`
