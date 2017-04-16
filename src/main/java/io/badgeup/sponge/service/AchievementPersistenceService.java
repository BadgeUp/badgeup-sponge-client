package io.badgeup.sponge.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AchievementPersistenceService {

    public CompletableFuture<List<String>> getUnpresentedAchievementsForPlayer(UUID playerID);

    public void addUnpresentedAchievement(UUID playerID, String achievementId);

    public void removeAchievementByID(UUID playerID, String achievementID);

}
