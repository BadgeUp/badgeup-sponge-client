package io.badgeup.sponge.award;

import org.json.JSONObject;
import org.spongepowered.api.entity.living.player.Player;

import io.badgeup.sponge.BadgeUpSponge;

public abstract class Award {
	
	protected BadgeUpSponge plugin;
	public String name;
	public String description;
	public JSONObject data;
	
	public Award(BadgeUpSponge plugin, JSONObject award) {
		this.plugin = plugin;
		this.name = award.getString("name");
		this.description = award.getString("description");
		this.data = award.getJSONObject("data");
	}
	
	public abstract boolean awardPlayer(Player player);
	
	public void notifyPlayer(Player player) {
		// TODO
	}
	
}
