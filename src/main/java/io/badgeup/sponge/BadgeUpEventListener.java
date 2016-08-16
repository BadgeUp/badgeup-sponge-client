package io.badgeup.sponge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.event.Modifier;
import io.badgeup.sponge.event.ModifierOperation;
import io.badgeup.sponge.service.AchievementPersistenceService;

public class BadgeUpEventListener {

	private BadgeUpSponge plugin;

	public BadgeUpEventListener(BadgeUpSponge plugin) {
		this.plugin = plugin;
	}

	@Listener(order = Order.POST)
	@Exclude({ NotifyNeighborBlockEvent.class, MoveEntityEvent.class, CollideBlockEvent.class, CollideEntityEvent.class,
			ClientConnectionEvent.Auth.class, ClientConnectionEvent.Login.class, UseItemStackEvent.Replace.class,
			UseItemStackEvent.Reset.class, UseItemStackEvent.Start.class, UseItemStackEvent.Tick.class })
	public void event(Event event, @Root Player player)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (event instanceof Cancellable && ((Cancellable) event).isCancelled()) {
			return;
		}

		final String className = event.getClass().getSimpleName();
		final String key = className.toLowerCase().substring(0, className.lastIndexOf("$")).replace('$', ':')
				.replace("event", "");

		final UUID uuid = player.getUniqueId();
		final int increment = 1;

		BadgeUpEvent newEvent = new BadgeUpEvent(key, uuid, new Modifier(ModifierOperation.INC, increment));

		for (Method m : event.getClass().getMethods()) {
			final String methodName = m.getName();
			if (methodName.startsWith("get")) {
				Object result = m.invoke(event, null);
				newEvent.addDataEntry(methodName.substring(3), result);
			}
		}

		send(newEvent);
	}

	private void send(BadgeUpEvent event) {
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

	@Listener
	public void playerJoin(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();
		AchievementPersistenceService aps = Sponge.getServiceManager().provide(AchievementPersistenceService.class).get();
		aps.getUnpresentedAchievementsForPlayer(player.getUniqueId()).thenAcceptAsync(achievements -> {
			for(JSONObject achievement : achievements) {
				BadgeUpSponge.presentAchievement(player, achievement);
			}
		});
	}

}
