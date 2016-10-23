package io.badgeup.sponge.command.executor;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.award.Award;
import io.badgeup.sponge.award.EntityAward;
import io.badgeup.sponge.award.ItemAward;
import io.badgeup.sponge.award.MonetaryAward;
import io.badgeup.sponge.award.PotionEffectAward;
import io.badgeup.sponge.service.AwardPersistenceService;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class RedeemAwardCommandExecutor implements CommandExecutor {

    private BadgeUpSponge plugin;

    public RedeemAwardCommandExecutor(BadgeUpSponge plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "Player only!"));
            return CommandResult.success();
        }
        Player player = (Player) src;

        String awardID = args.getOne("id").get().toString();

        AwardPersistenceService awardPS = Sponge.getServiceManager().provide(AwardPersistenceService.class).get();
        awardPS.getPendingAwardsForPlayer(player.getUniqueId()).thenAcceptAsync(awards -> {
            Optional<JSONObject> awardJSONOpt = Optional.empty();

            for (JSONObject tmpAward : awards) {
                if (tmpAward.getString("id").equals(awardID)) {
                    awardJSONOpt = Optional.of(tmpAward);
                }
            }

            if (!awardJSONOpt.isPresent()) {
                player.sendMessage(Text.of(TextColors.RED, "Invalid award."));
                return;
            }

            JSONObject awardJSON = awardJSONOpt.get();
            Optional<Award> awardOpt = processAward(awardJSON);
            if (!awardOpt.isPresent()) {
                player.sendMessage(Text.of(TextColors.RED, "Failed to process award data."));
                return;
            }

            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                boolean success = awardOpt.get().awardPlayer(player);
                if (success) {
                    awardOpt.get().notifyPlayer(player);
                    awardPS.removePendingAwardByID(player.getUniqueId(), awardID);
                } else {
                    player.sendMessage(Text.of(TextColors.RED, "Unable to redeem award."));
                }
            }).submit(this.plugin);

        });
        return CommandResult.success();
    }

    private Optional<Award> processAward(JSONObject awardJSON) {
        final String awardType = awardJSON.getJSONObject("data").getString("type");
        Optional<Award> awardOpt = Optional.empty();
        switch (awardType.toLowerCase()) {
            case "monetary":
                awardOpt = Optional.of(new MonetaryAward(this.plugin, awardJSON));
                break;
            case "item":
                awardOpt = Optional.of(new ItemAward(this.plugin, awardJSON));
                break;
            case "entity":
                awardOpt = Optional.of(new EntityAward(this.plugin, awardJSON));
                break;
            case "potion":
                awardOpt = Optional.of(new PotionEffectAward(this.plugin, awardJSON));
                break;
            default:
                awardOpt = Optional.empty();
                break;
        }

        if (!awardOpt.isPresent()) {
            this.plugin.getLogger().warn("Could not parse award for type " + awardType);
        }

        return awardOpt;
    }

}
