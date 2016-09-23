package io.badgeup.sponge;

import java.util.Base64;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.service.AchievementPersistenceService;
import io.badgeup.sponge.service.AwardPersistenceService;

public class PostEventsRunnable implements Runnable {

	private BadgeUpSponge plugin;

	public PostEventsRunnable(BadgeUpSponge plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {

		Config config = BadgeUpSponge.getConfig();

		// build the base API URL
		String baseURL = "";

		if (!config.getBadgeUpConfig().getBaseAPIURL().isEmpty()) {
			// override other config settings with this base URL
			baseURL = config.getBadgeUpConfig().getBaseAPIURL();
		} else {
			// region provided
			baseURL = "https://api." + config.getBadgeUpConfig().getRegion() + ".badgeup.io/v1/apps/";
		}

		String appId = Util.parseAppIdFromAPIKey(config.getBadgeUpConfig().getAPIKey()).get();

		plugin.getLogger().info("Started BadgeUp event consumer");

		try {
			while (true) {
				final BadgeUpEvent event = BadgeUpSponge.getEventQueue().take();

				HttpResponse<JsonNode> response;
				try {
					response = Unirest.post(baseURL + appId + "/events")
							.body(event.build())
							.asJson();
				} catch(Exception e) {
					plugin.getLogger().error("Could not connect to BadgeUp API!");
					continue;
				}

				
				if (response.getStatus() == 413) {
					System.out.println("Event too large: " + event.build().getString("key"));
					continue;
				}
				final JSONObject body = response.getBody().getObject();
				final JSONArray achievementProgress = body.getJSONArray("progress");
				achievementProgress.forEach(progressObj -> {
					JSONObject progress = (JSONObject) progressObj;
					if (!(progress.getBoolean("complete"))) { // &&
																// progress.getBoolean("isNew")
						return;
					}
					final Optional<Player> subjectOpt = Sponge.getServer().getPlayer(event.getSubject());
					final JSONObject achievement = progress.getJSONObject("achievement");

					AwardPersistenceService awardPS = Sponge.getServiceManager().provide(AwardPersistenceService.class).get();
					if(achievement.get("awards") != null) {
						achievement.getJSONArray("awards").forEach(award -> {
							awardPS.addPendingAward(event.getSubject(), (JSONObject) award);
						});
					}

					if (!subjectOpt.isPresent()) {
						AchievementPersistenceService achPS = Sponge.getServiceManager()
								.provide(AchievementPersistenceService.class).get();
						achPS.addUnpresentedAchievement(event.getSubject(), achievement);
					} else {
						BadgeUpSponge.presentAchievement(subjectOpt.get(), achievement);
					}

				});
			}
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
}
