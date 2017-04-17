package io.badgeup.sponge;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class ResourceCache {

    private Logger logger;
    private AsyncLoadingCache<String, JSONObject> awardsCache;
    private AsyncLoadingCache<String, JSONObject> achievementsCache;

    protected ResourceCache(Logger logger) {
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

    public CompletableFuture<JSONObject> getAwardById(String awardId) {
        return this.awardsCache.get(awardId);
    }

    public CompletableFuture<JSONObject> getAchievementById(String awardId) {
        return this.achievementsCache.get(awardId);
    }

    private CompletableFuture<JSONObject> getAwardById(String key, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<JsonNode> response = HttpUtils.get("/awards/" + key).asJson();
                if (response.getStatus() != HttpStatus.SC_OK) {
                    this.logger.error("Got " + response.getStatus() + " response getting the award \"" + key + "\"");
                    return new JSONObject();
                }

                return response.getBody().getObject();
            } catch (UnirestException e) {
                e.printStackTrace();
                return new JSONObject();
            }
        }, executor);
    }

    private CompletableFuture<JSONObject> getAchievementById(String key, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<JsonNode> response = HttpUtils.get("/achievements/" + key).asJson();
                if (response.getStatus() != HttpStatus.SC_OK) {
                    this.logger.error("Got " + response.getStatus() + " response getting the achievement \"" + key + "\"");
                    return new JSONObject();
                }

                return response.getBody().getObject();
            } catch (UnirestException e) {
                e.printStackTrace();
                return new JSONObject();
            }
        }, executor);
    }

}
