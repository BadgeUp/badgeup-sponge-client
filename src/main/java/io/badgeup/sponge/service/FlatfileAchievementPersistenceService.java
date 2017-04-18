package io.badgeup.sponge.service;

import com.google.common.collect.Maps;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FlatfileAchievementPersistenceService implements AchievementPersistenceService {

    private File achievementFile;
    private JSONObject unpresentedAchievements;

    public FlatfileAchievementPersistenceService(Path configDir) {
        // Make sure the directory exists
        configDir.toFile().mkdirs();
        this.achievementFile = configDir.resolve("unpresented-achievements.json").toFile();

        if (this.achievementFile.exists()) {
            this.unpresentedAchievements = readFromFile();
            attemptMigration();
        } else {
            this.unpresentedAchievements = new JSONObject();
        }

    }

    @Override
    public CompletableFuture<Map<String, Integer>> getAllForPlayer(UUID playerID) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> playerAchievementsMap = Maps.newHashMap();

            if (this.unpresentedAchievements.has(playerID.toString())) {
                JSONObject playerAchievements = this.unpresentedAchievements.getJSONObject(playerID.toString());
                for (String achievementId : playerAchievements.keySet()) {
                    playerAchievementsMap.put(achievementId, playerAchievements.getInt(achievementId));
                }
            }

            return playerAchievementsMap;
        });

    }

    @Override
    public void increment(UUID playerID, String achievementId) {
        if (this.unpresentedAchievements.has(playerID.toString())) {
            JSONObject playerAchievements = this.unpresentedAchievements.getJSONObject(playerID.toString());
            if (playerAchievements.has(achievementId)) {
                playerAchievements.put(achievementId, playerAchievements.getInt(achievementId) + 1);
            } else {
                playerAchievements.put(achievementId, 1);
            }
        } else {
            JSONObject playerAchievements = new JSONObject();
            playerAchievements.put(achievementId, 1);
            this.unpresentedAchievements.put(playerID.toString(), playerAchievements);
        }

        saveToFile();
    }

    @Override
    public void decrement(UUID playerID, String achievementId) {
        if (!this.unpresentedAchievements.has(playerID.toString())) {
            throw new IllegalStateException("Player " + playerID + " has no unpresented achievements to decrement");
        }

        JSONObject playerAchievements = this.unpresentedAchievements.getJSONObject(playerID.toString());
        if (!playerAchievements.has(achievementId)) {
            throw new IllegalStateException("Player " + playerID + " does not have the achievement " + achievementId + " to decrement");
        } else {
            int newValue = playerAchievements.getInt(achievementId) - 1;
            if (newValue <= 0) {
                playerAchievements.remove(achievementId);
            } else {
                playerAchievements.put(achievementId, newValue);
            }
        }

        saveToFile();
    }

    @Override
    public void remove(String achievementId) {
        for (String playerId : this.unpresentedAchievements.keySet()) {
            this.unpresentedAchievements.getJSONObject(playerId).remove(achievementId);
        }
    }

    // Migrate from storing the whole achievement object to just the achievement
    // ID mapped to count
    private void attemptMigration() {
        for (String playerId : this.unpresentedAchievements.keySet()) {
            JSONArray achievementsArray = this.unpresentedAchievements.optJSONArray(playerId);
            if (achievementsArray == null) {
                continue;
            }

            JSONObject achievementsCount = new JSONObject();

            Iterator<Object> i = achievementsArray.iterator();
            while (i.hasNext()) {
                JSONObject achObj = (JSONObject) i.next();
                String achievementId = achObj.getString("id");

                if (achievementsCount.has(achievementId)) {
                    achievementsCount.put(achievementId, achievementsCount.getInt(achievementId) + 1);
                } else {
                    achievementsCount.put(achievementId, 1);
                }
            }

            this.unpresentedAchievements.put(playerId, achievementsCount);
        }

        saveToFile();
    }

    private JSONObject readFromFile() {
        try {
            Scanner scanner = new Scanner(this.achievementFile);
            String jsonTxt = scanner.useDelimiter("\\Z").next();
            scanner.close();
            return new JSONObject(jsonTxt);
        } catch (Exception e) {
            // This shouldn't ever happen
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private void saveToFile() {
        try {
            this.achievementFile.createNewFile(); // Only creates if not already
                                                  // exists
            FileWriter writer = new FileWriter(this.achievementFile);
            writer.write(this.unpresentedAchievements.toString());
            writer.close();
        } catch (IOException e) {
            // Shouldn't ever happen
            e.printStackTrace();
        }

    }

}
