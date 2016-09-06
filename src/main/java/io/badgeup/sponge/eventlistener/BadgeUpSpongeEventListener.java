package io.badgeup.sponge.eventlistener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.flowpowered.math.vector.Vector3i;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.event.Modifier;
import io.badgeup.sponge.event.ModifierOperation;
import io.badgeup.sponge.service.AchievementPersistenceService;

public class BadgeUpSpongeEventListener {

	private BadgeUpSponge plugin;
	private Map<Class<? extends Event>, EventKeyProvider> keyProviders;
	private Map<UUID, PlayerPath> playerPaths;

	public BadgeUpSpongeEventListener(BadgeUpSponge plugin) {
		this.plugin = plugin;
		this.playerPaths = new HashMap<>();
		this.keyProviders = new HashMap<>();
		registerKeyProviders();
	}

	@Listener(order = Order.POST)
	@Exclude({ NotifyNeighborBlockEvent.class, MoveEntityEvent.class, CollideBlockEvent.class, CollideEntityEvent.class,
			ClientConnectionEvent.Auth.class, ClientConnectionEvent.Login.class, UseItemStackEvent.Replace.class,
			UseItemStackEvent.Reset.class, UseItemStackEvent.Start.class, UseItemStackEvent.Tick.class,
			ChangeBlockEvent.Post.class, ChangeBlockEvent.Pre.class, PlayerChangeClientSettingsEvent.class, ChangeInventoryEvent.Held.class })
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
				Object result = m.invoke(event, (Object[]) null);
				// Substring to index 3 to remove "get"
				newEvent.addDataEntry(methodName.substring(3), result);
			}
		}

		send(newEvent);
	}

	@Listener(order = Order.POST)
	@Exclude({ MoveEntityEvent.Teleport.class })
	public void event(MoveEntityEvent event, @Root Player player) {
		if (event.isCancelled()) {
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

		if (playerPath.size() >= 20) {
			BadgeUpEvent pathEvent = new BadgeUpEvent("playerpath", player.getUniqueId(),
					new Modifier(ModifierOperation.INC, 1));
			pathEvent.addDataEntry("path", playerPath);

			send(pathEvent);

			BadgeUpEvent distanceEvent = new BadgeUpEvent("distance", player.getUniqueId(),
					new Modifier(ModifierOperation.INC, playerPath.getDistance()));

			send(distanceEvent);
			
			this.playerPaths.remove(player.getUniqueId());
		}

	}

	@Listener
	public void playerJoin(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();
		AchievementPersistenceService aps = Sponge.getServiceManager().provide(AchievementPersistenceService.class)
				.get();
		aps.getUnpresentedAchievementsForPlayer(player.getUniqueId()).thenAcceptAsync(achievements -> {
			for (JSONObject achievement : achievements) {
				BadgeUpSponge.presentAchievement(player, achievement);
				aps.removeAchievementByID(player.getUniqueId(), achievement.getString("id"));
			}
		});
	}
	
	private EventKeyProvider resolveKeyProvider(Class eventClass) {
		for(Class interfaceClass : eventClass.getInterfaces()) {
			if(this.keyProviders.containsKey(interfaceClass)) {
				return this.keyProviders.get(interfaceClass);
			}
			
			EventKeyProvider provider = resolveKeyProvider(interfaceClass);
			if(provider == null) {
				continue;
			} else {
				return provider;
			}
		}
		
		// Only if the class has no interfaces
		return null;
	}
	
	private void registerKeyProviders() {
		this.keyProviders.put(Event.class, new EventKeyProvider<Event> () {
			@Override
			public String provide(Event event) {
				return getDefault(event);
			}
		});
		
		this.keyProviders.put(ChangeBlockEvent.Break.class, new EventKeyProvider<ChangeBlockEvent.Break> () {
			@Override
			public String provide(ChangeBlockEvent.Break event) {
				List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
				if(transactions.isEmpty()) {
					// Not sure if this can ever happen
					return getDefault(event);
				}
				
				return getDefault(event) + ":" + transactions.get(0).getOriginal().getState().getType().getId();
			}
		});
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

}
