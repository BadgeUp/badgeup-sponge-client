package io.badgeup.sponge.eventlistener;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.event.Modifier;
import io.badgeup.sponge.event.ModifierOperation;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.monster.Boss;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BadgeUpEventListener {

    protected BadgeUpSponge plugin;
    private Map<Class<? extends Event>, EventKeyProvider> keyProviders;

    public BadgeUpEventListener(BadgeUpSponge plugin) {
        this.keyProviders = new HashMap<>();
        this.plugin = plugin;
        registerKeyProviders();
    }

    public EventKeyProvider resolveKeyProvider(Class eventClass) {
        if (this.keyProviders.containsKey(eventClass)) {
            return this.keyProviders.get(eventClass);
        }

        for (Class interfaceClass : eventClass.getInterfaces()) {
            if (this.keyProviders.containsKey(interfaceClass)) {
                return this.keyProviders.get(interfaceClass);
            }

            EventKeyProvider provider = resolveKeyProvider(interfaceClass);
            if (provider == null) {
                continue;
            } else {
                return provider;
            }
        }

        // Only if the class has no interfaces
        return null;
    }

    private void registerKeyProviders() {
        this.keyProviders.put(Event.class, new EventKeyProvider<Event>() {

            @Override
            public String provide(Event event) {
                return getDefault(event);
            }
        });

        // WARNING: Don't make a key provider for any ChangeBlockEvent classes,
        // as it will break the custom
        // event handling for those events

        this.keyProviders.put(UseItemStackEvent.class, new EventKeyProvider<UseItemStackEvent>() {

            @Override
            public String provide(UseItemStackEvent event) {
                return getDefault(event) + ":" + event.getItemStackInUse().getType().getId();
            }
        });

        this.keyProviders.put(DropItemEvent.Dispense.class, new EventKeyProvider<DropItemEvent.Dispense>() {

            @Override
            public String provide(DropItemEvent.Dispense event) {
                Item item = (Item) event.getEntities().get(0);
                return getDefault(event) + ":" + item.getItemType().getId();
            }
        });

        this.keyProviders.put(ChangeInventoryEvent.Pickup.class, new EventKeyProvider<ChangeInventoryEvent.Pickup>() {

            @Override
            public String provide(ChangeInventoryEvent.Pickup event) {
                return getDefault(event) + ":" + event.getTargetEntity().getItemType().getId();
            }
        });

        this.keyProviders.put(DestructEntityEvent.Death.class, new EventKeyProvider<DestructEntityEvent.Death>() {

            @Override
            public String provide(DestructEntityEvent.Death event) {
                Entity entity = event.getTargetEntity();

                String entityCategory = "";
                if (entity instanceof Player) {
                    entityCategory = "player";
                } else if (entity instanceof Boss) {
                    entityCategory = "boss";
                } else if (entity instanceof Hostile) {
                    entityCategory = "hostile";
                } else {
                    entityCategory = "passive";
                }

                String id = "";
                if (entity instanceof Player) {
                    id = ((Player) entity).getUniqueId().toString();
                } else {
                    id = entity.getType().getId();
                }

                return getDefault(event) + ":" + entityCategory + ":" + id;
            }
        });

        this.keyProviders.put(GrantAchievementEvent.class, new EventKeyProvider<GrantAchievementEvent>() {

            @Override
            public String provide(GrantAchievementEvent event) {
                return "grantachievement:" + event.getAchievement().getId();
            }
        });

        this.keyProviders.put(SpawnEntityEvent.class, new EventKeyProvider<SpawnEntityEvent>() {

            @Override
            public String provide(SpawnEntityEvent event) {
                return getDefault(event) + ":" + event.getEntities().get(0).getType().getId();
            }
        });

        this.keyProviders.put(FishingEvent.Stop.class, new EventKeyProvider<FishingEvent.Stop>() {

            @Override
            public String provide(FishingEvent.Stop event) {
                List<Transaction<ItemStackSnapshot>> transactions = event.getItemStackTransaction();

                if (transactions.isEmpty()) {
                    return getDefault(event);
                }

                return getDefault(event) + ":" + transactions.get(0).getFinal().getType().getId();
            }
        });
    }

    public void processEvent(Event event, Player player) {
        EventKeyProvider<Event> keyProvider = resolveKeyProvider(event.getClass());
        final String key = keyProvider.provide(event);

        processEvent(event, player, key, 1);
    }

    public void processEvent(Event event, Player player, String key) {
        processEvent(event, player, key, 1);
    }

    public void processEvent(Event event, Player player, int increment) {
        EventKeyProvider<Event> keyProvider = resolveKeyProvider(event.getClass());
        final String key = keyProvider.provide(event);

        processEvent(event, player, key, increment);
    }

    public void processEvent(Event event, Player player, String key, int increment) {
        final UUID uuid = player.getUniqueId();

        BadgeUpEvent newEvent = new BadgeUpEvent(key, uuid, new Modifier(ModifierOperation.INC, increment));

        for (Method m : event.getClass().getMethods()) {
            final String methodName = m.getName();
            if (methodName.startsWith("get")) {
                m.setAccessible(true);
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

    public void send(BadgeUpEvent event) {
        this.plugin.getEventConnectionPool().sendEvent(event);
    }

}
