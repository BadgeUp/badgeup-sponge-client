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
	
	@Setting("region")
	private String apiRegion = "useast1";
	
	public String getRegion() {
		return apiRegion;
	}
	
	// setting intended for development purposes
	// overrides the api-host config setting
	@Setting("base-api-url")
	private String baseURL = "";
	
	public String getBaseAPIURL() {
		return baseURL;
	}
	
	@Setting("broadcast-achievements")
	private boolean broadcastAchievements = true;
	
	public boolean doBroadcastAchievements() {
		return broadcastAchievements;
	}
	

}