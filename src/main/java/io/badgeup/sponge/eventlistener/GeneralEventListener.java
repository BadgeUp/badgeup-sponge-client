package io.badgeup.sponge.eventlistener;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.event.Modifier;
import io.badgeup.sponge.event.ModifierOperation;
import io.badgeup.sponge.service.AchievementPersistenceService;
import io.badgeup.sponge.service.AwardPersistenceService;
import io.badgeup.sponge.util.Util;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
import org.spongepowered.api.event.action.SleepingEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.AnimateHandEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.network.ChannelRegistrationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.statistic.ChangeStatisticEvent;
import org.spongepowered.api.statistic.achievement.Achievement;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GeneralEventListener extends BadgeUpEventListener {

    public GeneralEventListener(BadgeUpSponge plugin) {
        super(plugin);
    }

    @Listener(order = Order.POST)
    @Exclude({
            AnimateHandEvent.class,
            ChangeBlockEvent.class, // handled below by changeBlock
            ChangeInventoryEvent.Held.class,
            ChangeStatisticEvent.class,
            ChannelRegistrationEvent.class,
            ClickInventoryEvent.class,
            ClientConnectionEvent.Auth.class, ClientConnectionEvent.Login.class,
            CollideBlockEvent.class,
            CollideEntityEvent.class,
            ConstructEntityEvent.class,
            GrantAchievementEvent.class, // handled below by grantAchievement
            MoveEntityEvent.class, // handled in MoveEventListener
            NotifyNeighborBlockEvent.class,
            PlayerChangeClientSettingsEvent.class,
            SleepingEvent.Pre.class, SleepingEvent.Tick.class, SleepingEvent.Post.class,
            TabCompleteEvent.class,
            // Don't exclude UseItemStackEvent.Stop (which bows use)
            UseItemStackEvent.Replace.class, UseItemStackEvent.Reset.class, UseItemStackEvent.Start.class, UseItemStackEvent.Tick.class
    })
    public void event(Event event, @Root Player player)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        processEvent(event, player);
    }

    @Listener(order = Order.POST)
    @Exclude({DropItemEvent.Pre.class})
    public void spawnEntity(Event event, @Root EntitySpawnCause cause)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (!(cause.getEntity() instanceof Player)) {
            return;
        }

        processEvent(event, (Player) cause.getEntity());
    }

    @Listener(order = Order.POST)
    public void damageEntity(Event event, @Root EntityDamageSource cause)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (!(cause.getSource() instanceof Player)) {
            return;
        }

        processEvent(event, (Player) cause.getSource());
    }

    @Listener(order = Order.POST)
    @Exclude({ChangeBlockEvent.Post.class, ChangeBlockEvent.Pre.class})
    public void changeBlock(ChangeBlockEvent event, @Root Player player) {
        // Just get the default key provider
        EventKeyProvider<Event> keyProvider = resolveKeyProvider(Event.class);

        final UUID uuid = player.getUniqueId();
        final int increment = 1;

        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            String key = keyProvider.provide(event);
            if (event instanceof ChangeBlockEvent.Place || event instanceof ChangeBlockEvent.Modify) {
                key += ":" + transaction.getFinal().getState().getType().getId();
            } else if (event instanceof ChangeBlockEvent.Break) {
                key += ":" + transaction.getOriginal().getState().getType().getId();
            } else {
                continue;
            }

            BadgeUpEvent newEvent = new BadgeUpEvent(key, uuid, new Modifier(ModifierOperation.INC, increment));
            newEvent.addDataEntry("Transaction", transaction);
            send(newEvent);
        }
    }

    @Listener(order = Order.POST)
    public void grantAchievement(GrantAchievementEvent.TargetPlayer event)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Player player = event.getTargetEntity();
        for (Achievement earnedAchievement : player.getAchievementData().achievements().get()) {
            if (earnedAchievement.getId().equals(event.getAchievement().getId())) {
                // Player has already earned the achievement -> do nothing
                return;
            }
        }

        processEvent(event, player);
    }

    @Listener(order = Order.POST)
    public void entityDeath(DestructEntityEvent.Death event, @Getter("getTargetEntity") Player inventory)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String key = "death";
        Optional<DamageSource> dmgSrcOpt = event.getCause().first(DamageSource.class);
        if (dmgSrcOpt.isPresent()) {
            key += ":" + dmgSrcOpt.get().getType().getId();
        }

        processEvent(event, (Player) event.getTargetEntity(), key);
    }

    @Listener
    public void playerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        AchievementPersistenceService achPS = Sponge.getServiceManager().provide(AchievementPersistenceService.class)
                .get();
        achPS.getAllForPlayer(player.getUniqueId()).thenAcceptAsync(achievements -> {
            for (String achievementId : achievements.keySet()) {
                for (int i = 0; i < achievements.get(achievementId).intValue(); i++) {
                    try {
                        this.plugin.presentAchievement(player, achievementId);
                        achPS.decrement(player.getUniqueId(), achievementId);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        AwardPersistenceService awardPS = Sponge.getServiceManager().provide(AwardPersistenceService.class).get();
        awardPS.getAllForPlayer(player.getUniqueId()).thenAcceptAsync(awards -> {
            for (String awardId : awards.keySet()) {
                for (int i = 0; i < awards.get(awardId).intValue(); i++) {
                    try {
                        Optional<JSONObject> awardOpt = this.plugin.getResourceCache().getAwardById(awardId).get();

                        if (!awardOpt.isPresent()) {
                            continue;
                        }

                        JSONObject award = awardOpt.get();

                        // Check if award is auto-redeemable. If so, redeem it
                        if (Util.safeGetBoolean(award.getJSONObject("data"), "autoRedeem").orElse(false)) {
                            Sponge.getCommandManager().process(player, "redeem " + awardId);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

}
