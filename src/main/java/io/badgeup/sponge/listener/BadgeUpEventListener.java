package io.badgeup.sponge.listener;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.event.BadgeUpEvent;

public abstract class BadgeUpEventListener {

	private BadgeUpSponge plugin;

	public BadgeUpEventListener(BadgeUpSponge plugin) {
		this.plugin = plugin;
	}

	public void send(BadgeUpEvent event) {
		boolean success = true;
		try {
			success = BadgeUpSponge.getEventQueue().add(event);
		} catch (IllegalStateException e) {
			success = false;
		}

		if (!success) {
			this.plugin.getLogger().warn("Could not add another event to the event queue. Discarding event.");
			// TODO try to re-add somehow
		}
	}

}
