package io.badgeup.sponge.util;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.Config;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;

public class HttpUtils {

    private static OkHttpClient httpClient = new OkHttpClient();
    private static String appId = Util.parseAppIdFromAPIKey(BadgeUpSponge.getConfig().getBadgeUpConfig().getAPIKey()).get();
    private static String baseUrl = getApiBaseUrl();
    private static Headers headers = getHeaders();

    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_NOT_FOUND = 404;

    public static Response post(String url, JSONObject body) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse(body.toString()), body.toString());
        Request request = new Request.Builder().url(baseUrl + "/v1/apps/" + appId + url).headers(headers).post(requestBody).build();

        return httpClient.newCall(request).execute();
    }

    public static Response getRaw(String url) throws IOException {
        Request request = new Request.Builder().url(baseUrl + url).headers(headers).get().build();
        return httpClient.newCall(request).execute();
    }

    public static Response get(String url) throws IOException {
        Request request = new Request.Builder().url(baseUrl + "/v1/apps/" + appId + url).headers(headers).get().build();
        return httpClient.newCall(request).execute();
    }

    public static Request getRawRequest(String url) throws IOException {
        return new Request.Builder().url(baseUrl + url).headers(headers).get().build();
    }

    public static Request getRequest(String url) throws IOException {
        return new Request.Builder().url(baseUrl + "/v1/apps/" + appId + url).headers(headers).get().build();
    }

    public static JSONObject parseBody(Response response) throws JSONException, IOException {
        return new JSONObject(response.body().string());
    }

    public static OkHttpClient getHttpClient() {
        return httpClient;
    }

    private static Headers getHeaders() {

        String apiKey = BadgeUpSponge.getConfig().getBadgeUpConfig().getAPIKey();
        final String authHeader = "Basic " + new String(Base64.getEncoder().encode((apiKey + ":").getBytes()));

        return new Headers.Builder()
                .add("User-Agent", "BadgeUp_SpongeClient v" + BadgeUpSponge.getContainer().getVersion().orElse("Unknown"))
                .add("Authorization", authHeader)
                .build();
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
