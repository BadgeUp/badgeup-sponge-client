package io.badgeup.sponge;

import com.google.common.collect.Lists;
import io.badgeup.sponge.command.executor.DebugCommandExecutor;
import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.util.HttpUtils;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;

public class EventConnectionPool {

    private BadgeUpSponge plugin;
    private List<WebSocket> sockets;

    public EventConnectionPool(BadgeUpSponge plugin) {
        this.plugin = plugin;
        this.sockets = Lists.newArrayList();
    }

    public void sendEvent(BadgeUpEvent event) {
        event.setDiscardable(false);

        WebSocket leastFullSocket = this.sockets.get(0);
        for (WebSocket ws : this.sockets) {
            if (ws.queueSize() < leastFullSocket.queueSize()) {
                leastFullSocket = ws;
            }
        }

        // TODO do something with this
        boolean isSuccessful = leastFullSocket.send(event.build().toString());

        DebugCommandExecutor.getDebugMessageChannelForPlayer(event.getSubject())
                .send(Text.of((isSuccessful ? TextColors.GREEN : TextColors.RED), constructEventText(event)));
    }

    public void openNewConnection() {
        Request wsRequest = new Request.Builder().url(HttpUtils.getWebSocketUrl()).build();
        this.sockets.add(HttpUtils.getHttpClient().newWebSocket(wsRequest, new EventWebSocketListener(this.plugin)));
    }

    public void closeAllConnections(String reason) {
        for (WebSocket ws : this.sockets) {
            ws.close(HttpUtils.WS_CLOSED, reason);
        }
        this.sockets.clear();
    }

    private Text constructEventText(BadgeUpEvent event) {
        String playerName = "N/A";
        Optional<Player> playerOpt = Sponge.getServer().getPlayer(event.getSubject());
        if (playerOpt.isPresent()) {
            playerName = playerOpt.get().getName();
        }

        return Text.of(
                "<", playerName, "> ",
                Text.builder(event.getKey()).onHover(TextActions.showText(Text.of(event.build().toString(4)))).build());
    }

}
