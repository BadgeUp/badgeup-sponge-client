package io.badgeup.sponge.eventlistener;

import org.spongepowered.api.event.Event;

public interface EventKeyProvider<T extends Event> {

    public String provide(T event);

    default String getDefault(T event) {
        return event.getClass().getSimpleName()
                .replace("$Impl", "") // Remove the $Impl in all Sponge events
                .replace('$', ':')
                .toLowerCase()
                .replace("event", "");
    }

}
