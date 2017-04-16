package io.badgeup.sponge.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AwardPersistenceService {

    public CompletableFuture<List<String>> getPendingAwardsForPlayer(UUID playerID);

    public void addPendingAward(UUID playerID, String awardId);

    public void removePendingAwardByID(UUID playerID, String awardId);

}
