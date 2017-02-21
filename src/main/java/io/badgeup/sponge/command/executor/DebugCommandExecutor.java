package io.badgeup.sponge.command.executor;

import com.google.common.collect.Lists;
import io.badgeup.sponge.BadgeUpSponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DebugCommandExecutor implements CommandExecutor {

    private BadgeUpSponge plugin;
    private static Map<UUID, MessageChannel> messageChannels = new HashMap<>();

    public DebugCommandExecutor(BadgeUpSponge plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<Object> optPlayer = args.getOne("player");
        if (!optPlayer.isPresent()) {
            throw new CommandException(Text.of("No player provided"));
        }

        Player player = (Player) optPlayer.get();

        if (messageChannels.containsKey(player.getUniqueId())) {
            MessageChannel existingChannel = messageChannels.get(player.getUniqueId());
            // Convert to a mutable collection
            Collection<MessageReceiver> receivers = Lists.newArrayList(existingChannel.getMembers());

            if (receivers.contains(src)) {
                receivers.remove(src);
                src.sendMessage(
                        Text.of(TextColors.GREEN, "You have stopped listening to ", TextColors.GOLD, player.getName(), TextColors.GREEN,
                                "'s events."));
            } else {
                receivers.add(src);
                src.sendMessage(
                        Text.of(TextColors.GREEN, "You have started listening to ", TextColors.GOLD, player.getName(), TextColors.GREEN,
                                "'s events."));
            }
            messageChannels.put(player.getUniqueId(), MessageChannel.fixed(receivers));

        } else {
            messageChannels.put(player.getUniqueId(), MessageChannel.fixed(src));
            src.sendMessage(
                    Text.of(TextColors.GREEN, "You have started listening to ", TextColors.GOLD, player.getName(), TextColors.GREEN, "'s events."));
        }

        return CommandResult.success();
    }

    public static MessageChannel getDebugMessageChannelForPlayer(UUID uuid) {
        if (messageChannels.containsKey(uuid)) {
            return messageChannels.get(uuid);
        } else {
            return MessageChannel.TO_NONE;
        }
    }

}
