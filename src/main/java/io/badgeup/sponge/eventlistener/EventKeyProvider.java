package io.badgeup.sponge.eventlistener;

import org.spongepowered.api.event.Event;

public interface EventKeyProvider<T extends Event> {

    public String provide(T event);

    default String getDefault(T event) {
        String className = event.getClass().getSimpleName();
        return className.toLowerCase().substring(0, className.lastIndexOf("$")).replace('$', ':')
                .replace("event", "");
    }

}
