package io.badgeup.sponge.eventlistener;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.PostEventRunnable;
import io.badgeup.sponge.event.BadgeUpEvent;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.scheduler.Task;

import java.util.HashMap;
import java.util.Map;

public abstract class BadgeUpEventListener {

    private BadgeUpSponge plugin;
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
    }

    public void send(BadgeUpEvent event) {
        Task.builder().async().execute(new PostEventRunnable(this.plugin, event)).submit(this.plugin);
    }

}
