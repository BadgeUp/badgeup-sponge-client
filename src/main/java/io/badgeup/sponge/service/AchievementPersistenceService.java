package io.badgeup.sponge.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;

public interface AchievementPersistenceService {
	
	public CompletableFuture<List<JSONObject>> getUnpresentedAchievementsForPlayer(UUID playerID);
	
	public void addUnpresentedAchievement(UUID playerID, JSONObject achievement);
	
	public void removeAchievementByID(UUID playerID, String achievementID);

}
