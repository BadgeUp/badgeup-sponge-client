package io.badgeup.sponge.eventlistener;

import org.spongepowered.api.event.Event;

@FunctionalInterface
public interface EventKeyProvider<T extends Event> {
	
	public String provide(T event);
	
}
