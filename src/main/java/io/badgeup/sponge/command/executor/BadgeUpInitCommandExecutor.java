package io.badgeup.sponge.command.executor;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.HttpUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class BadgeUpInitCommandExecutor implements CommandExecutor {

    private BadgeUpSponge plugin;

    public BadgeUpInitCommandExecutor(BadgeUpSponge plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Sponge.getScheduler().createTaskBuilder().async().execute(new BadgeUpInitRunnable(src)).submit(this.plugin);
        return CommandResult.success();
    }

    class BadgeUpInitRunnable implements Runnable {

        private static final String ID = "id";
        private static final String NAME = "name";
        private static final String DESC = "description";
        private static final String KEY = "key";
        private static final String EVALUATION = "evaluation";
        private static final String TYPE = "type";
        private static final String TYPE_STANDARD = "standard";
        private static final String OPERATOR = "operator";
        private static final String THRESHOLD = "threshold";

        private static final String AWARDS = "awards";
        private static final String EVAL_TREE = "evalTree";
        private static final String CRITERIA = "criteria";
        private static final String GROUPS = "groups";
        private static final String CONDITION = "condition";
        private static final String AND = "AND";
        private static final String OR = "OR";
        private static final String GROUP = "GROUP";

        private static final String DATA = "data";

        private CommandSource src;

        public BadgeUpInitRunnable(CommandSource src) {
            this.src = src;
        }

        @Override
        public void run() {
            this.src.sendMessage(Text.of(TextColors.GREEN, "Creating demo achievements..."));

            final Text contactSupportMsg = Text.of("Please contact BadgeUp Support at support@badgeup.io with the error log in the console.");

            try {
                meatLoverAchievement();
                this.src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Meat Lover achievement."));
            } catch (Exception e) {
                this.src.sendMessage(Text.of(TextColors.RED, "Failed to create Meat Lover achievement."));
                this.src.sendMessage(contactSupportMsg);
                e.printStackTrace();
            }

            try {
                vegematicAchievement();
                this.src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Vegematic achievement."));
            } catch (Exception e) {
                this.src.sendMessage(Text.of(TextColors.RED, "Failed to create Vegematic achievement."));
                this.src.sendMessage(contactSupportMsg);
                e.printStackTrace();
            }

            try {
                greenThumbAchievement();
                this.src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Green Thumb achievement."));
            } catch (Exception e) {
                this.src.sendMessage(Text.of(TextColors.RED, "Failed to create Green Thumb achievement."));
                this.src.sendMessage(contactSupportMsg);
                e.printStackTrace();
            }

            try {
                pyroAchievement();
                this.src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Pyro achievement."));
            } catch (Exception e) {
                this.src.sendMessage(Text.of(TextColors.RED, "Failed to create Pyro achievement."));
                this.src.sendMessage(contactSupportMsg);
                e.printStackTrace();
            }

            try {
                lumberjackAchievement();
                this.src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Lumberjack achievement."));
            } catch (Exception e) {
                this.src.sendMessage(Text.of(TextColors.RED, "Failed to create Lumberjack achievement."));
                this.src.sendMessage(contactSupportMsg);
                e.printStackTrace();
            }

            try {
                moneybagsAchievement();
                this.src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Moneybags achievement."));
            } catch (Exception e) {
                this.src.sendMessage(Text.of(TextColors.RED, "Failed to create Moneybags achievement."));
                this.src.sendMessage(contactSupportMsg);
                e.printStackTrace();
            }

            try {
                oneMansTrashAchievement();
                this.src.sendMessage(Text.of(TextColors.GREEN, "Successfully created One Man's Trash achievement."));
            } catch (Exception e) {
                this.src.sendMessage(Text.of(TextColors.RED, "Failed to create One Man's Trash achievement."));
                this.src.sendMessage(contactSupportMsg);
                e.printStackTrace();
            }
        }

        private void meatLoverAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> rawPorkCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Porkchop")
                            .put(DESC, "Eat 1 Raw Porkchop")
                            .put(KEY, "useitemstack:finish:minecraft:porkchop")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawPorkCritResponse.getStatus() == 201);
            final String rawPorkCritId = rawPorkCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> cookedPorkCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Cooked Porkchop")
                            .put(DESC, "Eat 1 Cooked Porkchop")
                            .put(KEY, "useitemstack:finish:minecraft:cooked_porkchop")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(cookedPorkCritResponse.getStatus() == 201);
            final String cookedPorkCritId = cookedPorkCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> rawChickenCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Chicken")
                            .put(DESC, "Eat 1 Raw Chicken")
                            .put(KEY, "useitemstack:finish:minecraft:chicken")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawChickenCritResponse.getStatus() == 201);
            final String rawChickenCritId = rawChickenCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> cookedChickenCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Cooked Chicken")
                            .put(DESC, "Eat 1 Cooked Chicken")
                            .put(KEY, "useitemstack:finish:minecraft:cooked_chicken")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(cookedChickenCritResponse.getStatus() == 201);
            final String cookedChickenCritId = cookedChickenCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> rawMuttonCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Mutton")
                            .put(DESC, "Eat 1 Raw Mutton")
                            .put(KEY, "useitemstack:finish:minecraft:mutton")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawMuttonCritResponse.getStatus() == 201);
            final String rawMuttonCritId = rawMuttonCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> cookedMuttonCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Cooked Mutton")
                            .put(DESC, "Eat 1 Cooked Mutton")
                            .put(KEY, "useitemstack:finish:minecraft:cooked_mutton")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(cookedMuttonCritResponse.getStatus() == 201);
            final String cookedMuttonCritId = cookedMuttonCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> rawBeefCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Beef")
                            .put(DESC, "Eat 1 Raw Beef")
                            .put(KEY, "useitemstack:finish:minecraft:beef")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawBeefCritResponse.getStatus() == 201);
            final String rawBeefCritId = rawBeefCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> cookedBeefCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Cooked Beef")
                            .put(DESC, "Eat 1 Cooked Beef")
                            .put(KEY, "useitemstack:finish:minecraft:cooked_beef")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(cookedBeefCritResponse.getStatus() == 201);
            final String cookedBeefCritId = cookedBeefCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> rawRabbitCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Rabbit")
                            .put(DESC, "Eat 1 Raw Rabbit")
                            .put(KEY, "useitemstack:finish:minecraft:rabbit")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawRabbitCritResponse.getStatus() == 201);
            final String rawRabbitCritId = rawRabbitCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> cookedRabbitCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Cooked Rabbit")
                            .put(DESC, "Eat 1 Cooked Rabbit")
                            .put(KEY, "useitemstack:finish:minecraft:cooked_rabbit")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(cookedRabbitCritResponse.getStatus() == 201);
            final String cookedRabbitCritId = cookedRabbitCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> steakAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Where's the Meat?")
                            .put(DESC, "A feast to behold!")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:cooked_beef")
                                    .put("quantity", 64)))
                    .asJson();
            Preconditions.checkArgument(steakAwardResponse.getStatus() == 201);
            final String steakAwardId = steakAwardResponse.getBody().getObject().getString(ID);

            // Create the achievement
            HttpResponse<JsonNode> achievementResponse = HttpUtils.post("/achievements")
                    .body(new JSONObject()
                            .put(NAME, "Meat Lover")
                            .put(DESC, "Gotta get that protein!")
                            .put(EVAL_TREE, new JSONObject()
                                    .put(CONDITION, AND)
                                    .put(CRITERIA, new JSONArray())
                                    .put(TYPE, GROUP)
                                    .put(GROUPS, new JSONArray()
                                            .put(new JSONObject()
                                                    .put(CONDITION, OR)
                                                    .put(CRITERIA, new JSONArray()
                                                            .put(rawPorkCritId)
                                                            .put(cookedPorkCritId))
                                                    .put(GROUPS, new JSONArray())
                                                    .put(TYPE, GROUP))
                                            .put(new JSONObject()
                                                    .put(CONDITION, OR)
                                                    .put(CRITERIA, new JSONArray()
                                                            .put(rawChickenCritId)
                                                            .put(cookedChickenCritId))
                                                    .put(GROUPS, new JSONArray())
                                                    .put(TYPE, GROUP))
                                            .put(new JSONObject()
                                                    .put(CONDITION, OR)
                                                    .put(CRITERIA, new JSONArray()
                                                            .put(rawMuttonCritId)
                                                            .put(cookedMuttonCritId))
                                                    .put(GROUPS, new JSONArray())
                                                    .put(TYPE, GROUP))
                                            .put(new JSONObject()
                                                    .put(CONDITION, OR)
                                                    .put(CRITERIA, new JSONArray()
                                                            .put(rawBeefCritId)
                                                            .put(cookedBeefCritId))
                                                    .put(GROUPS, new JSONArray())
                                                    .put(TYPE, GROUP))
                                            .put(new JSONObject()
                                                    .put(CONDITION, OR)
                                                    .put(CRITERIA, new JSONArray()
                                                            .put(rawRabbitCritId)
                                                            .put(cookedRabbitCritId))
                                                    .put(GROUPS, new JSONArray())
                                                    .put(TYPE, GROUP))))
                            .put(AWARDS, new JSONArray().put(steakAwardId)))
                    .asJson();
            Preconditions.checkArgument(achievementResponse.getStatus() == 201);

        }

        private void vegematicAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> appleCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Apple")
                            .put(DESC, "Eat 1 Apple")
                            .put(KEY, "useitemstack:finish:minecraft:apple")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(appleCritResponse.getStatus() == 201);
            final String appleCritId = appleCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> goldenAppleCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Golden Apple")
                            .put(DESC, "Eat 1 Golden Apple")
                            .put(KEY, "useitemstack:finish:minecraft:golden_apple")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(goldenAppleCritResponse.getStatus() == 201);
            final String goldenAppleCritId = goldenAppleCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> mushroomStewCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Mushroom Stew")
                            .put(DESC, "Eat 1 Mushroom Stew")
                            .put(KEY, "useitemstack:finish:minecraft:mushroom_stew")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(mushroomStewCritResponse.getStatus() == 201);
            final String mushroomStewCritId = mushroomStewCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> melonCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Melon")
                            .put(DESC, "Eat 1 Melon")
                            .put(KEY, "useitemstack:finish:minecraft:melon")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(melonCritResponse.getStatus() == 201);
            final String melonCritId = melonCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> carrotCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Carrot")
                            .put(DESC, "Eat 1 Carrot")
                            .put(KEY, "useitemstack:finish:minecraft:carrot")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(carrotCritResponse.getStatus() == 201);
            final String carrotCritId = carrotCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> goldenCarrotCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Golden Carrot")
                            .put(DESC, "Eat 1 Golden Carrot")
                            .put(KEY, "useitemstack:finish:minecraft:golden_carrot")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(goldenCarrotCritResponse.getStatus() == 201);
            final String goldenCarrotCritId = goldenCarrotCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> rawPotatoCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Potato")
                            .put(DESC, "Eat 1 Raw Potato")
                            .put(KEY, "useitemstack:finish:minecraft:potato")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawPotatoCritResponse.getStatus() == 201);
            final String rawPotatoCritId = rawPotatoCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> bakedPotatoCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Baked Potato")
                            .put(DESC, "Eat 1 Baked Mutton")
                            .put(KEY, "useitemstack:finish:minecraft:baked_potato")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(bakedPotatoCritResponse.getStatus() == 201);
            final String bakedPotatoCritId = bakedPotatoCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> poisonousPotatoCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Poisonous Potato")
                            .put(DESC, "Eat 1 Poisonous Potato")
                            .put(KEY, "useitemstack:finish:minecraft:poisonous_potato")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(poisonousPotatoCritResponse.getStatus() == 201);
            final String poisonousPotatoCritId = poisonousPotatoCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> beetrootCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Beetroot")
                            .put(DESC, "Eat 1 Beetroot")
                            .put(KEY, "useitemstack:finish:minecraft:beetroot")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(beetrootCritResponse.getStatus() == 201);
            final String beetrootCritId = beetrootCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> beetrootSoupCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Beetroot Soup")
                            .put(DESC, "Eat 1 Beetroot Soup")
                            .put(KEY, "useitemstack:finish:minecraft:beetroot_soup")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(beetrootSoupCritResponse.getStatus() == 201);
            final String beetrootSoupCritId = beetrootSoupCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> goldenAppleAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Golden Apple")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:golden_apple")
                                    .put("quantity", 1)))
                    .asJson();
            Preconditions.checkArgument(goldenAppleAwardResponse.getStatus() == 201);
            final String goldenAppleAwardId = goldenAppleAwardResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> goldenCarrotAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Golden Carrot")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:golden_carrot")
                                    .put("quantity", 1)))
                    .asJson();
            Preconditions.checkArgument(goldenCarrotAwardResponse.getStatus() == 201);
            final String goldenCarrotAwardId = goldenCarrotAwardResponse.getBody().getObject().getString(ID);

            // Create the achievement
            HttpResponse<JsonNode> achievementResponse = HttpUtils.post("/achievements")
                    .body(new JSONObject()
                            .put(NAME, "Vegematic")
                            .put(DESC, "Is that a leaf growing from your head?")
                            .put(EVAL_TREE, new JSONObject()
                                    .put(CONDITION, AND)
                                    .put(CRITERIA, new JSONArray()
                                            .put(mushroomStewCritId)
                                            .put(melonCritId))
                                    .put(TYPE, GROUP)
                                    .put(GROUPS, new JSONArray()
                                            .put(new JSONObject()
                                                    .put(CONDITION, OR)
                                                    .put(CRITERIA, new JSONArray()
                                                            .put(appleCritId)
                                                            .put(goldenAppleCritId))
                                                    .put(GROUPS, new JSONArray())
                                                    .put(TYPE, GROUP))
                                            .put(new JSONObject()
                                                    .put(CONDITION, OR)
                                                    .put(CRITERIA, new JSONArray()
                                                            .put(carrotCritId)
                                                            .put(goldenCarrotCritId))
                                                    .put(GROUPS, new JSONArray())
                                                    .put(TYPE, GROUP))
                                            .put(new JSONObject()
                                                    .put(CONDITION, OR)
                                                    .put(CRITERIA, new JSONArray()
                                                            .put(rawPotatoCritId)
                                                            .put(bakedPotatoCritId)
                                                            .put(poisonousPotatoCritId))
                                                    .put(GROUPS, new JSONArray())
                                                    .put(TYPE, GROUP))
                                            .put(new JSONObject()
                                                    .put(CONDITION, OR)
                                                    .put(CRITERIA, new JSONArray()
                                                            .put(beetrootCritId)
                                                            .put(beetrootSoupCritId))
                                                    .put(GROUPS, new JSONArray())
                                                    .put(TYPE, GROUP))))
                            .put(AWARDS, new JSONArray()
                                    .put(goldenAppleAwardId)
                                    .put(goldenCarrotAwardId)))
                    .asJson();
            Preconditions.checkArgument(achievementResponse.getStatus() == 201);

        }

        private void greenThumbAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> placeSaplingCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Place Saplings")
                            .put(DESC, "Place 10 Saplings")
                            .put(KEY, "changeblock:place:minecraft:sapling")
                            .put(EVALUATION, standardEvalBlock("@gte", 10)))
                    .asJson();
            Preconditions.checkArgument(placeSaplingCritResponse.getStatus() == 201);
            final String placeSaplingCritId = placeSaplingCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> rainbowSheepAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Rainbow Sheep")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "entity")
                                    .put("entityType", "minecraft:sheep")
                                    .put("position", new JSONObject().put("x", "~").put("y", "~").put("z", "~"))
                                    .put("displayName", "jeb_")))
                    .asJson();
            Preconditions.checkArgument(rainbowSheepAwardResponse.getStatus() == 201);
            final String rainbowSheepAwardId = rainbowSheepAwardResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> timeMachineAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Time Machine")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "command")
                                    .put("command", "time set day")))
                    .asJson();
            Preconditions.checkArgument(timeMachineAwardResponse.getStatus() == 201);
            final String timeMachineAwardId = timeMachineAwardResponse.getBody().getObject().getString(ID);

            // Create the achievement
            HttpResponse<JsonNode> achievementResponse = HttpUtils.post("/achievements")
                    .body(new JSONObject()
                            .put(NAME, "Green Thumb")
                            .put(DESC, "That deep-rooted desire")
                            .put(EVAL_TREE, new JSONObject()
                                    .put(CONDITION, AND)
                                    .put(CRITERIA, new JSONArray()
                                            .put(placeSaplingCritId))
                                    .put(TYPE, GROUP)
                                    .put(GROUPS, new JSONArray()))
                            .put(AWARDS, new JSONArray()
                                    .put(rainbowSheepAwardId)
                                    .put(timeMachineAwardId)))
                    .asJson();
            Preconditions.checkArgument(achievementResponse.getStatus() == 201);

        }

        private void pyroAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> lightFireCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Light Fires")
                            .put(DESC, "Light a fire")
                            .put(KEY, "changeblock:place:minecraft:fire")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(lightFireCritResponse.getStatus() == 201);
            final String lightFireCritId = lightFireCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> tntAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "TNT")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:tnt")
                                    .put("quantity", 10)))
                    .asJson();
            Preconditions.checkArgument(tntAwardResponse.getStatus() == 201);
            final String tntAwardId = tntAwardResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> resistanceEffectAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Resistance Potion Effect")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "potion")
                                    .put("potionEffectType", "minecraft:resistance")
                                    .put("duration", 2400) // 2 minutes
                                    .put("amplifier", 4)))
                    .asJson();
            Preconditions.checkArgument(resistanceEffectAwardResponse.getStatus() == 201);
            final String resistanceEffectAwardId = resistanceEffectAwardResponse.getBody().getObject().getString(ID);

            // Create the achievement
            HttpResponse<JsonNode> achievementResponse = HttpUtils.post("/achievements")
                    .body(new JSONObject()
                            .put(NAME, "Pyro")
                            .put(DESC, "Some people just want to watch the world burn")
                            .put(EVAL_TREE, new JSONObject()
                                    .put(CONDITION, AND)
                                    .put(CRITERIA, new JSONArray()
                                            .put(lightFireCritId))
                                    .put(TYPE, GROUP)
                                    .put(GROUPS, new JSONArray()))
                            .put(AWARDS, new JSONArray()
                                    .put(tntAwardId)
                                    .put(resistanceEffectAwardId)))
                    .asJson();
            Preconditions.checkArgument(achievementResponse.getStatus() == 201);

        }

        private void lumberjackAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> chopLogsCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Chop Trees")
                            .put(DESC, "Chop 50 Logs")
                            .put(KEY, "changeblock:break:minecraft:log")
                            .put(EVALUATION, standardEvalBlock("@gte", 50)))
                    .asJson();
            Preconditions.checkArgument(chopLogsCritResponse.getStatus() == 201);
            final String chopLogsCritId = chopLogsCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> axeAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Chainsaw")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:diamond_axe")
                                    .put("displayName", "&6Chainsaw")
                                    .put("enchantments", new JSONArray()
                                            .put(new JSONObject()
                                                    .put(ID, "minecraft:efficiency")
                                                    .put("level", 5))
                                            .put(new JSONObject()
                                                    .put(ID, "minecraft:unbreaking")
                                                    .put("level", 3)))))
                    .asJson();
            Preconditions.checkArgument(axeAwardResponse.getStatus() == 201);
            final String axeAwardId = axeAwardResponse.getBody().getObject().getString(ID);

            // Create the achievement
            HttpResponse<JsonNode> achievementResponse = HttpUtils.post("/achievements")
                    .body(new JSONObject()
                            .put(NAME, "Lumberjack")
                            .put(DESC, "Haven't you heard of email?")
                            .put(EVAL_TREE, new JSONObject()
                                    .put(CONDITION, AND)
                                    .put(CRITERIA, new JSONArray()
                                            .put(chopLogsCritId))
                                    .put(TYPE, GROUP)
                                    .put(GROUPS, new JSONArray()))
                            .put(AWARDS, new JSONArray()
                                    .put(axeAwardId)))
                    .asJson();
            Preconditions.checkArgument(achievementResponse.getStatus() == 201);

        }

        private void moneybagsAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> dropGoldCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Drop Gold")
                            .put(DESC, "Drop a Gold Ingot")
                            .put(KEY, "dropitem:dispense:minecraft:gold_ingot")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(dropGoldCritResponse.getStatus() == 201);
            final String dropGoldCritId = dropGoldCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> moneyAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "100 Grand")
                            .put(DESC, "Better than the candy bar!")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "monetary")
                                    .put("amount", 100000)))
                    .asJson();
            Preconditions.checkArgument(moneyAwardResponse.getStatus() == 201);
            final String moneyAwardId = moneyAwardResponse.getBody().getObject().getString(ID);

            // Create the achievement
            HttpResponse<JsonNode> achievementResponse = HttpUtils.post("/achievements")
                    .body(new JSONObject()
                            .put(NAME, "Moneybags")
                            .put(DESC, "'Let me get that for you!'")
                            .put(EVAL_TREE, new JSONObject()
                                    .put(CONDITION, AND)
                                    .put(CRITERIA, new JSONArray()
                                            .put(dropGoldCritId))
                                    .put(TYPE, GROUP)
                                    .put(GROUPS, new JSONArray()))
                            .put(AWARDS, new JSONArray()
                                    .put(moneyAwardId)))
                    .asJson();
            Preconditions.checkArgument(achievementResponse.getStatus() == 201);

        }

        private void oneMansTrashAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> pickupRottenFleshCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Pickup Rotten Flesh")
                            .put(DESC, "Pick up 100 Rotten Flesh")
                            .put(KEY, "changeinventory:pickup:minecraft:rotten_flesh")
                            .put(EVALUATION, standardEvalBlock("@gte", 100)))
                    .asJson();
            Preconditions.checkArgument(pickupRottenFleshCritResponse.getStatus() == 201);
            final String pickupFleshCritId = pickupRottenFleshCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> zombieSkullAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Zombie Skull")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:skull")
                                    .put("skullType", "zombie")))
                    .asJson();
            Preconditions.checkArgument(zombieSkullAwardResponse.getStatus() == 201);
            final String zombieSkullAwardId = zombieSkullAwardResponse.getBody().getObject().getString(ID);

            // Create the achievement
            HttpResponse<JsonNode> achievementResponse = HttpUtils.post("/achievements")
                    .body(new JSONObject()
                            .put(NAME, "One Man's Trash")
                            .put(DESC, "Never know when you might need it!")
                            .put(EVAL_TREE, new JSONObject()
                                    .put(CONDITION, AND)
                                    .put(CRITERIA, new JSONArray()
                                            .put(pickupFleshCritId))
                                    .put(TYPE, GROUP)
                                    .put(GROUPS, new JSONArray()))
                            .put(AWARDS, new JSONArray()
                                    .put(zombieSkullAwardId)))
                    .asJson();
            Preconditions.checkArgument(achievementResponse.getStatus() == 201);

        }

        private JSONObject standardEvalBlock(String operator, int threshold) {
            return new JSONObject()
                    .put(TYPE, TYPE_STANDARD)
                    .put(OPERATOR, operator)
                    .put(THRESHOLD, threshold);
        }
    }
}
