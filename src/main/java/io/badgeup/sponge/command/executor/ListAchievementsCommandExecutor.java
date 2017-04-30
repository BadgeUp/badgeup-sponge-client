package io.badgeup.sponge.command.executor;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.util.HttpUtils;
import io.badgeup.sponge.util.ResourceCache;
import io.badgeup.sponge.util.Util;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ListAchievementsCommandExecutor implements CommandExecutor {

    private BadgeUpSponge plugin;

    public ListAchievementsCommandExecutor(BadgeUpSponge plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "Player only!"));
            return CommandResult.success();
        }

        Sponge.getScheduler().createTaskBuilder().async().execute(new GetPlayerProgressRunnable(this.plugin, (Player) src))
                .submit(this.plugin);
        return CommandResult.success();
    }

    class GetPlayerProgressRunnable implements Runnable {

        private ResourceCache resourceCache;
        private Logger logger;
        private Player player;

        private GetPlayerProgressRunnable(BadgeUpSponge plugin, Player player) {
            this.logger = plugin.getLogger();
            this.resourceCache = plugin.getResourceCache();
            this.player = player;
        }

        @Override
        public void run() {
            List<JSONObject> progress = Lists.newArrayList();

            try {
                boolean morePaginationData = true;
                Request request = HttpUtils.getRequest("/progress?subject=" + this.player.getUniqueId().toString());
                while (morePaginationData) {
                    Response response = HttpUtils.getHttpClient().newCall(request).execute();

                    if (response.code() != HttpUtils.STATUS_OK) {
                        this.logger.error("Got response code " + response.code() + " with body " + response.body().string()
                                + " when getting progress for player " + this.player.getUniqueId().toString());
                        return;
                    }

                    JSONObject body = HttpUtils.parseBody(response);

                    JSONArray data = body.getJSONArray("data");
                    data.forEach(obj -> progress.add((JSONObject) obj));

                    JSONObject pages = body.getJSONObject("pages");
                    morePaginationData = !pages.isNull("next");
                    if (morePaginationData) {
                        request = HttpUtils.getRawRequest(pages.getString("next"));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if (progress.isEmpty()) {
                this.player.sendMessage(Text.of(TextColors.GREEN, "You have no achievement progress to show."));
                return;
            }

            Collection<CompletableFuture<Optional<JSONObject>>> achievementFutures =
                    Collections2.transform(progress, progressObj -> progressObj.getString("achievementId")).stream()
                            .distinct()
                            .map(achId -> this.resourceCache.getAchievementById(achId))
                            .collect(Collectors.toList());

            List<JSONObject> achievements;
            try {
                // Get the achievements
                achievements =
                        Util.sequence(achievementFutures).get().stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return;
            }

            Map<String, JSONObject> mappedAchievements = Maps.uniqueIndex(achievements, ach -> ach.getString("id"));

            List<Text> textList = Lists.newArrayList();
            for (JSONObject progressObj : progress) {
                JSONObject achievement = mappedAchievements.get(progressObj.getString("achievementId"));

                Text.Builder achievementTextBuilder = Text.builder(achievement.getString("name")).color(TextColors.GOLD);
                if (!achievement.isNull("description")) {
                    achievementTextBuilder
                            .onHover(TextActions.showText(Text.of(TextColors.GOLD, achievement.getString("description"))));
                }

                double percentComplete = progressObj.getDouble("percentComplete");
                TextColor percentColor;
                if (percentComplete <= 0) {
                    percentColor = TextColors.GRAY;
                } else if (percentComplete > 0 && percentComplete < 1) {
                    percentColor = TextColors.YELLOW;
                } else {
                    percentColor = TextColors.GREEN;
                }

                Text progressText = Text.of(percentColor, (int) (percentComplete * 100) + "% Complete");

                textList.add(Text.of(achievementTextBuilder.build(), TextColors.RESET, " - ", progressText));
            }

            PaginationService pagination = Sponge.getServiceManager().provide(PaginationService.class).get();
            pagination.builder().contents(textList).title(Text.of(TextColors.BLUE, "Achievement Progress")).padding(Text.of('-'))
                    .linesPerPage(10).sendTo(this.player); // 10 lines = 8
                                                           // progress
            // entries + header +
            // footer
        }

    }

}
