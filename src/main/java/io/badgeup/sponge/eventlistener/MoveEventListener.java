package io.badgeup.sponge.eventlistener;

import com.flowpowered.math.vector.Vector3i;
import io.badgeup.sponge.JSONSerializable;
import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.event.Modifier;
import io.badgeup.sponge.event.ModifierOperation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MoveEventListener extends BadgeUpEventListener {

    private Map<UUID, PlayerPath> playerPaths;

    public MoveEventListener() {
        super();
        this.playerPaths = new HashMap<>();
    }

    @Listener(order = Order.POST)
    public void move(MoveEntityEvent event) {
        if (event.isCancelled() || !(event.getTargetEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getTargetEntity();

        // If the player teleported, just send the distance event (not including
        // their new location) and remove their player path
        if (event instanceof MoveEntityEvent.Teleport) {
            sendAndRemove(player.getUniqueId());
            return;
        }

        PlayerPath playerPath;
        if (this.playerPaths.containsKey(player.getUniqueId())) {
            playerPath = this.playerPaths.get(player.getUniqueId());
        } else {
            playerPath = new PlayerPath();
            this.playerPaths.put(player.getUniqueId(), playerPath);
        }

        Vector3i position = player.getLocation().getBlockPosition();
        playerPath.addPoint(position);

        // Send the event every 20 blocks moved
        if (playerPath.size() >= 20) {
            sendAndRemove(player.getUniqueId());
        }

    }

    @Listener
    public void playerLeave(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        sendAndRemove(player.getUniqueId());
    }
    
    @Listener
    public void playerDeath(DestructEntityEvent.Death event) {
        if (!(event.getTargetEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getTargetEntity();
        sendAndRemove(player.getUniqueId());
    }
    
    // Sends the "distance" event and removes the player from the path map
    private void sendAndRemove(UUID playerUUID) {
        if (!this.playerPaths.containsKey(playerUUID)) {
            return;
        }
        
        PlayerPath playerPath = this.playerPaths.get(playerUUID);
        
        BadgeUpEvent distanceEvent = new BadgeUpEvent("distance", playerUUID,
                new Modifier(ModifierOperation.INC, playerPath.getDistance()));
        distanceEvent.addDataEntry("path", playerPath);

        send(distanceEvent);

        this.playerPaths.remove(playerUUID);
    }

    class PlayerPath implements JSONSerializable {

        private double distance;
        private List<Vector3i> points;

        public PlayerPath() {
            this.distance = 0;
            this.points = new ArrayList<>();
        }

        public void addPoint(Vector3i point) {
            if (this.points.size() == 0) {
                this.points.add(point);
            } else {
                Vector3i lastPoint = this.points.get(this.points.size() - 1);
                if (!point.equals(lastPoint)) {
                    this.points.add(point);
                }
                this.distance += lastPoint.distance(point);
            }
        }

        public double getDistance() {
            return this.distance;
        }

        public int size() {
            return this.points.size();
        }

        @Override
        public JSONObject toJSON() {
            JSONObject obj = new JSONObject();
            obj.put("distance", this.distance);

            JSONArray pathArray = new JSONArray();
            for (Vector3i point : this.points) {
                pathArray.put(new JSONObject().put("x", point.getX()).put("y", point.getY()).put("z", point.getZ()));
            }
            obj.put("path", pathArray);
            return obj;
        }

    }

}
