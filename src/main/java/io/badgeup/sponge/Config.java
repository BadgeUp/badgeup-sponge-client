package io.badgeup.sponge;

import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

public class Config {

    public static final ObjectMapper<Config> MAPPER;

    static {
        try {
            MAPPER = ObjectMapper.forClass(Config.class);
        } catch (ObjectMappingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @ConfigSerializable
    public static class BadgeUpConfig {

        @Setting("api-key") private String apiKey = "";

        public String getAPIKey() {
            return this.apiKey;
        }

        @Setting("region") private String apiRegion = "useast1";

        public String getRegion() {
            return this.apiRegion;
        }

        // setting intended for development purposes
        // overrides the api-host config setting
        @Setting("base-api-url") private String baseURL = "";

        public String getBaseAPIURL() {
            return this.baseURL;
        }

        // 3 connections by default to try to spread connections out over BUp
        // servers
        @Setting("event-pool-connections") private int eventPoolConnections = 3;

        public int getEventPoolConnections() {
            return this.eventPoolConnections;
        }
        
        @Setting("collect-event-data") private boolean collectEventData = false;
        
        public boolean collectEventData() {
            return this.collectEventData;
        }
        
    }

    @Setting("badgeup") private BadgeUpConfig bUpConfig = new BadgeUpConfig();

    public BadgeUpConfig getBadgeUpConfig() {
        return this.bUpConfig;
    }

    @Setting("broadcast-achievements") private boolean broadcastAchievements = true;

    public boolean doBroadcastAchievements() {
        return this.broadcastAchievements;
    }

    @Setting("fireworks") private boolean fireworks = true;

    public boolean doFireworks() {
        return this.fireworks;
    }

    @ConfigSerializable
    public static class SoundEffectConfig {

        @Setting("enabled") private boolean enabled = false;

        public boolean isEnabled() {
            return this.enabled;
        }

        @Setting(value = "sound", comment = "See http://minecraft.gamepedia.com/Sounds.json for all sounds") private String sound =
                "minecraft:entity.experience_orb.pickup";

        public String getSound() {
            return this.sound;
        }
    }

    @Setting("sounds") private SoundEffectConfig soundsConfig = new SoundEffectConfig();

    public SoundEffectConfig getSoundsConfig() {
        return this.soundsConfig;
    }

}
