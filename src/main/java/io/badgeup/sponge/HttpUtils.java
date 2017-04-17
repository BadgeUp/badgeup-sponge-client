package io.badgeup.sponge;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

    private static String appId = Util.parseAppIdFromAPIKey(BadgeUpSponge.getConfig().getBadgeUpConfig().getAPIKey()).get();
    private static String baseUrl = getApiBaseUrl();
    private static Map<String, String> headers = getHeaders();

    public static HttpRequestWithBody post(String url) {
        return Unirest.post(baseUrl + "/v1/apps/" + appId + url).headers(headers);
    }

    public static GetRequest getRaw(String url) {
        return Unirest.get(baseUrl + url).headers(headers);
    }

    public static GetRequest get(String url) {
        return Unirest.get(baseUrl + "/v1/apps/" + appId + url).headers(headers);
    }

    private static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();

        String apiKey = BadgeUpSponge.getConfig().getBadgeUpConfig().getAPIKey();
        final String authHeader = "Basic " + new String(Base64.getEncoder().encode((apiKey + ":").getBytes()));

        headers.put("User-Agent", "BadgeUp_SpongeClient v" + BadgeUpSponge.getContainer().getVersion().orElse("Unknown"));
        headers.put("Authorization", authHeader);

        return headers;
    }

    private static String getApiBaseUrl() {
        Config config = BadgeUpSponge.getConfig();

        // build the base API URL
        String baseURL = "";

        if (!config.getBadgeUpConfig().getBaseAPIURL().isEmpty()) {
            // override other config settings with this base URL
            baseURL = config.getBadgeUpConfig().getBaseAPIURL();
        } else {
            // region provided
            baseURL = "https://api." + config.getBadgeUpConfig().getRegion() + ".badgeup.io";
        }

        return baseURL;
    }

}
