package io.badgeup.sponge;

import java.net.URI;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import com.google.common.base.Preconditions;

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
		final Config config = BadgeUpSponge.getConfig();
		
		final String apiKey = config.getBadgeUpConfig().getAPIKey();
		Preconditions.checkArgument(!apiKey.isEmpty(), "API key must not be empty");
				
		// build the base API URL
		String baseURL = "";
		
		if (!config.getBadgeUpConfig().getBaseAPIURL().isEmpty()) {
			// override other config settings with this base URL
			baseURL = config.getBadgeUpConfig().getBaseAPIURL();
		} else {
			// region provided
			baseURL = "https://api." + config.getBadgeUpConfig().getRegion() + ".badgeup.io/v1/apps/"; 
		}
		
		// Base64 decode the API key
		final byte[] decodedKey = Base64.getDecoder().decode(apiKey);
		JSONObject keyObj = null;
		try {
			keyObj = new JSONObject(new String(decodedKey));
		} catch (Exception e) {
			plugin.getLogger().error("Please specify a valid API key.");
		}
		final String appId = keyObj.getString("applicationId");
		Preconditions.checkArgument(!appId.isEmpty(), "Application ID must not be empty");
		Preconditions.checkArgument(appId.matches("^[a-zA-Z0-9]*$"),
				"Application ID must contain only letters and numbers");
		final BlockingQueue<BadgeUpEvent> eventQueue = BadgeUpSponge.getEventQueue();

		final String authHeader = "Basic " + new String(Base64.getEncoder().encode((apiKey + ":").getBytes()));
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(URI.create(baseURL + appId + "/events"));
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);
		invocationBuilder.header("Authorization", authHeader);
		invocationBuilder.header("User-Agent", "BadgeUp_SpongeClient v1.0.0");

		plugin.getLogger().info("Started BadgeUp event consumer");

		try {
			while (true) {
				final BadgeUpEvent event = eventQueue.take();
				
				Response response;
				try {
					response = invocationBuilder
							.post(Entity.entity(event.build().toString(), MediaType.APPLICATION_JSON_TYPE));
				} catch(Exception e) {
					plugin.getLogger().error("Could not connect to BadgeUp API!");
					continue;
				}
				
				final String rawBody = response.readEntity(String.class);
				if (response.getStatus() == 413) {
					System.out.println("Event too large: " + event.build().getString("key"));
					continue;
				}
				final JSONObject body = new JSONObject(rawBody);
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
