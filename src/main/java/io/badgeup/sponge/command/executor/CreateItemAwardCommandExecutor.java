package io.badgeup.sponge.command.executor;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.util.HttpUtils;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.type.SkullType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CreateItemAwardCommandExecutor implements CommandExecutor {

    private BadgeUpSponge plugin;

    public CreateItemAwardCommandExecutor(BadgeUpSponge plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "Player only!"));
            return CommandResult.success();
        }

        Player player = (Player) src;

        Optional<ItemStack> itemStackOpt = player.getItemInHand(HandTypes.MAIN_HAND);

        if (!itemStackOpt.isPresent()) {
            player.sendMessage(Text.of(TextColors.RED, "You must hold an item in your hand"));
            return CommandResult.success();
        }

        ItemStack itemStack = itemStackOpt.get();

        JSONObject awardData = new JSONObject()
                .put("type", "item")
                .put("itemType", itemStack.getItem().getId())
                .put("quantity", itemStack.getQuantity());

        Optional<Text> displayNameOpt = itemStack.get(Keys.DISPLAY_NAME);
        if (displayNameOpt.isPresent()) {
            awardData.put("displayName", TextSerializers.FORMATTING_CODE.serialize(displayNameOpt.get()));
        }

        Optional<List<Text>> loreOpt = itemStack.get(Keys.ITEM_LORE);
        if (loreOpt.isPresent()) {
            JSONArray lore = new JSONArray();
            for (Text line : loreOpt.get()) {
                lore.put(TextSerializers.FORMATTING_CODE.serialize(line));
            }
            awardData.put("lore", TextSerializers.FORMATTING_CODE.serialize(displayNameOpt.get()));
        }

        Optional<List<ItemEnchantment>> enchantmentsOpt = itemStack.get(Keys.ITEM_ENCHANTMENTS);
        if (enchantmentsOpt.isPresent()) {
            JSONArray enchantments = new JSONArray();
            for (ItemEnchantment enchant : enchantmentsOpt.get()) {
                enchantments.put(new JSONObject()
                        .put("id", enchant.getEnchantment().getId())
                        .put("level", enchant.getLevel()));
            }
            awardData.put("enchantments", enchantments);
        }

        Optional<Integer> durabilityOpt = itemStack.get(Keys.ITEM_DURABILITY);
        if (durabilityOpt.isPresent()) {
            awardData.put("durability", durabilityOpt.get());
        }

        Optional<DyeColor> dyeColorOpt = itemStack.get(Keys.DYE_COLOR);
        if (dyeColorOpt.isPresent()) {
            awardData.put("color", dyeColorOpt.get().getId());
        }

        Optional<SkullType> skullTypeOpt = itemStack.get(Keys.SKULL_TYPE);
        if (skullTypeOpt.isPresent()) {
            awardData.put("skullType", skullTypeOpt.get());
        }

        JSONObject awardBody = new JSONObject()
                .put("name", itemStack.get(Keys.DISPLAY_NAME).orElse(Text.of(itemStack.getTranslation().get(Locale.ENGLISH))).toPlain())
                .put("description", "Created by the BadgeUp Sponge Client on " + new Date().toString())
                .put("data", awardData);

        Sponge.getScheduler().createTaskBuilder().execute(new PostAwardRunnable(player, awardBody, this.plugin.getLogger())).async()
                .submit(this.plugin);

        return CommandResult.success();
    }

    class PostAwardRunnable implements Runnable {

        Player player;
        JSONObject body;
        Logger logger;

        public PostAwardRunnable(Player player, JSONObject awardBody, Logger logger) {
            this.player = player;
            this.body = awardBody;
            this.logger = logger;
        }

        @Override
        public void run() {

            try {
                Response response = HttpUtils.post("/awards", this.body);
                if (response.code() != HttpUtils.STATUS_CREATED) {
                    this.player.sendMessage(Text.of(TextColors.RED, "Failed to create award. See console for stacktrace."));
                    this.logger.error(
                            "Got response code " + response.code() + " when creating an award. Response body: " + response.body().string());
                    return;
                }

                JSONObject body = HttpUtils.parseBody(response);
                String awardId = body.getString("id");

                URL url = new URL("https://dashboard.badgeup.io/#/awards/edit/" + awardId);

                Text dashboardLink = Text.builder("here").color(TextColors.GOLD).onClick(TextActions.openUrl(url)).build();

                this.player.sendMessage(Text.of(TextColors.GREEN, "Successfully created award. View it on the dashboard ", dashboardLink));

            } catch (IOException e) {
                this.player.sendMessage(Text.of(TextColors.RED, "Failed to create award. See console for stacktrace."));
                e.printStackTrace();
            }

        }

    }

}
