package io.badgeup.sponge.listener.block;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.CollideBlockEvent;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.event.Modifier;
import io.badgeup.sponge.event.ModifierOperation;
import io.badgeup.sponge.listener.BadgeUpEventListener;

public class CollideBlockEventListener extends BadgeUpEventListener {

	public CollideBlockEventListener(BadgeUpSponge plugin) {
		super(plugin);
	}
	
	@Listener(order = Order.POST)
	public void breakBlock(CollideBlockEvent event) {
		if(event.isCancelled()) {
			return;
		}
		final Optional<Player> optPlayer = event.getCause().<Player>first(Player.class);
		if(!optPlayer.isPresent()) {
			return;
		}
   		final Player player = optPlayer.get();

		final UUID uuid = player.getUniqueId();
		String key = event instanceof CollideBlockEvent.Impact ? "collideblock:impact" : "collideblock";
		final int increment = 1;
		
		BadgeUpEvent newEvent = new BadgeUpEvent(key, uuid, new Modifier(ModifierOperation.INC, increment));
		newEvent.addDataEntry("targetLocation", event.getTargetLocation().toContainer());
		newEvent.addDataEntry("targetBlock", event.getTargetBlock().toContainer());
		newEvent.addDataEntry("targetSide", event.getTargetSide().toString());
		
		if(event instanceof CollideBlockEvent.Impact) {
			newEvent.addDataEntry("impactPoint", ((CollideBlockEvent.Impact) event).getImpactPoint().toContainer());
		}
		
		send(newEvent);
	}
	
}
