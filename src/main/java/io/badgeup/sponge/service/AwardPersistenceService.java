package io.badgeup.sponge.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;

public interface AwardPersistenceService {

	public CompletableFuture<List<JSONObject>> getPendingAwardsForPlayer(UUID playerID);
	
	public void addPendingAward(UUID playerID, JSONObject award);
	
	public void removePendingAwardByID(UUID playerID, String rewardID);
	
}
