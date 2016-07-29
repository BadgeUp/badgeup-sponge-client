package io.badgeup.sponge.listener.block;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.event.Modifier;
import io.badgeup.sponge.event.ModifierOperation;
import io.badgeup.sponge.listener.BadgeUpEventListener;

public class ChangeBlockEventListener extends BadgeUpEventListener {
	
	public ChangeBlockEventListener(BadgeUpSponge plugin) {
		super(plugin);
	}

	@Listener(order = Order.POST)
	public void breakBlock(ChangeBlockEvent.Break event) {
		if(event.isCancelled()) {
			return;
		}
		final Optional<Player> optPlayer = event.getCause().<Player>first(Player.class);
		if(!optPlayer.isPresent()) {
			return;
		}
   		final Player player = optPlayer.get();

   		for(final Transaction<BlockSnapshot> transaction : event.getTransactions()) {
   			final UUID uuid = player.getUniqueId();
   			final String key = "block:break";
   			final int increment = 1;
   			
   			final Optional<ItemStack> optItem = player.getItemInHand(HandTypes.MAIN_HAND); // Can't break blocks w/ off-hand
   			String tool = "fist";
   			if (optItem.isPresent()) {
   				tool = optItem.get().getItem().getName().toLowerCase();
   			}
   			
   			final String blockType = transaction.getOriginal().getState().getType().getId().toLowerCase();

   			BadgeUpEvent newEvent = new BadgeUpEvent(key, uuid, new Modifier(ModifierOperation.INC, increment));
   			newEvent.addDataEntry("blockType", blockType);
   			newEvent.addDataEntry("tool", tool);
   			
   			send(newEvent);
   		}
	}

	@Listener(order = Order.POST)
	public void placeBlock(ChangeBlockEvent.Place event) {
		if(event.isCancelled()) {
			return;
		}
		final Optional<Player> optPlayer = event.getCause().<Player>first(Player.class);
		if(!optPlayer.isPresent()) {
			return;
		}
   		final Player player = optPlayer.get();

   		for(final Transaction<BlockSnapshot> transaction : event.getTransactions()) {
   			final UUID uuid = player.getUniqueId();
   			final String key = "block:place";
   			final int increment = 1;

   			final String blockType = transaction.getFinal().getState().getType().getId().toLowerCase();
   			BadgeUpEvent newEvent = new BadgeUpEvent(key, uuid, new Modifier(ModifierOperation.INC, increment));
   			newEvent.addDataEntry("blockType", blockType);
   			
   			send(newEvent);
   		}
	}
}
