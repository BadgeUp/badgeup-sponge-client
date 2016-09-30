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
		
		private static final String EVAL_TREE = "evalTree";
		private static final String CRITERIA = "criteria";
		private static final String GROUPS = "groups";
		private static final String CONDITION = "condition";
		private static final String AND = "AND";
		private static final String OR = "OR";
		private static final String TYPE = "type";
		private static final String GROUP = "GROUP";

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
											)
									))
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
											)
									))
					.asJson();
			Preconditions.checkArgument(achievementResponse.getStatus() == 201);

		}

	}

}
