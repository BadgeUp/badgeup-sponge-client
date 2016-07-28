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

	@Setting("rest-uri")
	private String RESTURI = "http://localhost:3000/v1/";
	
	public String getRESTURI() {
		return RESTURI;
	} 

}