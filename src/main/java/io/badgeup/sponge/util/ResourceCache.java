package io.badgeup.sponge.util;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.badgeup.sponge.service.AchievementPersistenceService;
import io.badgeup.sponge.service.AwardPersistenceService;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class ResourceCache {

    private Logger logger;
    private AsyncLoadingCache<String, Optional<JSONObject>> awardsCache;
    private AsyncLoadingCache<String, Optional<JSONObject>> achievementsCache;

    public ResourceCache(Logger logger) {
        this.logger = logger;

        this.awardsCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .buildAsync((key, executor) -> getAwardById(key, executor));

        this.achievementsCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .buildAsync((key, executor) -> getAchievementById(key, executor));
    }

    public CompletableFuture<Optional<JSONObject>> getAwardById(String awardId) {
        return this.awardsCache.get(awardId);
    }

    public CompletableFuture<Optional<JSONObject>> getAchievementById(String awardId) {
        return this.achievementsCache.get(awardId);
    }

    private CompletableFuture<Optional<JSONObject>> getAwardById(String key, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<JsonNode> response = HttpUtils.get("/awards/" + key).asJson();
                if (response.getStatus() != HttpStatus.SC_OK) {
                    this.logger.error("Got " + response.getStatus() + " response getting the award \"" + key + "\"");

                    if (response.getStatus() == HttpStatus.SC_NOT_FOUND) {
                        this.logger.info("Got 404 for award \"" + key + "\". Removing from storage");
                        AwardPersistenceService aps = Sponge.getServiceManager().provideUnchecked(AwardPersistenceService.class);
                        aps.remove(key);
                    }
                    return Optional.empty();
                }

                return Optional.of(response.getBody().getObject());
            } catch (UnirestException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }, executor);
    }

    private CompletableFuture<Optional<JSONObject>> getAchievementById(String key, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<JsonNode> response = HttpUtils.get("/achievements/" + key).asJson();
                if (response.getStatus() != HttpStatus.SC_OK) {
                    this.logger.error("Got " + response.getStatus() + " response getting the achievement \"" + key + "\"");

                    if (response.getStatus() == HttpStatus.SC_NOT_FOUND) {
                        this.logger.info("Got 404 for award \"" + key + "\". Removing from storage");
                        AchievementPersistenceService aps = Sponge.getServiceManager().provideUnchecked(AchievementPersistenceService.class);
                        aps.remove(key);
                    }
                    return Optional.empty();
                }

                return Optional.of(response.getBody().getObject());
            } catch (UnirestException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }, executor);
    }

}