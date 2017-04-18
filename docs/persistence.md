# Data Persistence

The BadgeUp Sponge client requires data about un-claimed awards and un-presented achievements to be persisted to some sort of storage. By default, the client uses its own flatfile storage. However, this is not ideal in many circumstances, such as in networks with multiple seamless Minecraft servers. We provide two interfaces you may implement to interface with your own backend: the `AchievementPersistenceService` and `AwardPersistenceService`.

## Gradle/Maven

You may access these interfaces via JitPack with either Gradle or Maven.

Gradle:

```groovy
repositories {
	maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.badgeup:sponge-client:v2.0.8' // Replace "v2.0.8" with the latest version
}
```

Maven:

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.BadgeUp</groupId>
    <artifactId>sponge-client</artifactId>
    <version>v2.0.8</version> <!-- Replace "v2.0.8" with the latest version -->
</dependency>
```

## Implementing Services

Each service supports three basic operations: getAllForPlayer, increment, decrement, and remove. All four methods are assumed to be running asynchronously, so you should not interact with strictly synchronous game operations.

```java
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Maps;
import org.json.JSONArray;
import org.json.JSONObject;

public class MyAchievementPersistenceService implements AchievementPersistenceService {

    @Override
    public CompletableFuture<Map<String, Integer>> getAllForPlayer(UUID playerID) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> playerAwardsMap = Maps.newHashMap();
            // Get the achievements
            return playerAchievements;
        });
    }

    @Override
    public void increment(UUID playerID, String achievementId) {
        // Increment the count for the achievement in storage
    }

    @Override
    public void decrement(UUID playerID, String achievementId) {
        // Decrement the count for the achievement in storage
    }

    @Override
    public void remove(String achievementId) {
        // Completely remove this achievement from storage
    }
}
```

## Registering Services

After implementing each service, you must register them with the Sponge service manager during **pre-initialization**:

```java
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;

import io.badgeup.sponge.service.AchievementPersistenceService;
import io.badgeup.sponge.service.AwardPersistenceService;

@Listener
public void preInit(GamePreInitializationEvent event) {
	Sponge.getServiceManager().setProvider(this, AchievementPersistenceService.class,
			new MyAchievementPersistenceService());
	Sponge.getServiceManager().setProvider(this, AwardPersistenceService.class,
			new MyAwardPersistenceService());
}
```
