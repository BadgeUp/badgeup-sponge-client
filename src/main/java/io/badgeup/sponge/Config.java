package io.badgeup.sponge;

import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;

public class Config {

	public static final ObjectMapper<Config> MAPPER;

	static {
		try {
			MAPPER = ObjectMapper.forClass(Config.class);
		} catch (ObjectMappingException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	@Setting("api-key")
	private String apiKey = "";
	
	public String getAPIKey() {
		return apiKey;
	}
	
	@Setting("broadcast-achievements")
	private boolean broadcastAchievements = true;
	
	public boolean doBroadcastAchievements() {
		return broadcastAchievements;
	}
	

}