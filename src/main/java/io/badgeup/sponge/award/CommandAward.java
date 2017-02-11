package io.badgeup.sponge.award;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.Util;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandAward extends Award {

    public CommandAward(BadgeUpSponge plugin, JSONObject award) {
        super(plugin, award);
    }

    @Override
    public boolean awardPlayer(Player player) {
        Optional<String> commandTemplateOpt = Util.safeGetString(this.data, "command");
        if (!commandTemplateOpt.isPresent()) {
            this.plugin.getLogger().error("No command specified. Aborting.");
            return false;
        }
        
        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("playerName", player.getName());
        valuesMap.put("playerId", player.getUniqueId().toString());
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String command = sub.replace(commandTemplateOpt.get());
        
        this.plugin.getLogger().info("Executing command award as console: " + command);
        
        CommandResult result = Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);

        return result.getSuccessCount().orElse(0) > 0;
    }

}
