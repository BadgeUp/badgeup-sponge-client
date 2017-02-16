package io.badgeup.sponge.eventlistener;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.Util;
import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.event.Modifier;
import io.badgeup.sponge.event.ModifierOperation;
import io.badgeup.sponge.service.AchievementPersistenceService;
import io.badgeup.sponge.service.AwardPersistenceService;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.AnimateHandEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.network.ChannelRegistrationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class GeneralEventListener extends BadgeUpEventListener {

    private BadgeUpSponge plugin;

    public GeneralEventListener(BadgeUpSponge plugin) {
        super();
        this.plugin = plugin;
    }

    @Listener(order = Order.POST)
    @Exclude({NotifyNeighborBlockEvent.class, MoveEntityEvent.class, CollideBlockEvent.class, CollideEntityEvent.class,
            ClientConnectionEvent.Auth.class, ClientConnectionEvent.Login.class, UseItemStackEvent.Replace.class,
            UseItemStackEvent.Reset.class, UseItemStackEvent.Start.class, UseItemStackEvent.Tick.class,
            UseItemStackEvent.Stop.class, ChangeBlockEvent.Post.class, ChangeBlockEvent.Pre.class,
            PlayerChangeClientSettingsEvent.class, ChangeInventoryEvent.Held.class, AnimateHandEvent.class, ClickInventoryEvent.class,
            ChannelRegistrationEvent.class, TabCompleteEvent.class})
    public void event(Event event, @Root Player player)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (event instanceof Cancellable && ((Cancellable) event).isCancelled()) {
            return;
        }

        EventKeyProvider<Event> keyProvider = resolveKeyProvider(event.getClass());
        final String key = keyProvider.provide(event);

        final UUID uuid = player.getUniqueId();
        final int increment = 1;

        BadgeUpEvent newEvent = new BadgeUpEvent(key, uuid, new Modifier(ModifierOperation.INC, increment));

        for (Method m : event.getClass().getMethods()) {
            final String methodName = m.getName();
            if (methodName.startsWith("get")) {
                // To catch weird stuff such as getting entity snapshots from
                // DropItemEvent after Order.PRE, which can't be done
                try {
                    Object result = m.invoke(event, (Object[]) null);
                    // Substring to index 3 to remove "get"
                    newEvent.addDataEntry(methodName.substring(3), result);
                } catch (Exception e) {
                    continue;
                }

            }
        }

        send(newEvent);
    }

    @Listener(order = Order.POST)
    public void dropItem(DropItemEvent.Dispense event, @Root EntitySpawnCause cause)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (!(cause.getEntity() instanceof Player)) {
            return;
        }
        event(event, (Player) cause.getEntity());
    }

    @Listener
    public void playerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        AchievementPersistenceService achPS = Sponge.getServiceManager().provide(AchievementPersistenceService.class)
                .get();
        achPS.getUnpresentedAchievementsForPlayer(player.getUniqueId()).thenAcceptAsync(achievements -> {
            for (JSONObject achievement : achievements) {
                BadgeUpSponge.presentAchievement(player, achievement);
                achPS.removeAchievementByID(player.getUniqueId(), achievement.getString("id"));
            }
        });

        AwardPersistenceService awardPS = Sponge.getServiceManager().provide(AwardPersistenceService.class).get();
        awardPS.getPendingAwardsForPlayer(player.getUniqueId()).thenAcceptAsync(awards -> {
            for (JSONObject award : awards) {
                // Check if award is auto-redeemable. If so, redeem it
                if (Util.safeGetBoolean(award.getJSONObject("data"), "autoRedeem").orElse(false)) {
                    Sponge.getCommandManager().process(player, "redeem " + award.getString("id"));
                }
            }
        });

    }

}