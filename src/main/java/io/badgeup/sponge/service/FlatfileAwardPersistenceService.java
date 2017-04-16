package io.badgeup.sponge.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FlatfileAwardPersistenceService implements AwardPersistenceService {

    private File awardsFile;
    private JSONObject pendingAwards;

    public FlatfileAwardPersistenceService(Path configDir) {
        // Make sure the directory exists
        configDir.toFile().mkdirs();
        this.awardsFile = configDir.resolve("pending-awards.json").toFile();

        if (this.awardsFile.exists()) {
            this.pendingAwards = readFromFile();
            attemptMigration();
        } else {
            this.pendingAwards = new JSONObject();
        }

    }

    @Override
    public CompletableFuture<List<String>> getPendingAwardsForPlayer(UUID playerID) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> playerAwardIds = new ArrayList<>();

            if (this.pendingAwards.has(playerID.toString())) {
                JSONObject playerAwards = this.pendingAwards.getJSONObject(playerID.toString());
                for (String awardId : playerAwards.keySet()) {
                    for (int i = 0; i < playerAwards.getInt(awardId); i++) {
                        playerAwardIds.add(awardId);
                    }
                }
            }

            return playerAwardIds;
        });

    }

    @Override
    public void addPendingAward(UUID playerID, String awardId) {
        if (this.pendingAwards.has(playerID.toString())) {
            JSONObject playerAwards = this.pendingAwards.getJSONObject(playerID.toString());
            if (playerAwards.has(awardId)) {
                playerAwards.put(awardId, playerAwards.getInt(awardId) + 1);
            } else {
                playerAwards.put(awardId, 1);
            }
        } else {
            JSONObject playerAwards = new JSONObject();
            playerAwards.put(awardId, 1);
            this.pendingAwards.put(playerID.toString(), playerAwards);
        }

        saveToFile();
    }

    @Override
    public void removePendingAwardByID(UUID playerID, String awardId) {
        if (!this.pendingAwards.has(playerID.toString())) {
            throw new IllegalStateException("Player " + playerID + " has no pending awards to remove");
        }

        JSONObject playerAwards = this.pendingAwards.getJSONObject(playerID.toString());
        if (!playerAwards.has(awardId)) {
            throw new IllegalStateException("Player " + playerID + " does not have the award " + awardId + " to remove");
        } else {
            int newValue = playerAwards.getInt(awardId) - 1;
            if (newValue <= 0) {
                playerAwards.remove(awardId);
            } else {
                playerAwards.put(awardId, newValue);
            }
        }

        saveToFile();
    }
    
    // Migrate from storing the whole award object to just the award ID mapped to count
    private void attemptMigration() {
        for (String playerId : this.pendingAwards.keySet()) {
            JSONArray awardsArray = this.pendingAwards.optJSONArray(playerId); 
            if (awardsArray == null) {
                continue;
            }
            
            JSONObject awardsCount = new JSONObject();
            
            Iterator<Object> i = awardsArray.iterator();
            while(i.hasNext()) {
                JSONObject awardObj = (JSONObject) i.next();
                String awardId = awardObj.getString("id");
                
                if (awardsCount.has(awardId)) {
                    awardsCount.put(awardId, awardsCount.getInt(awardId) + 1);
                } else {
                    awardsCount.put(awardId, 1);
                }
            }
            
            this.pendingAwards.put(playerId, awardsCount);
        }
        
        saveToFile();
    }

    private JSONObject readFromFile() {
        try {
            Scanner scanner = new Scanner(this.awardsFile);
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
            this.awardsFile.createNewFile(); // Only creates if not already
                                             // exists
            FileWriter writer = new FileWriter(this.awardsFile);
            writer.write(this.pendingAwards.toString());
            writer.close();
        } catch (IOException e) {
            // Shouldn't ever happen
            e.printStackTrace();
        }

    }

}
