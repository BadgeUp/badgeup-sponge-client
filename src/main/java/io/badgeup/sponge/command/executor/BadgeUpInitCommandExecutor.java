package io.badgeup.sponge.command.executor;

import com.google.common.base.Preconditions;
import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.util.HttpUtils;
import okhttp3.Response;
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

import java.io.IOException;

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

        private void meatLoverAchievement() throws JSONException, IOException, IllegalStateException {
            Response rawPorkCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Raw Porkchop")
                    .put(DESC, "Eat 1 Raw Porkchop")
                    .put(KEY, "^useitemstack:finish:minecraft:porkchop$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(rawPorkCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String rawPorkCritId = HttpUtils.parseBody(rawPorkCritResponse).getString(ID);

            Response cookedPorkCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Cooked Porkchop")
                    .put(DESC, "Eat 1 Cooked Porkchop")
                    .put(KEY, "^useitemstack:finish:minecraft:cooked_porkchop$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(cookedPorkCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String cookedPorkCritId = HttpUtils.parseBody(cookedPorkCritResponse).getString(ID);

            Response rawChickenCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Raw Chicken")
                    .put(DESC, "Eat 1 Raw Chicken")
                    .put(KEY, "^useitemstack:finish:minecraft:chicken$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(rawChickenCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String rawChickenCritId = HttpUtils.parseBody(rawChickenCritResponse).getString(ID);

            Response cookedChickenCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Cooked Chicken")
                    .put(DESC, "Eat 1 Cooked Chicken")
                    .put(KEY, "^useitemstack:finish:minecraft:cooked_chicken$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(cookedChickenCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String cookedChickenCritId = HttpUtils.parseBody(cookedChickenCritResponse).getString(ID);

            Response rawMuttonCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Raw Mutton")
                    .put(DESC, "Eat 1 Raw Mutton")
                    .put(KEY, "^useitemstack:finish:minecraft:mutton$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(rawMuttonCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String rawMuttonCritId = HttpUtils.parseBody(rawMuttonCritResponse).getString(ID);

            Response cookedMuttonCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Cooked Mutton")
                    .put(DESC, "Eat 1 Cooked Mutton")
                    .put(KEY, "^useitemstack:finish:minecraft:cooked_mutton$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(cookedMuttonCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String cookedMuttonCritId = HttpUtils.parseBody(cookedMuttonCritResponse).getString(ID);

            Response rawBeefCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Raw Beef")
                    .put(DESC, "Eat 1 Raw Beef")
                    .put(KEY, "^useitemstack:finish:minecraft:beef$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(rawBeefCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String rawBeefCritId = HttpUtils.parseBody(rawBeefCritResponse).getString(ID);

            Response cookedBeefCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Cooked Beef")
                    .put(DESC, "Eat 1 Cooked Beef")
                    .put(KEY, "^useitemstack:finish:minecraft:cooked_beef$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(cookedBeefCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String cookedBeefCritId = HttpUtils.parseBody(cookedBeefCritResponse).getString(ID);

            Response rawRabbitCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Raw Rabbit")
                    .put(DESC, "Eat 1 Raw Rabbit")
                    .put(KEY, "^useitemstack:finish:minecraft:rabbit$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(rawRabbitCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String rawRabbitCritId = HttpUtils.parseBody(rawRabbitCritResponse).getString(ID);

            Response cookedRabbitCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Cooked Rabbit")
                    .put(DESC, "Eat 1 Cooked Rabbit")
                    .put(KEY, "^useitemstack:finish:minecraft:cooked_rabbit$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(cookedRabbitCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String cookedRabbitCritId = HttpUtils.parseBody(cookedRabbitCritResponse).getString(ID);

            Response steakAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "Where's the Meat?")
                    .put(DESC, "A feast to behold!")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "item")
                            .put("itemType", "minecraft:cooked_beef")
                            .put("quantity", 64)));
            Preconditions.checkArgument(steakAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String steakAwardId = HttpUtils.parseBody(steakAwardResponse).getString(ID);

            // Create the achievement
            Response achievementResponse = HttpUtils.post("/achievements", new JSONObject()
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
                    .put(AWARDS, new JSONArray().put(steakAwardId)));
            Preconditions.checkArgument(achievementResponse.code() == HttpUtils.STATUS_CREATED);

        }

        private void vegematicAchievement() throws JSONException, IOException, IllegalStateException {
            Response appleCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Apple")
                    .put(DESC, "Eat 1 Apple")
                    .put(KEY, "^useitemstack:finish:minecraft:apple$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(appleCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String appleCritId = HttpUtils.parseBody(appleCritResponse).getString(ID);

            Response goldenAppleCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Golden Apple")
                    .put(DESC, "Eat 1 Golden Apple")
                    .put(KEY, "^useitemstack:finish:minecraft:golden_apple$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(goldenAppleCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String goldenAppleCritId = HttpUtils.parseBody(goldenAppleCritResponse).getString(ID);

            Response mushroomStewCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Mushroom Stew")
                    .put(DESC, "Eat 1 Mushroom Stew")
                    .put(KEY, "^useitemstack:finish:minecraft:mushroom_stew$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(mushroomStewCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String mushroomStewCritId = HttpUtils.parseBody(mushroomStewCritResponse).getString(ID);

            Response melonCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Melon")
                    .put(DESC, "Eat 1 Melon")
                    .put(KEY, "^useitemstack:finish:minecraft:melon$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(melonCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String melonCritId = HttpUtils.parseBody(melonCritResponse).getString(ID);

            Response carrotCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Carrot")
                    .put(DESC, "Eat 1 Carrot")
                    .put(KEY, "^useitemstack:finish:minecraft:carrot$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(carrotCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String carrotCritId = HttpUtils.parseBody(carrotCritResponse).getString(ID);

            Response goldenCarrotCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Golden Carrot")
                    .put(DESC, "Eat 1 Golden Carrot")
                    .put(KEY, "^useitemstack:finish:minecraft:golden_carrot$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(goldenCarrotCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String goldenCarrotCritId = HttpUtils.parseBody(goldenCarrotCritResponse).getString(ID);

            Response rawPotatoCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Raw Potato")
                    .put(DESC, "Eat 1 Raw Potato")
                    .put(KEY, "^useitemstack:finish:minecraft:potato$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(rawPotatoCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String rawPotatoCritId = HttpUtils.parseBody(rawPotatoCritResponse).getString(ID);

            Response bakedPotatoCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Baked Potato")
                    .put(DESC, "Eat 1 Baked Mutton")
                    .put(KEY, "^useitemstack:finish:minecraft:baked_potato$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(bakedPotatoCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String bakedPotatoCritId = HttpUtils.parseBody(bakedPotatoCritResponse).getString(ID);

            Response poisonousPotatoCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Poisonous Potato")
                    .put(DESC, "Eat 1 Poisonous Potato")
                    .put(KEY, "^useitemstack:finish:minecraft:poisonous_potato$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(poisonousPotatoCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String poisonousPotatoCritId = HttpUtils.parseBody(poisonousPotatoCritResponse).getString(ID);

            Response beetrootCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Beetroot")
                    .put(DESC, "Eat 1 Beetroot")
                    .put(KEY, "^useitemstack:finish:minecraft:beetroot$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(beetrootCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String beetrootCritId = HttpUtils.parseBody(beetrootCritResponse).getString(ID);

            Response beetrootSoupCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Beetroot Soup")
                    .put(DESC, "Eat 1 Beetroot Soup")
                    .put(KEY, "^useitemstack:finish:minecraft:beetroot_soup$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(beetrootSoupCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String beetrootSoupCritId = HttpUtils.parseBody(beetrootSoupCritResponse).getString(ID);

            Response goldenAppleAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "Golden Apple")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "item")
                            .put("itemType", "minecraft:golden_apple")
                            .put("quantity", 1)));
            Preconditions.checkArgument(goldenAppleAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String goldenAppleAwardId = HttpUtils.parseBody(goldenAppleAwardResponse).getString(ID);

            Response goldenCarrotAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "Golden Carrot")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "item")
                            .put("itemType", "minecraft:golden_carrot")
                            .put("quantity", 1)));
            Preconditions.checkArgument(goldenCarrotAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String goldenCarrotAwardId = HttpUtils.parseBody(goldenCarrotAwardResponse).getString(ID);

            // Create the achievement
            Response achievementResponse = HttpUtils.post("/achievements", new JSONObject()
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
                            .put(goldenCarrotAwardId)));
            Preconditions.checkArgument(achievementResponse.code() == HttpUtils.STATUS_CREATED);

        }

        private void greenThumbAchievement() throws JSONException, IOException, IllegalStateException {
            Response placeSaplingCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Place Saplings")
                    .put(DESC, "Place 10 Saplings")
                    .put(KEY, "^changeblock:place:minecraft:sapling$")
                    .put(EVALUATION, standardEvalBlock("@gte", 10)));
            Preconditions.checkArgument(placeSaplingCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String placeSaplingCritId = HttpUtils.parseBody(placeSaplingCritResponse).getString(ID);

            Response rainbowSheepAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "Rainbow Sheep")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "entity")
                            .put("entityType", "minecraft:sheep")
                            .put("position", new JSONObject().put("x", "~").put("y", "~").put("z", "~"))
                            .put("displayName", "jeb_")));
            Preconditions.checkArgument(rainbowSheepAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String rainbowSheepAwardId = HttpUtils.parseBody(rainbowSheepAwardResponse).getString(ID);

            Response timeMachineAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "Time Machine")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "command")
                            .put("command", "time set day")));
            Preconditions.checkArgument(timeMachineAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String timeMachineAwardId = HttpUtils.parseBody(timeMachineAwardResponse).getString(ID);

            // Create the achievement
            Response achievementResponse = HttpUtils.post("/achievements", new JSONObject()
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
                            .put(timeMachineAwardId)));
            Preconditions.checkArgument(achievementResponse.code() == HttpUtils.STATUS_CREATED);

        }

        private void pyroAchievement() throws JSONException, IOException, IllegalStateException {
            Response lightFireCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Light Fires")
                    .put(DESC, "Light a fire")
                    .put(KEY, "^changeblock:place:minecraft:fire$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(lightFireCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String lightFireCritId = HttpUtils.parseBody(lightFireCritResponse).getString(ID);

            Response tntAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "TNT")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "item")
                            .put("itemType", "minecraft:tnt")
                            .put("quantity", 10)));
            Preconditions.checkArgument(tntAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String tntAwardId = HttpUtils.parseBody(tntAwardResponse).getString(ID);

            Response resistanceEffectAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "Resistance Potion Effect")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "potion")
                            .put("potionEffectType", "minecraft:resistance")
                            .put("duration", 2400) // 2 minutes
                            .put("amplifier", 4)));
            Preconditions.checkArgument(resistanceEffectAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String resistanceEffectAwardId = HttpUtils.parseBody(resistanceEffectAwardResponse).getString(ID);

            // Create the achievement
            Response achievementResponse = HttpUtils.post("/achievements", new JSONObject()
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
                            .put(resistanceEffectAwardId)));
            Preconditions.checkArgument(achievementResponse.code() == HttpUtils.STATUS_CREATED);

        }

        private void lumberjackAchievement() throws JSONException, IOException, IllegalStateException {
            Response chopLogsCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Chop Trees")
                    .put(DESC, "Chop 50 Logs")
                    .put(KEY, "^changeblock:break:minecraft:log$")
                    .put(EVALUATION, standardEvalBlock("@gte", 50)));
            Preconditions.checkArgument(chopLogsCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String chopLogsCritId = HttpUtils.parseBody(chopLogsCritResponse).getString(ID);

            Response axeAwardResponse = HttpUtils.post("/awards", new JSONObject()
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
                                            .put("level", 3)))));
            Preconditions.checkArgument(axeAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String axeAwardId = HttpUtils.parseBody(axeAwardResponse).getString(ID);

            // Create the achievement
            Response achievementResponse = HttpUtils.post("/achievements", new JSONObject()
                    .put(NAME, "Lumberjack")
                    .put(DESC, "Haven't you heard of email?")
                    .put(EVAL_TREE, new JSONObject()
                            .put(CONDITION, AND)
                            .put(CRITERIA, new JSONArray()
                                    .put(chopLogsCritId))
                            .put(TYPE, GROUP)
                            .put(GROUPS, new JSONArray()))
                    .put(AWARDS, new JSONArray()
                            .put(axeAwardId)));
            Preconditions.checkArgument(achievementResponse.code() == HttpUtils.STATUS_CREATED);

        }

        private void moneybagsAchievement() throws JSONException, IOException, IllegalStateException {
            Response dropGoldCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Drop Gold")
                    .put(DESC, "Drop a Gold Ingot")
                    .put(KEY, "^dropitem:dispense:minecraft:gold_ingot$")
                    .put(EVALUATION, standardEvalBlock("@gte", 1)));
            Preconditions.checkArgument(dropGoldCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String dropGoldCritId = HttpUtils.parseBody(dropGoldCritResponse).getString(ID);

            Response moneyAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "100 Grand")
                    .put(DESC, "Better than the candy bar!")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "monetary")
                            .put("amount", 100000)));
            Preconditions.checkArgument(moneyAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String moneyAwardId = HttpUtils.parseBody(moneyAwardResponse).getString(ID);

            // Create the achievement
            Response achievementResponse = HttpUtils.post("/achievements", new JSONObject()
                    .put(NAME, "Moneybags")
                    .put(DESC, "'Let me get that for you!'")
                    .put(EVAL_TREE, new JSONObject()
                            .put(CONDITION, AND)
                            .put(CRITERIA, new JSONArray()
                                    .put(dropGoldCritId))
                            .put(TYPE, GROUP)
                            .put(GROUPS, new JSONArray()))
                    .put(AWARDS, new JSONArray()
                            .put(moneyAwardId)));
            Preconditions.checkArgument(achievementResponse.code() == HttpUtils.STATUS_CREATED);

        }

        private void oneMansTrashAchievement() throws JSONException, IOException, IllegalStateException {
            Response pickupRottenFleshCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Pickup Rotten Flesh")
                    .put(DESC, "Pick up 100 Rotten Flesh")
                    .put(KEY, "^changeinventory:pickup:minecraft:rotten_flesh$")
                    .put(EVALUATION, standardEvalBlock("@gte", 100)));
            Preconditions.checkArgument(pickupRottenFleshCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String pickupFleshCritId = HttpUtils.parseBody(pickupRottenFleshCritResponse).getString(ID);

            Response zombieSkullAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "Zombie Skull")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "item")
                            .put("itemType", "minecraft:skull")
                            .put("skullType", "zombie")));
            Preconditions.checkArgument(zombieSkullAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String zombieSkullAwardId = HttpUtils.parseBody(zombieSkullAwardResponse).getString(ID);

            // Create the achievement
            Response achievementResponse = HttpUtils.post("/achievements", new JSONObject()
                    .put(NAME, "One Man's Trash")
                    .put(DESC, "Never know when you might need it!")
                    .put(EVAL_TREE, new JSONObject()
                            .put(CONDITION, AND)
                            .put(CRITERIA, new JSONArray()
                                    .put(pickupFleshCritId))
                            .put(TYPE, GROUP)
                            .put(GROUPS, new JSONArray()))
                    .put(AWARDS, new JSONArray()
                            .put(zombieSkullAwardId)));
            Preconditions.checkArgument(achievementResponse.code() == HttpUtils.STATUS_CREATED);

        }

        private void baneOfUndeadAchievement() throws JSONException, IOException, IllegalStateException {
            Response killMonstersCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Undead killer")
                    .put(DESC, "Slay 10 Skeletons or Zombies")
                    .put(KEY, "destructentity:death:hostile:minecraft:(zombie|skeleton)")
                    .put(EVALUATION, standardEvalBlock("@gte", 10)));
            Preconditions.checkArgument(killMonstersCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String killMonstersCritId = HttpUtils.parseBody(killMonstersCritResponse).getString(ID);

            Response swordAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "Zombie Ripper")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "item")
                            .put("itemType", "minecraft:diamond_sword")
                            .put("displayName", "&cZombie Ripper")
                            .put("enchantments", new JSONArray()
                                    .put(new JSONObject()
                                            .put(ID, "minecraft:smite")
                                            .put("level", 5)))));
            Preconditions.checkArgument(swordAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String swordAwardId = HttpUtils.parseBody(swordAwardResponse).getString(ID);

            // Create the achievement
            Response achievementResponse = HttpUtils.post("/achievements", new JSONObject()
                    .put(NAME, "Bane of the Undead")
                    .put(DESC, "Negan's got nothing on you!")
                    .put(EVAL_TREE, new JSONObject()
                            .put(CONDITION, AND)
                            .put(CRITERIA, new JSONArray()
                                    .put(killMonstersCritId))
                            .put(TYPE, GROUP)
                            .put(GROUPS, new JSONArray()))
                    .put(AWARDS, new JSONArray()
                            .put(swordAwardId)));
            Preconditions.checkArgument(achievementResponse.code() == HttpUtils.STATUS_CREATED);

        }

        private void clumsyAchievement() throws JSONException, IOException, IllegalStateException {
            Response fallCritResponse = HttpUtils.post("/criteria", new JSONObject()
                    .put(NAME, "Clumsy")
                    .put(DESC, "Fall to your death 3 times")
                    .put(KEY, "^death:fall$")
                    .put(EVALUATION, standardEvalBlock("@gte", 3)));
            Preconditions.checkArgument(fallCritResponse.code() == HttpUtils.STATUS_CREATED);
            final String fallCritId = HttpUtils.parseBody(fallCritResponse).getString(ID);

            Response bootsAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "Feather Falling Boots")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "item")
                            .put("itemType", "minecraft:diamond_boots")
                            .put("displayName", "&cSafety Net")
                            .put("enchantments", new JSONArray()
                                    .put(new JSONObject()
                                            .put(ID, "feather_falling")
                                            .put("level", 4)))));
            Preconditions.checkArgument(bootsAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String bootsAwardId = HttpUtils.parseBody(bootsAwardResponse).getString(ID);

            Response expAwardResponse = HttpUtils.post("/awards", new JSONObject()
                    .put(NAME, "Replacement Experience")
                    .put(DATA, new JSONObject()
                            .put(TYPE, "experience")
                            .put("amount", 500)));
            Preconditions.checkArgument(expAwardResponse.code() == HttpUtils.STATUS_CREATED);
            final String expAwardId = HttpUtils.parseBody(expAwardResponse).getString(ID);

            // Create the achievement
            Response achievementResponse = HttpUtils.post("/achievements", new JSONObject()
                    .put(NAME, "Clumsy")
                    .put(EVAL_TREE, new JSONObject()
                            .put(CONDITION, AND)
                            .put(CRITERIA, new JSONArray()
                                    .put(fallCritId))
                            .put(TYPE, GROUP)
                            .put(GROUPS, new JSONArray()))
                    .put(AWARDS, new JSONArray()
                            .put(bootsAwardId)
                            .put(expAwardId)));
            Preconditions.checkArgument(achievementResponse.code() == HttpUtils.STATUS_CREATED);
        }

        private JSONObject standardEvalBlock(String operator, int threshold) {
            return new JSONObject()
                    .put(TYPE, TYPE_STANDARD)
                    .put(OPERATOR, operator)
                    .put(THRESHOLD, threshold);
        }
    }
}
