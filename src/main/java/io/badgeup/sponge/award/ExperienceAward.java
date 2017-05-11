package io.badgeup.sponge.award;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.util.Util;
import org.json.JSONObject;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class ExperienceAward extends Award {

    public ExperienceAward(BadgeUpSponge plugin, JSONObject award) {
        super(plugin, award);
    }

    @Override
    public boolean awardPlayer(Player player) {
        final Optional<Integer> expAmountOpt = Util.safeGetInt(this.data, "amount");
        if (!expAmountOpt.isPresent()) {
            this.plugin.getLogger().error("No experience amount specified. Aborting.");
            return false;
        }

        int existingExperience = player.get(Keys.TOTAL_EXPERIENCE).get();

        DataTransactionResult result = player.offer(Keys.TOTAL_EXPERIENCE, existingExperience + expAmountOpt.get());
        return result.isSuccessful();
    }

}
