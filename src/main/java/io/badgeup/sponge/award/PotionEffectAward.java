package io.badgeup.sponge.award;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.util.Util;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotionEffectAward extends Award {

    public PotionEffectAward(BadgeUpSponge plugin, JSONObject award) {
        super(plugin, award);
    }

    @Override
    public boolean awardPlayer(Player player) {
        final Optional<String> potionEffectTypeIDOpt = Util.safeGetString(this.data, "potionEffectType");
        if (!potionEffectTypeIDOpt.isPresent()) {
            this.plugin.getLogger().error("No potion effect type specified. Aborting.");
            return false;
        }

        String potionEffectTypeID = potionEffectTypeIDOpt.get();

        final Optional<PotionEffectType> optType = Sponge.getRegistry().getType(PotionEffectType.class,
                potionEffectTypeID);
        if (!optType.isPresent()) {
            this.plugin.getLogger().error("Potion effect type " + potionEffectTypeID + " not found. Aborting.");
            return false;
        }

        PotionEffect.Builder builder = Sponge.getRegistry().createBuilder(PotionEffect.Builder.class)
                .potionType(optType.get());

        Optional<Integer> durationOpt = Util.safeGetInt(this.data, "duration");
        if (!durationOpt.isPresent()) {
            this.plugin.getLogger().error("Potion effect duration not specified. Aborting.");
            return false;
        }
        builder.duration(durationOpt.get());

        builder.amplifier(Util.safeGetInt(this.data, "amplifier").orElse(1));

        List<PotionEffect> currentEffects = player.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
        currentEffects.add(builder.build());

        DataTransactionResult result = player.offer(Keys.POTION_EFFECTS, currentEffects);
        return result.isSuccessful();
    }

}
