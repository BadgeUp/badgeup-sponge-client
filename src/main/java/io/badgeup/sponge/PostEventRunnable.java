package io.badgeup.sponge;

import io.badgeup.sponge.command.executor.DebugCommandExecutor;
import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.service.AchievementPersistenceService;
import io.badgeup.sponge.service.AwardPersistenceService;
import io.badgeup.sponge.util.HttpUtils;
import io.badgeup.sponge.util.Util;
import okhttp3.Response;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostEventRunnable implements Runnable {

    private BadgeUpSponge plugin;
    private BadgeUpEvent event;

    public PostEventRunnable(BadgeUpSponge plugin, BadgeUpEvent event) {
        this.plugin = plugin;
        this.event = event;
    }

    public Text constructEventText(BadgeUpEvent event, int responseCode) {
        String playerName = "N/A";
        Optional<Player> playerOpt = Sponge.getServer().getPlayer(event.getSubject());
        if (playerOpt.isPresent()) {
            playerName = playerOpt.get().getName();
        }

        return Text.of(
                "<", playerName, "> ",
                Text.builder(event.getKey()).onHover(TextActions.showText(Text.of(event.build().toString(4)))).build(),
                " (Response: " + responseCode + ")");
    }

    @Override
    public void run() {
        this.event.setDiscardable(false);

        try (Response response = HttpUtils.post("/events", this.event.build())) {
            // If status code is 413, log that the event was too big (to be
            // looked at and slimmed down later)
            if (response.code() == 413) {
                this.plugin.getLogger().warn("Event too large: " + this.event.build().getString("key"));
                this.plugin.getLogger().debug(this.event.build().toString());
                DebugCommandExecutor.getDebugMessageChannelForPlayer(this.event.getSubject())
                        .send(Text.of(TextColors.RED, constructEventText(this.event, response.code())));
                return;
            } else if (response.code() != 201) {
                // If not 201, log the error
                this.plugin.getLogger().error("Non-201 status code response from BadgeUp. Response code: " + response.code()
                        + ". Response body: " + response.body().string());
                DebugCommandExecutor.getDebugMessageChannelForPlayer(this.event.getSubject())
                        .send(Text.of(TextColors.RED, constructEventText(this.event, response.code())));
                return;
            }

            DebugCommandExecutor.getDebugMessageChannelForPlayer(this.event.getSubject())
                    .send(Text.of(TextColors.GREEN, constructEventText(this.event, response.code())));

            final JSONObject body = HttpUtils.parseBody(response);

            List<JSONObject> completedAchievements = new ArrayList<>();
            body.getJSONArray("progress").forEach(progressObj -> {
                JSONObject record = (JSONObject) progressObj;
                if (record.getBoolean("isComplete") && record.getBoolean("isNew")) {
                    completedAchievements.add(record);
                }
            });

            for (JSONObject record : completedAchievements) {
                Optional<JSONObject> achievementOpt = this.plugin.getResourceCache().getAchievementById(record.getString("achievementId")).get();
                if (!achievementOpt.isPresent()) {
                    continue;
                }
                JSONObject achievement = achievementOpt.get();

                AwardPersistenceService awardPS = Sponge.getServiceManager()
                        .provide(AwardPersistenceService.class).get();
                List<String> awardIds = new ArrayList<>();
                achievement.getJSONArray("awards")
                        .forEach(awardId -> awardIds.add((String) awardId));

                final Optional<Player> subjectOpt = Sponge.getServer().getPlayer(this.event.getSubject());

                for (String awardId : awardIds) {
                    Optional<JSONObject> awardOpt = this.plugin.getResourceCache().getAwardById(awardId).get();
                    if (!awardOpt.isPresent()) {
                        continue;
                    }
                    JSONObject award = awardOpt.get();

                    awardPS.increment(this.event.getSubject(), awardId);

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
                    achPS.increment(this.event.getSubject(), achievement.getString("id"));
                } else {
                    // Present the achievement to the player
                    this.plugin.presentAchievement(subjectOpt.get(), achievement.getString("id"));
                }
            }
        } catch (Exception e) {
            this.plugin.getLogger().error("There was an error posting events!");
            this.plugin.getLogger().error(e.getMessage());
            e.printStackTrace();
        }

    }
}
