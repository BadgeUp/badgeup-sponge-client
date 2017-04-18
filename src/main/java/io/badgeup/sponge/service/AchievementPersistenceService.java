package io.badgeup.sponge.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AchievementPersistenceService {

    public CompletableFuture<Map<String, Integer>> getAllForPlayer(UUID playerID);

    public void increment(UUID playerID, String achievementId);

    public void decrement(UUID playerID, String achievementId);

    public void remove(String achievementId);

}
