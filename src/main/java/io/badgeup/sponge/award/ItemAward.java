package io.badgeup.sponge.award;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.Util;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.SkullType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemAward extends Award {

    public ItemAward(BadgeUpSponge plugin, JSONObject award) {
        super(plugin, award);
    }

    @Override
    public boolean awardPlayer(Player player) {
        Optional<String> itemTypeIDOpt = Util.safeGetString(this.data, "itemType");
        if (!itemTypeIDOpt.isPresent()) {
            this.plugin.getLogger().error("No item type specified. Aborting.");
            return false;
        }

        String itemTypeID = itemTypeIDOpt.get();

        final Optional<ItemType> optType = Sponge.getRegistry().getType(ItemType.class, itemTypeID);
        if (!optType.isPresent()) {
            this.plugin.getLogger().error("Could not retrieve ItemType with ID " + itemTypeID + ". Aborting.");
            return false;
        }

        final ItemStack.Builder builder = Sponge.getRegistry().createBuilder(ItemStack.Builder.class)
                .itemType(optType.get()).quantity(Util.safeGetInt(this.data, "quantity").orElse(1));

        final Optional<Text> displayNameOpt = Util.deserializeText(Util.safeGet(this.data, "displayName").orElse(null));
        if (displayNameOpt.isPresent()) {
            builder.add(Keys.DISPLAY_NAME, displayNameOpt.get());
        }

        final Optional<List<Object>> rawLoreOpt = Util.safeGetList(this.data, "lore");
        if (rawLoreOpt.isPresent()) {
            List<Text> lore = new ArrayList<>();
            for (Object rawLine : rawLoreOpt.get()) {
                Util.deserializeText(rawLine).ifPresent(lore::add);
            }
            builder.add(Keys.ITEM_LORE, lore);
        }

        final Optional<List<Object>> rawEnchantmentsOpt = Util.safeGetList(this.data, "enchantments");
        if (rawEnchantmentsOpt.isPresent()) {
            List<ItemEnchantment> enchantments = new ArrayList<>();
            for (Object obj : rawEnchantmentsOpt.get()) {
                if (!(obj instanceof JSONObject)) {
                    this.plugin.getLogger().error("Item enchantment entry is not a JSON object. Skipping enchantment.");
                    continue;
                }
                JSONObject enchantmentInfo = (JSONObject) obj;
                Optional<String> enchantmentIDOpt = Util.safeGetString(enchantmentInfo, "id");
                if (!enchantmentIDOpt.isPresent()) {
                    this.plugin.getLogger().error("No enchantment ID specified. Skipping enchantment.");
                    continue;
                }
                Optional<Enchantment> enchantmentOpt = Sponge.getRegistry().getType(Enchantment.class,
                        enchantmentIDOpt.get());
                if (!enchantmentOpt.isPresent()) {
                    this.plugin.getLogger().error(
                            "Could not find enchantment with ID " + enchantmentIDOpt.get() + ". Skipping enchantment.");
                    continue;
                }
                Optional<Integer> enchantLevelOpt = Util.safeGetInt(enchantmentInfo, "level");
                if (!enchantLevelOpt.isPresent()) {
                    this.plugin.getLogger().error("No enchantment level specified. Skipping enchantment.");
                    continue;
                }
                int enchantLevel = enchantLevelOpt.get();
                if (enchantLevel <= 0) {
                    this.plugin.getLogger()
                            .error("Invalid enchantment level of " + enchantLevel + ". Skipping enchantment.");
                    continue;
                }
                enchantments.add(new ItemEnchantment(enchantmentOpt.get(), enchantLevel));
            }
            if (!enchantments.isEmpty()) {
                builder.add(Keys.ITEM_ENCHANTMENTS, enchantments);
            }
        }

        final Optional<Integer> itemDurabilityOpt = Util.safeGetInt(this.data, "durability");
        if (itemDurabilityOpt.isPresent()) {
            builder.add(Keys.ITEM_DURABILITY, itemDurabilityOpt.get());
        }

        Optional<String> colorIdOpt = Util.safeGetString(this.data, "color");
        if (colorIdOpt.isPresent()) {
            Optional<DyeColor> colorOpt = Sponge.getRegistry().getType(DyeColor.class, colorIdOpt.get());
            if (colorOpt.isPresent()) {
                builder.add(Keys.DYE_COLOR, colorOpt.get());
            } else {
                this.plugin.getLogger().error("Could not retrieve DyeColor with ID " + colorIdOpt.get() + ". Skipping.");
            }
        }

        Optional<String> skullTypeStringOpt = Util.safeGetString(this.data, "skullType");
        if (skullTypeStringOpt.isPresent()) {
            final Optional<SkullType> skullTypeOpt = Sponge.getRegistry().getType(SkullType.class, skullTypeStringOpt.get());
            if (skullTypeOpt.isPresent()) {
                builder.add(Keys.SKULL_TYPE, skullTypeOpt.get());
            } else {
                this.plugin.getLogger().error("Could not retrieve SkullType with ID " + skullTypeStringOpt.get() + ". Skipping.");
            }
        }

        ItemStack item = builder.build();
        InventoryTransactionResult result = player.getInventory().offer(item);
        return result.getRejectedItems().isEmpty();
    }

}
