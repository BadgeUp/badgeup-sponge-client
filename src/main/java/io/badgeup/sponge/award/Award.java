package io.badgeup.sponge.award;

import io.badgeup.sponge.BadgeUpSponge;
import org.json.JSONObject;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public abstract class Award {

    protected BadgeUpSponge plugin;
    public String name;
    public String description;
    public JSONObject data;

    public Award(BadgeUpSponge plugin, JSONObject award) {
        this.plugin = plugin;
        this.name = award.getString("name");

        if (!award.isNull("description")) {
            this.description = award.getString("description");
        } else {
            this.description = null;
        }

        this.data = award.getJSONObject("data");
    }

    public abstract boolean awardPlayer(Player player);

    public void notifyPlayer(Player player) {
        player.sendMessage(Text.of(TextColors.GREEN, "You have redeemed ",
                Text.builder(this.name).color(TextColors.GOLD)
                        .onHover(TextActions.showText(Text.of(TextColors.GOLD, this.description))).build(),
                TextColors.GREEN, "!"));
    }

}
