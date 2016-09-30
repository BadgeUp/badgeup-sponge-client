package io.badgeup.sponge.command.executor;

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

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.Util;

public class BadgeUpInitCommandExecutor implements CommandExecutor {

	private BadgeUpSponge plugin;

	public BadgeUpInitCommandExecutor(BadgeUpSponge plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Sponge.getScheduler().createTaskBuilder().async().execute(new BadgeUpInitRunnable(src)).submit(plugin);
		return CommandResult.success();
	}

	class BadgeUpInitRunnable implements Runnable {

		private static final String ID = "id";
		private static final String NAME = "name";
		private static final String DESC = "description";
		private static final String KEY = "key";
		private static final String OPERATOR = "operator";
		private static final String THRESHOLD = "threshold";
		
		private static final String AWARDS = "awards";
		private static final String EVAL_TREE = "evalTree";
		private static final String CRITERIA = "criteria";
		private static final String GROUPS = "groups";
		private static final String CONDITION = "condition";
		private static final String AND = "AND";
		private static final String OR = "OR";
		private static final String TYPE = "type";
		private static final String GROUP = "GROUP";
		
		private static final String DATA = "data";

		private CommandSource src;

		public BadgeUpInitRunnable(CommandSource src) {
			this.src = src;
		}

		@Override
		public void run() {
			final Text contactSupportMsg = Text.of("Please contact BadgeUp Support at support@badgeup.io with the following error log:");
			
			try {
				meatLoverAchievement();
			} catch (JSONException | UnirestException e) {
				src.sendMessage(Text.of(TextColors.RED, "Failed to create Meat Lover achievement."));
				src.sendMessage(contactSupportMsg);
				e.printStackTrace();
			}
			src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Meat Lover achievement."));
			
			try {
				vegematicAchievement();
			} catch (JSONException | UnirestException e) {
				src.sendMessage(Text.of(TextColors.RED, "Failed to create Veggematic achievement."));
				src.sendMessage(contactSupportMsg);
				e.printStackTrace();
			}
			src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Veggematic achievement."));
			
			try {
				greenThumbAchievement();
			} catch (JSONException | UnirestException e) {
				src.sendMessage(Text.of(TextColors.RED, "Failed to create Green Thumb achievement."));
				src.sendMessage(contactSupportMsg);
				e.printStackTrace();
			}
			src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Green Thumb achievement."));
			
			try {
				pyroAchievement();
			} catch (JSONException | UnirestException e) {
				src.sendMessage(Text.of(TextColors.RED, "Failed to create Pyro achievement."));
				src.sendMessage(contactSupportMsg);
				e.printStackTrace();
			}
			src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Pyro achievement."));
			
			try {
				lumberjackAchievement();
			} catch (JSONException | UnirestException e) {
				src.sendMessage(Text.of(TextColors.RED, "Failed to create Lumberjack achievement."));
				src.sendMessage(contactSupportMsg);
				e.printStackTrace();
			}
			src.sendMessage(Text.of(TextColors.GREEN, "Successfully created Lumberjack achievement."));
		}

