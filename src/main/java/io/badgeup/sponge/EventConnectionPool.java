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

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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

        int size = event.build().toString().getBytes(StandardCharsets.UTF_8).length;
        if (size > 5000) {
            this.plugin.getLogger().warn("Event too large: " + event.build().toString());
            return;
        }

        WebSocket leastFullSocket = this.sockets.get(0);
        for (WebSocket ws : this.sockets) {
            if (ws.queueSize() < leastFullSocket.queueSize()) {
                leastFullSocket = ws;
            }
        }

        boolean isSuccessful = leastFullSocket.send(event.build().toString());

        if (!isSuccessful) {
            this.sockets.remove(leastFullSocket);
            isSuccessful = openNewConnection().send(event.build().toString());

            if (!isSuccessful) {
                this.plugin.getLogger().error("Failed to send event after opening new connection");
            }
        }

        DebugCommandExecutor.getDebugMessageChannelForPlayer(event.getSubject())
                .send(constructEventText(event));
    }

    public WebSocket openNewConnection() {
        Request wsRequest = new Request.Builder().url(HttpUtils.getWebSocketUrl()).build();
        WebSocket socket = HttpUtils.getHttpClient().newWebSocket(wsRequest, new EventWebSocketListener(this.plugin));
        this.sockets.add(socket);
        return socket;
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

        return Text.of(TextColors.GREEN,
                "<", playerName, "> ",
                Text.builder(event.getKey()).onHover(TextActions.showText(Text.of(event.build().toString(4)))).build());
    }

}
