package io.badgeup.sponge;

import io.badgeup.sponge.service.AchievementPersistenceService;
import io.badgeup.sponge.service.AwardPersistenceService;
import io.badgeup.sponge.util.Util;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class EventWebSocketListener extends WebSocketListener {

    private BadgeUpSponge plugin;

    public EventWebSocketListener(BadgeUpSponge plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.plugin.getLogger().info("Opened event websocket");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        this.plugin.getLogger().error("Error with WebSocket:");
        t.printStackTrace();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        JSONObject body;
        try {
            body = new JSONObject(text);
        } catch (JSONException e) {
            this.plugin.getLogger().warn("Failed to parse event response: " + text);
            return;
        }

        if (body.has("error")) {
            this.plugin.getLogger().warn("Error sending event: " + body.toString());
            return;
        }

        JSONObject event = body.getJSONObject("event");

        List<JSONObject> completedAchievements = new ArrayList<>();
        body.getJSONArray("progress").forEach(progressObj -> {
            JSONObject record = (JSONObject) progressObj;
            if (record.getBoolean("isComplete") && record.getBoolean("isNew")) {
                completedAchievements.add(record);
            }
        });

        for (JSONObject record : completedAchievements) {
            try {
                Optional<JSONObject> achievementOpt = this.plugin.getResourceCache().getAchievementById(record.getString("achievementId")).get();
                System.out.println(achievementOpt.isPresent());
                if (!achievementOpt.isPresent()) {
                    continue;
                }
                JSONObject achievement = achievementOpt.get();

                AwardPersistenceService awardPS = Sponge.getServiceManager()
                        .provide(AwardPersistenceService.class).get();
                List<String> awardIds = new ArrayList<>();
                achievement.getJSONArray("awards")
                        .forEach(awardId -> awardIds.add((String) awardId));

                final Optional<Player> subjectOpt = Sponge.getServer().getPlayer(UUID.fromString(event.getString("subject")));

                for (String awardId : awardIds) {
                    Optional<JSONObject> awardOpt = this.plugin.getResourceCache().getAwardById(awardId).get();
                    if (!awardOpt.isPresent()) {
                        continue;
                    }
                    JSONObject award = awardOpt.get();

                    awardPS.increment(UUID.fromString(event.getString("subject")), awardId);

                    boolean autoRedeem = Util.safeGetBoolean(award.getJSONObject("data"), "autoRedeem").orElse(false);
                    if (subjectOpt.isPresent() && autoRedeem) {
                        // Check if the award is auto-redeemable and
                        // send the redeem command if it is
                        Sponge.getCommandManager().process(subjectOpt.get(), "redeem " + awardId);
                    }
                }

                if (!subjectOpt.isPresent()) {
                    // Store the achievement to be presented later
                    AchievementPersistenceService achPS = Sponge.getServiceManager()
                            .provide(AchievementPersistenceService.class).get();
                    achPS.increment(UUID.fromString(event.getString("subject")), achievement.getString("id"));
                } else {
                    // Present the achievement to the player
                    this.plugin.presentAchievement(subjectOpt.get(), achievement.getString("id"));
                }

            } catch (JSONException | InterruptedException | ExecutionException e) {
                this.plugin.getLogger().error("Error presenting achievements: ");
                e.printStackTrace();
            }
        }

    }

}