		private void meatLoverAchievement() throws JSONException, UnirestException, IllegalStateException {
			final String baseURL = BadgeUpSponge.getConfig().getBadgeUpConfig().getBaseAPIURL();
			final String appId = Util.parseAppIdFromAPIKey(BadgeUpSponge.getConfig().getBadgeUpConfig().getAPIKey()).get();

			HttpResponse<JsonNode> rawPorkCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Raw Porkchop")
							.put(DESC, "Eat 1 Raw Porkchop")
							.put(KEY, "useitemstack:finish:minecraft:porkchop")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(rawPorkCritResponse.getStatus() == 201);
			final String rawPorkCritId = rawPorkCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> cookedPorkCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Cooked Porkchop")
							.put(DESC, "Eat 1 Cooked Porkchop")
							.put(KEY, "useitemstack:finish:minecraft:cooked_porkchop")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(cookedPorkCritResponse.getStatus() == 201);
			final String cookedPorkCritId = cookedPorkCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> rawChickenCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Raw Chicken")
							.put(DESC, "Eat 1 Raw Chicken")
							.put(KEY, "useitemstack:finish:minecraft:chicken")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(rawChickenCritResponse.getStatus() == 201);
			final String rawChickenCritId = rawChickenCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> cookedChickenCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Cooked Chicken")
							.put(DESC, "Eat 1 Cooked Chicken")
							.put(KEY, "useitemstack:finish:minecraft:cooked_chicken")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(cookedChickenCritResponse.getStatus() == 201);
			final String cookedChickenCritId = cookedChickenCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> rawMuttonCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Raw Mutton")
							.put(DESC, "Eat 1 Raw Mutton")
							.put(KEY, "useitemstack:finish:minecraft:mutton")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(rawMuttonCritResponse.getStatus() == 201);
			final String rawMuttonCritId = rawMuttonCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> cookedMuttonCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Cooked Mutton")
							.put(DESC, "Eat 1 Cooked Mutton")
							.put(KEY, "useitemstack:finish:minecraft:cooked_mutton")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(cookedMuttonCritResponse.getStatus() == 201);
			final String cookedMuttonCritId = cookedMuttonCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> rawBeefCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Raw Beef")
							.put(DESC, "Eat 1 Raw Beef")
							.put(KEY, "useitemstack:finish:minecraft:beef")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(rawBeefCritResponse.getStatus() == 201);
			final String rawBeefCritId = rawBeefCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> cookedBeefCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Cooked Beef")
							.put(DESC, "Eat 1 Cooked Beef")
							.put(KEY, "useitemstack:finish:minecraft:cooked_beef")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(cookedBeefCritResponse.getStatus() == 201);
			final String cookedBeefCritId = cookedBeefCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> rawRabbitCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Raw Rabbit")
							.put(DESC, "Eat 1 Raw Rabbit")
							.put(KEY, "useitemstack:finish:minecraft:rabbit")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(rawRabbitCritResponse.getStatus() == 201);
			final String rawRabbitCritId = rawRabbitCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> cookedRabbitCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Cooked Rabbit")
							.put(DESC, "Eat 1 Cooked Rabbit")
							.put(KEY, "useitemstack:finish:minecraft:cooked_rabbit")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(cookedRabbitCritResponse.getStatus() == 201);
			final String cookedRabbitCritId = cookedRabbitCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> steakAwardResponse = Unirest.post(baseURL + appId + "/awards")
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
			HttpResponse<JsonNode> achievementResponse = Unirest.post(baseURL + appId + "/achievements")
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
														.put(new JSONObject().put(ID, rawPorkCritId))
														.put(new JSONObject().put(ID, cookedPorkCritId)))
												.put(GROUPS, new JSONArray())
												.put(TYPE, GROUP))
											.put(new JSONObject()
													.put(CONDITION, OR)
													.put(CRITERIA, new JSONArray()
															.put(new JSONObject().put(ID, rawChickenCritId))
															.put(new JSONObject().put(ID, cookedChickenCritId)))
													.put(GROUPS, new JSONArray())
													.put(TYPE, GROUP))
											.put(new JSONObject()
													.put(CONDITION, OR)
													.put(CRITERIA, new JSONArray()
															.put(new JSONObject().put(ID, rawMuttonCritId))
															.put(new JSONObject().put(ID, cookedMuttonCritId)))
													.put(GROUPS, new JSONArray())
													.put(TYPE, GROUP))
											.put(new JSONObject()
													.put(CONDITION, OR)
													.put(CRITERIA, new JSONArray()
															.put(new JSONObject().put(ID, rawBeefCritId))
															.put(new JSONObject().put(ID, cookedBeefCritId)))
													.put(GROUPS, new JSONArray())
													.put(TYPE, GROUP))
											.put(new JSONObject()
													.put(CONDITION, OR)
													.put(CRITERIA, new JSONArray()
															.put(new JSONObject().put(ID, rawRabbitCritId))
															.put(new JSONObject().put(ID, cookedRabbitCritId)))
													.put(GROUPS, new JSONArray())
													.put(TYPE, GROUP))
											))
								.put(AWARDS, new JSONArray()
										.put(new JSONObject().put(ID, steakAwardId)))
							)
					.asJson();
			Preconditions.checkArgument(achievementResponse.getStatus() == 201);

		}
		
		private void vegematicAchievement() throws JSONException, UnirestException, IllegalStateException {
			final String baseURL = BadgeUpSponge.getConfig().getBadgeUpConfig().getBaseAPIURL();
			final String appId = Util.parseAppIdFromAPIKey(BadgeUpSponge.getConfig().getBadgeUpConfig().getAPIKey()).get();

			HttpResponse<JsonNode> appleCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Apple")
							.put(DESC, "Eat 1 Apple")
							.put(KEY, "useitemstack:finish:minecraft:apple")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(appleCritResponse.getStatus() == 201);
			final String appleCritId = appleCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> goldenAppleCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Golden Apple")
							.put(DESC, "Eat 1 Golden Apple")
							.put(KEY, "useitemstack:finish:minecraft:golden_apple")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(goldenAppleCritResponse.getStatus() == 201);
			final String goldenAppleCritId = goldenAppleCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> mushroomStewCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Mushroom Stew")
							.put(DESC, "Eat 1 Mushroom Stew")
							.put(KEY, "useitemstack:finish:minecraft:mushroom_stew")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(mushroomStewCritResponse.getStatus() == 201);
			final String mushroomStewCritId = mushroomStewCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> melonCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Melon")
							.put(DESC, "Eat 1 Melon")
							.put(KEY, "useitemstack:finish:minecraft:melon")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(melonCritResponse.getStatus() == 201);
			final String melonCritId = melonCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> carrotCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Carrot")
							.put(DESC, "Eat 1 Carrot")
							.put(KEY, "useitemstack:finish:minecraft:carrot")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(carrotCritResponse.getStatus() == 201);
			final String carrotCritId = carrotCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> goldenCarrotCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Golden Carrot")
							.put(DESC, "Eat 1 Golden Carrot")
							.put(KEY, "useitemstack:finish:minecraft:golden_carrot")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(goldenCarrotCritResponse.getStatus() == 201);
			final String goldenCarrotCritId = goldenCarrotCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> rawPotatoCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Raw Potato")
							.put(DESC, "Eat 1 Raw Potato")
							.put(KEY, "useitemstack:finish:minecraft:potato")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(rawPotatoCritResponse.getStatus() == 201);
			final String rawPotatoCritId = rawPotatoCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> bakedPotatoCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Baked Potato")
							.put(DESC, "Eat 1 Baked Mutton")
							.put(KEY, "useitemstack:finish:minecraft:baked_potato")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(bakedPotatoCritResponse.getStatus() == 201);
			final String bakedPotatoCritId = bakedPotatoCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> poisonousPotatoCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Poisonous Potato")
							.put(DESC, "Eat 1 Poisonous Potato")
							.put(KEY, "useitemstack:finish:minecraft:poisonous_potato")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(poisonousPotatoCritResponse.getStatus() == 201);
			final String poisonousPotatoCritId = poisonousPotatoCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> beetrootCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Beetroot")
							.put(DESC, "Eat 1 Beetroot")
							.put(KEY, "useitemstack:finish:minecraft:beetroot")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(beetrootCritResponse.getStatus() == 201);
			final String beetrootCritId = beetrootCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> beetrootSoupCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Beetroot Soup")
							.put(DESC, "Eat 1 Beetroot Soup")
							.put(KEY, "useitemstack:finish:minecraft:beetroot_soup")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(beetrootSoupCritResponse.getStatus() == 201);
			final String beetrootSoupCritId = beetrootSoupCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> goldenAppleAwardResponse = Unirest.post(baseURL + appId + "/awards")
					.body(new JSONObject()
							.put(NAME, "Golden Apple")
							.put(DATA, new JSONObject()
									.put(TYPE, "item")
									.put("itemType", "minecraft:golden_apple")
									.put("quantity", 1)))
					.asJson();
			Preconditions.checkArgument(goldenAppleAwardResponse.getStatus() == 201);
			final String goldenAppleAwardId = goldenAppleAwardResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> goldenCarrotAwardResponse = Unirest.post(baseURL + appId + "/awards")
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
			HttpResponse<JsonNode> achievementResponse = Unirest.post(baseURL + appId + "/achievements")
					.body(new JSONObject()
							.put(NAME, "Veggematic")
							.put(DESC, "Is that a leaf growing from your head?")
							.put(EVAL_TREE, new JSONObject()
									.put(CONDITION, AND)
									.put(CRITERIA, new JSONArray()
											.put(new JSONObject().put(ID, mushroomStewCritId))
											.put(new JSONObject().put(ID, melonCritId)))
									.put(TYPE, GROUP)
									.put(GROUPS, new JSONArray()
											.put(new JSONObject()
													.put(CONDITION, OR)
													.put(CRITERIA, new JSONArray()
															.put(new JSONObject().put(ID, appleCritId))
															.put(new JSONObject().put(ID, goldenAppleCritId)))
													.put(GROUPS, new JSONArray())
													.put(TYPE, GROUP))
											.put(new JSONObject()
													.put(CONDITION, OR)
													.put(CRITERIA, new JSONArray()
															.put(new JSONObject().put(ID, carrotCritId))
															.put(new JSONObject().put(ID, goldenCarrotCritId)))
													.put(GROUPS, new JSONArray())
													.put(TYPE, GROUP))
											.put(new JSONObject()
													.put(CONDITION, OR)
													.put(CRITERIA, new JSONArray()
															.put(new JSONObject().put(ID, rawPotatoCritId))
															.put(new JSONObject().put(ID, bakedPotatoCritId))
															.put(new JSONObject().put(ID, poisonousPotatoCritId)))
													.put(GROUPS, new JSONArray())
													.put(TYPE, GROUP))
											.put(new JSONObject()
													.put(CONDITION, OR)
													.put(CRITERIA, new JSONArray()
															.put(new JSONObject().put(ID, beetrootCritId))
															.put(new JSONObject().put(ID, beetrootSoupCritId)))
													.put(GROUPS, new JSONArray())
													.put(TYPE, GROUP))
											))
								.put(AWARDS, new JSONArray()
										.put(new JSONObject().put(ID, goldenAppleAwardId))
										.put(new JSONObject().put(ID, goldenCarrotAwardId)))
							)
					.asJson();
			Preconditions.checkArgument(achievementResponse.getStatus() == 201);

		}
		
		private void greenThumbAchievement() throws JSONException, UnirestException, IllegalStateException {
			final String baseURL = BadgeUpSponge.getConfig().getBadgeUpConfig().getBaseAPIURL();
			final String appId = Util.parseAppIdFromAPIKey(BadgeUpSponge.getConfig().getBadgeUpConfig().getAPIKey()).get();

			HttpResponse<JsonNode> placeSaplingCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Place Saplings")
							.put(DESC, "Place 10 Saplings")
							.put(KEY, "changeblock:place:minecraft:sapling")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 10))
					.asJson();
			Preconditions.checkArgument(placeSaplingCritResponse.getStatus() == 201);
			final String placeSaplingCritId = placeSaplingCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> rainbowSheepAwardResponse = Unirest.post(baseURL + appId + "/awards")
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
			
			// Create the achievement
			HttpResponse<JsonNode> achievementResponse = Unirest.post(baseURL + appId + "/achievements")
					.body(new JSONObject()
							.put(NAME, "Green Thumb")
							.put(DESC, "That deep-rooted desire")
							.put(EVAL_TREE, new JSONObject()
									.put(CONDITION, AND)
									.put(CRITERIA, new JSONArray()
											.put(new JSONObject().put(ID, placeSaplingCritId)))
									.put(TYPE, GROUP)
									.put(GROUPS, new JSONArray()))
							.put(AWARDS, new JSONArray()
									.put(new JSONObject().put(ID, rainbowSheepAwardId)))
							)
					.asJson();
			Preconditions.checkArgument(achievementResponse.getStatus() == 201);

		}
		
		private void pyroAchievement() throws JSONException, UnirestException, IllegalStateException {
			final String baseURL = BadgeUpSponge.getConfig().getBadgeUpConfig().getBaseAPIURL();
			final String appId = Util.parseAppIdFromAPIKey(BadgeUpSponge.getConfig().getBadgeUpConfig().getAPIKey()).get();

			HttpResponse<JsonNode> lightFireCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Light Fires")
							.put(DESC, "Light a fire")
							.put(KEY, "changeblock:place:minecraft:fire")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 1))
					.asJson();
			Preconditions.checkArgument(lightFireCritResponse.getStatus() == 201);
			final String lightFireCritId = lightFireCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> tntAwardResponse = Unirest.post(baseURL + appId + "/awards")
					.body(new JSONObject()
							.put(NAME, "TNT")
							.put(DATA, new JSONObject()
									.put(TYPE, "item")
									.put("itemType", "minecraft:tnt")
									.put("quantity", 10)))
					.asJson();
			Preconditions.checkArgument(tntAwardResponse.getStatus() == 201);
			final String tntAwardId = tntAwardResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> resistanceEffectAwardResponse = Unirest.post(baseURL + appId + "/awards")
					.body(new JSONObject()
							.put(NAME, "Resistance Potion Effect")
							.put(DATA, new JSONObject()
									.put(TYPE, "potion")
									.put("potionEffectType", "minecraft:resistance")
									.put("duration", 2400) // 2 minutes
									.put("amplifier", 4)
								))
					.asJson();
			Preconditions.checkArgument(resistanceEffectAwardResponse.getStatus() == 201);
			final String resistanceEffectAwardId = resistanceEffectAwardResponse.getBody().getObject().getString(ID);
			
			// Create the achievement
			HttpResponse<JsonNode> achievementResponse = Unirest.post(baseURL + appId + "/achievements")
					.body(new JSONObject()
							.put(NAME, "Pyro")
							.put(DESC, "Some people just want to watch the world burn")
							.put(EVAL_TREE, new JSONObject()
									.put(CONDITION, AND)
									.put(CRITERIA, new JSONArray()
											.put(new JSONObject().put(ID, lightFireCritId)))
									.put(TYPE, GROUP)
									.put(GROUPS, new JSONArray()))
							.put(AWARDS, new JSONArray()
									.put(new JSONObject().put(ID, tntAwardId))
									.put(new JSONObject().put(ID, resistanceEffectAwardId)))
							)
					.asJson();
			Preconditions.checkArgument(achievementResponse.getStatus() == 201);

		}
		
		private void lumberjackAchievement() throws JSONException, UnirestException, IllegalStateException {
			final String baseURL = BadgeUpSponge.getConfig().getBadgeUpConfig().getBaseAPIURL();
			final String appId = Util.parseAppIdFromAPIKey(BadgeUpSponge.getConfig().getBadgeUpConfig().getAPIKey()).get();

			HttpResponse<JsonNode> chopLogsCritResponse = Unirest.post(baseURL + appId + "/criteria")
					.body(new JSONObject()
							.put(NAME, "Chop Trees")
							.put(DESC, "Chop 50 Logs")
							.put(KEY, "changeblock:break:minecraft:log")
							.put(OPERATOR, "@gte")
							.put(THRESHOLD, 50))
					.asJson();
			Preconditions.checkArgument(chopLogsCritResponse.getStatus() == 201);
			final String chopLogsCritId = chopLogsCritResponse.getBody().getObject().getString(ID);
			
			HttpResponse<JsonNode> axeAwardResponse = Unirest.post(baseURL + appId + "/awards")
					.body(new JSONObject()
							.put(NAME, "Chainsaw")
							.put(DATA, new JSONObject()
									.put(TYPE, "item")
									.put("itemType", "minecraft:diamond_axe")
									.put("displayName", "&6Chainsaw")
									.put("enchantments", new JSONArray()
											.put(new JSONObject()
													.put(ID, "minecraft:efficiency")
													.put("level", 5)
											).put(new JSONObject()
													.put(ID, "minecraft:unbreaking")
													.put("level", 3)
													)
											)
								))
					.asJson();
			Preconditions.checkArgument(axeAwardResponse.getStatus() == 201);
			final String axeAwardId = axeAwardResponse.getBody().getObject().getString(ID);
			
			// Create the achievement
			HttpResponse<JsonNode> achievementResponse = Unirest.post(baseURL + appId + "/achievements")
					.body(new JSONObject()
							.put(NAME, "Lumberjack")
							.put(DESC, "Haven't you heard of email?")
							.put(EVAL_TREE, new JSONObject()
									.put(CONDITION, AND)
									.put(CRITERIA, new JSONArray()
											.put(new JSONObject().put(ID, chopLogsCritId)))
									.put(TYPE, GROUP)
									.put(GROUPS, new JSONArray()))
							.put(AWARDS, new JSONArray()
									.put(new JSONObject().put(ID, axeAwardId)))
							)
					.asJson();
			Preconditions.checkArgument(achievementResponse.getStatus() == 201);

		}

	}

}
