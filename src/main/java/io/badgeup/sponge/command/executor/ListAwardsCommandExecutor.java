package io.badgeup.sponge.command.executor;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.service.AwardPersistenceService;
import org.json.JSONObject;
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
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListAwardsCommandExecutor implements CommandExecutor {

    private BadgeUpSponge plugin;

    public ListAwardsCommandExecutor(BadgeUpSponge plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "Player only!"));
            return CommandResult.success();
        }
        Player player = (Player) src;

        AwardPersistenceService awardPS = Sponge.getServiceManager().provide(AwardPersistenceService.class).get();
        awardPS.getAllForPlayer(player.getUniqueId()).thenAcceptAsync(awards -> {
            if (awards.size() == 0) {
                player.sendMessage(Text.of(TextColors.GREEN, "You have no awards to claim."));
                return;
            }

            List<Text> awardTexts = new ArrayList<>();
            for (String awardId : awards.keySet()) {
                try {
                    JSONObject award = this.plugin.getResourceCache().getAwardById(awardId).get();
                    Text.Builder awardTextBuilder = Text.builder(award.getString("name")).color(TextColors.GOLD);

                    if (!award.isNull("description")) {
                        awardTextBuilder
                                .onHover(TextActions.showText(Text.of(TextColors.GOLD, award.getString("description"))));
                    }

                    Text redeemText = Text.builder("[Redeem]").color(TextColors.GREEN)
                            .onHover(TextActions.showText(Text.of(TextColors.GREEN, "Redeem award")))
                            .onClick(TextActions.runCommand("/redeem " + award.getString("id"))).build();

                    awardTexts.add(Text.of(awardTextBuilder.build(), TextColors.GREEN, " (x" + awards.get(awardId).intValue() + ")", TextColors.RESET,
                            " - ", redeemText));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            PaginationService pagination = Sponge.getServiceManager().provide(PaginationService.class).get();
            pagination.builder().contents(awardTexts).title(Text.of(TextColors.BLUE, "Awards")).padding(Text.of('-'))
                    .linesPerPage(10).sendTo(player); // 10 lines = 8 awards +
                                                      // header + footer
        });
        return CommandResult.success();
    }

}
