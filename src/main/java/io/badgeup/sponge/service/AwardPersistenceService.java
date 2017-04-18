package io.badgeup.sponge.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AwardPersistenceService {

    public CompletableFuture<Map<String, Integer>> getAllForPlayer(UUID playerID);

    public void increment(UUID playerID, String awardId);

    public void decrement(UUID playerID, String awardId);

    public void remove(String awardId);

}
