package io.badgeup.sponge.command.executor;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.HttpUtils;
import org.apache.http.HttpStatus;
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

            try {
                baneOfUndeadAchievement();
                this.src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Bane of the Undead achievement."));
            } catch (Exception e) {
                this.src.sendMessage(Text.of(TextColors.RED, "Failed to create Bane of the Undead achievement."));
                this.src.sendMessage(contactSupportMsg);
                e.printStackTrace();
            }

            try {
                clumsyAchievement();
                this.src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Clumsy achievement."));
            } catch (Exception e) {
                this.src.sendMessage(Text.of(TextColors.RED, "Failed to create Clumsy achievement."));
                this.src.sendMessage(contactSupportMsg);
                e.printStackTrace();
            }
        }

        private void meatLoverAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> rawPorkCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Porkchop")
                            .put(DESC, "Eat 1 Raw Porkchop")
                            .put(KEY, "^useitemstack:finish:minecraft:porkchop$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawPorkCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String rawPorkCritId = rawPorkCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> cookedPorkCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Cooked Porkchop")
                            .put(DESC, "Eat 1 Cooked Porkchop")
                            .put(KEY, "^useitemstack:finish:minecraft:cooked_porkchop$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(cookedPorkCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String cookedPorkCritId = cookedPorkCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> rawChickenCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Chicken")
                            .put(DESC, "Eat 1 Raw Chicken")
                            .put(KEY, "^useitemstack:finish:minecraft:chicken$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawChickenCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String rawChickenCritId = rawChickenCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> cookedChickenCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Cooked Chicken")
                            .put(DESC, "Eat 1 Cooked Chicken")
                            .put(KEY, "^useitemstack:finish:minecraft:cooked_chicken$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(cookedChickenCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String cookedChickenCritId = cookedChickenCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> rawMuttonCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Mutton")
                            .put(DESC, "Eat 1 Raw Mutton")
                            .put(KEY, "^useitemstack:finish:minecraft:mutton$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawMuttonCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String rawMuttonCritId = rawMuttonCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> cookedMuttonCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Cooked Mutton")
                            .put(DESC, "Eat 1 Cooked Mutton")
                            .put(KEY, "^useitemstack:finish:minecraft:cooked_mutton$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(cookedMuttonCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String cookedMuttonCritId = cookedMuttonCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> rawBeefCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Beef")
                            .put(DESC, "Eat 1 Raw Beef")
                            .put(KEY, "^useitemstack:finish:minecraft:beef$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawBeefCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String rawBeefCritId = rawBeefCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> cookedBeefCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Cooked Beef")
                            .put(DESC, "Eat 1 Cooked Beef")
                            .put(KEY, "^useitemstack:finish:minecraft:cooked_beef$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(cookedBeefCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String cookedBeefCritId = cookedBeefCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> rawRabbitCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Rabbit")
                            .put(DESC, "Eat 1 Raw Rabbit")
                            .put(KEY, "^useitemstack:finish:minecraft:rabbit$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawRabbitCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String rawRabbitCritId = rawRabbitCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> cookedRabbitCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Cooked Rabbit")
                            .put(DESC, "Eat 1 Cooked Rabbit")
                            .put(KEY, "^useitemstack:finish:minecraft:cooked_rabbit$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(cookedRabbitCritResponse.getStatus() == HttpStatus.SC_CREATED);
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
            Preconditions.checkArgument(steakAwardResponse.getStatus() == HttpStatus.SC_CREATED);
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
            Preconditions.checkArgument(achievementResponse.getStatus() == HttpStatus.SC_CREATED);

        }

        private void vegematicAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> appleCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Apple")
                            .put(DESC, "Eat 1 Apple")
                            .put(KEY, "^useitemstack:finish:minecraft:apple$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(appleCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String appleCritId = appleCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> goldenAppleCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Golden Apple")
                            .put(DESC, "Eat 1 Golden Apple")
                            .put(KEY, "^useitemstack:finish:minecraft:golden_apple$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(goldenAppleCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String goldenAppleCritId = goldenAppleCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> mushroomStewCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Mushroom Stew")
                            .put(DESC, "Eat 1 Mushroom Stew")
                            .put(KEY, "^useitemstack:finish:minecraft:mushroom_stew$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(mushroomStewCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String mushroomStewCritId = mushroomStewCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> melonCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Melon")
                            .put(DESC, "Eat 1 Melon")
                            .put(KEY, "^useitemstack:finish:minecraft:melon$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(melonCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String melonCritId = melonCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> carrotCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Carrot")
                            .put(DESC, "Eat 1 Carrot")
                            .put(KEY, "^useitemstack:finish:minecraft:carrot$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(carrotCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String carrotCritId = carrotCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> goldenCarrotCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Golden Carrot")
                            .put(DESC, "Eat 1 Golden Carrot")
                            .put(KEY, "^useitemstack:finish:minecraft:golden_carrot$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(goldenCarrotCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String goldenCarrotCritId = goldenCarrotCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> rawPotatoCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Raw Potato")
                            .put(DESC, "Eat 1 Raw Potato")
                            .put(KEY, "^useitemstack:finish:minecraft:potato$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(rawPotatoCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String rawPotatoCritId = rawPotatoCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> bakedPotatoCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Baked Potato")
                            .put(DESC, "Eat 1 Baked Mutton")
                            .put(KEY, "^useitemstack:finish:minecraft:baked_potato$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(bakedPotatoCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String bakedPotatoCritId = bakedPotatoCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> poisonousPotatoCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Poisonous Potato")
                            .put(DESC, "Eat 1 Poisonous Potato")
                            .put(KEY, "^useitemstack:finish:minecraft:poisonous_potato$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(poisonousPotatoCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String poisonousPotatoCritId = poisonousPotatoCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> beetrootCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Beetroot")
                            .put(DESC, "Eat 1 Beetroot")
                            .put(KEY, "^useitemstack:finish:minecraft:beetroot$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(beetrootCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String beetrootCritId = beetrootCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> beetrootSoupCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Beetroot Soup")
                            .put(DESC, "Eat 1 Beetroot Soup")
                            .put(KEY, "^useitemstack:finish:minecraft:beetroot_soup$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(beetrootSoupCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String beetrootSoupCritId = beetrootSoupCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> goldenAppleAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Golden Apple")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:golden_apple")
                                    .put("quantity", 1)))
                    .asJson();
            Preconditions.checkArgument(goldenAppleAwardResponse.getStatus() == HttpStatus.SC_CREATED);
            final String goldenAppleAwardId = goldenAppleAwardResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> goldenCarrotAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Golden Carrot")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:golden_carrot")
                                    .put("quantity", 1)))
                    .asJson();
            Preconditions.checkArgument(goldenCarrotAwardResponse.getStatus() == HttpStatus.SC_CREATED);
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
            Preconditions.checkArgument(achievementResponse.getStatus() == HttpStatus.SC_CREATED);

        }

        private void greenThumbAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> placeSaplingCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Place Saplings")
                            .put(DESC, "Place 10 Saplings")
                            .put(KEY, "^changeblock:place:minecraft:sapling$")
                            .put(EVALUATION, standardEvalBlock("@gte", 10)))
                    .asJson();
            Preconditions.checkArgument(placeSaplingCritResponse.getStatus() == HttpStatus.SC_CREATED);
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
            Preconditions.checkArgument(rainbowSheepAwardResponse.getStatus() == HttpStatus.SC_CREATED);
            final String rainbowSheepAwardId = rainbowSheepAwardResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> timeMachineAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Time Machine")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "command")
                                    .put("command", "time set day")))
                    .asJson();
            Preconditions.checkArgument(timeMachineAwardResponse.getStatus() == HttpStatus.SC_CREATED);
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
            Preconditions.checkArgument(achievementResponse.getStatus() == HttpStatus.SC_CREATED);

        }

        private void pyroAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> lightFireCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Light Fires")
                            .put(DESC, "Light a fire")
                            .put(KEY, "^changeblock:place:minecraft:fire$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(lightFireCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String lightFireCritId = lightFireCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> tntAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "TNT")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:tnt")
                                    .put("quantity", 10)))
                    .asJson();
            Preconditions.checkArgument(tntAwardResponse.getStatus() == HttpStatus.SC_CREATED);
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
            Preconditions.checkArgument(resistanceEffectAwardResponse.getStatus() == HttpStatus.SC_CREATED);
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
            Preconditions.checkArgument(achievementResponse.getStatus() == HttpStatus.SC_CREATED);

        }

        private void lumberjackAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> chopLogsCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Chop Trees")
                            .put(DESC, "Chop 50 Logs")
                            .put(KEY, "^changeblock:break:minecraft:log$")
                            .put(EVALUATION, standardEvalBlock("@gte", 50)))
                    .asJson();
            Preconditions.checkArgument(chopLogsCritResponse.getStatus() == HttpStatus.SC_CREATED);
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
            Preconditions.checkArgument(axeAwardResponse.getStatus() == HttpStatus.SC_CREATED);
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
            Preconditions.checkArgument(achievementResponse.getStatus() == HttpStatus.SC_CREATED);

        }

        private void moneybagsAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> dropGoldCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Drop Gold")
                            .put(DESC, "Drop a Gold Ingot")
                            .put(KEY, "^dropitem:dispense:minecraft:gold_ingot$")
                            .put(EVALUATION, standardEvalBlock("@gte", 1)))
                    .asJson();
            Preconditions.checkArgument(dropGoldCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String dropGoldCritId = dropGoldCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> moneyAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "100 Grand")
                            .put(DESC, "Better than the candy bar!")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "monetary")
                                    .put("amount", 100000)))
                    .asJson();
            Preconditions.checkArgument(moneyAwardResponse.getStatus() == HttpStatus.SC_CREATED);
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
            Preconditions.checkArgument(achievementResponse.getStatus() == HttpStatus.SC_CREATED);

        }

        private void oneMansTrashAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> pickupRottenFleshCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Pickup Rotten Flesh")
                            .put(DESC, "Pick up 100 Rotten Flesh")
                            .put(KEY, "^changeinventory:pickup:minecraft:rotten_flesh$")
                            .put(EVALUATION, standardEvalBlock("@gte", 100)))
                    .asJson();
            Preconditions.checkArgument(pickupRottenFleshCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String pickupFleshCritId = pickupRottenFleshCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> zombieSkullAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Zombie Skull")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:skull")
                                    .put("skullType", "zombie")))
                    .asJson();
            Preconditions.checkArgument(zombieSkullAwardResponse.getStatus() == HttpStatus.SC_CREATED);
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
            Preconditions.checkArgument(achievementResponse.getStatus() == HttpStatus.SC_CREATED);

        }

        private void baneOfUndeadAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> killMonstersCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Undead killer")
                            .put(DESC, "Slay 10 Skeletons or Zombies")
                            .put(KEY, "destructentity:death:hostile:minecraft:(zombie|skeleton)")
                            .put(EVALUATION, standardEvalBlock("@gte", 10)))
                    .asJson();
            Preconditions.checkArgument(killMonstersCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String killMonstersCritId = killMonstersCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> swordAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Zombie Ripper")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:diamond_sword")
                                    .put("displayName", "&cZombie Ripper")
                                    .put("enchantments", new JSONArray()
                                            .put(new JSONObject()
                                                    .put(ID, "minecraft:smite")
                                                    .put("level", 5)))))
                    .asJson();
            Preconditions.checkArgument(swordAwardResponse.getStatus() == HttpStatus.SC_CREATED);
            final String swordAwardId = swordAwardResponse.getBody().getObject().getString(ID);

            // Create the achievement
            HttpResponse<JsonNode> achievementResponse = HttpUtils.post("/achievements")
                    .body(new JSONObject()
                            .put(NAME, "Bane of the Undead")
                            .put(DESC, "Negan's got nothing on you!")
                            .put(EVAL_TREE, new JSONObject()
                                    .put(CONDITION, AND)
                                    .put(CRITERIA, new JSONArray()
                                            .put(killMonstersCritId))
                                    .put(TYPE, GROUP)
                                    .put(GROUPS, new JSONArray()))
                            .put(AWARDS, new JSONArray()
                                    .put(swordAwardId)))
                    .asJson();
            Preconditions.checkArgument(achievementResponse.getStatus() == HttpStatus.SC_CREATED);

        }

        private void clumsyAchievement() throws JSONException, UnirestException, IllegalStateException {
            HttpResponse<JsonNode> fallCritResponse = HttpUtils.post("/criteria")
                    .body(new JSONObject()
                            .put(NAME, "Clumsy")
                            .put(DESC, "Fall to your death 3 times")
                            .put(KEY, "^death:fall$")
                            .put(EVALUATION, standardEvalBlock("@gte", 3)))
                    .asJson();
            Preconditions.checkArgument(fallCritResponse.getStatus() == HttpStatus.SC_CREATED);
            final String fallCritId = fallCritResponse.getBody().getObject().getString(ID);

            HttpResponse<JsonNode> bootsAwardResponse = HttpUtils.post("/awards")
                    .body(new JSONObject()
                            .put(NAME, "Feather Falling Boots")
                            .put(DATA, new JSONObject()
                                    .put(TYPE, "item")
                                    .put("itemType", "minecraft:diamond_boots")
                                    .put("displayName", "&cSafety Net")
                                    .put("enchantments", new JSONArray()
                                            .put(new JSONObject()
                                                    .put(ID, "feather_falling")
                                                    .put("level", 4)))))
                    .asJson();
            Preconditions.checkArgument(bootsAwardResponse.getStatus() == HttpStatus.SC_CREATED);
            final String bootsAwardId = bootsAwardResponse.getBody().getObject().getString(ID);

            // Create the achievement
            HttpResponse<JsonNode> achievementResponse = HttpUtils.post("/achievements")
                    .body(new JSONObject()
                            .put(NAME, "Clumsy")
                            .put(EVAL_TREE, new JSONObject()
                                    .put(CONDITION, AND)
                                    .put(CRITERIA, new JSONArray()
                                            .put(fallCritId))
                                    .put(TYPE, GROUP)
                                    .put(GROUPS, new JSONArray()))
                            .put(AWARDS, new JSONArray()
                                    .put(bootsAwardId)))
                    .asJson();
            Preconditions.checkArgument(achievementResponse.getStatus() == HttpStatus.SC_CREATED);
        }

        private JSONObject standardEvalBlock(String operator, int threshold) {
            return new JSONObject()
                    .put(TYPE, TYPE_STANDARD)
                    .put(OPERATOR, operator)
                    .put(THRESHOLD, threshold);
        }
    }
}
