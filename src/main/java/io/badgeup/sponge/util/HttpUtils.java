package io.badgeup.sponge.util;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.Config;
import okhttp3.ConnectionPool;
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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpUtils {

    private static final String V1_APPS = "/v1/apps/";
    private static Logger httpClientLogger = Logger.getLogger(OkHttpClient.class.getName());
    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(5, 15, TimeUnit.SECONDS))
            .pingInterval(30, TimeUnit.SECONDS)
            .build();
    private static String appId = Util.parseAppIdFromAPIKey(BadgeUpSponge.getConfig().getBadgeUpConfig().getAPIKey()).get();
    private static String baseUrl = getApiBaseUrl();
    private static Headers headers = getHeaders();

    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_NOT_FOUND = 404;
    // https://tools.ietf.org/html/rfc6455#section-7.4
    public static final int WS_CLOSED = 1000;

    static {
        // Set the log level to FINE so that stacktraces from leaked responses
        // are printed out
        httpClientLogger.setLevel(Level.FINE);
    }

    public static Response post(String url, JSONObject body) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse(body.toString()), body.toString());
        Request request = new Request.Builder().url(baseUrl + V1_APPS + appId + url).headers(headers).post(requestBody).build();

        return httpClient.newCall(request).execute();
    }

    public static Response getRaw(String url) throws IOException {
        Request request = new Request.Builder().url(baseUrl + url).headers(headers).get().build();
        return httpClient.newCall(request).execute();
    }

    public static Response get(String url) throws IOException {
        Request request = new Request.Builder().url(baseUrl + V1_APPS + appId + url).headers(headers).get().build();
        return httpClient.newCall(request).execute();
    }

    public static Request getRawRequest(String url) {
        return new Request.Builder().url(baseUrl + url).headers(headers).get().build();
    }

    public static Request getRequest(String url) {
        return new Request.Builder().url(baseUrl + V1_APPS + appId + url).headers(headers).get().build();
    }

    public static JSONObject parseBody(Response response) throws JSONException, IOException {
        return new JSONObject(response.body().string());
    }

    public static OkHttpClient getHttpClient() {
        return httpClient;
    }

    public static String getWebSocketUrl() {
        String baseUrl = getApiBaseUrl().replace("https://", "ws://").replace("http://", "ws://");
        return baseUrl + V1_APPS + appId + "/events/streams/create?authorization=" + getAuthHeader();
    }

    private static Headers getHeaders() {
        return new Headers.Builder()
                .add("User-Agent", "BadgeUp_SpongeClient v" + BadgeUpSponge.getContainer().getVersion().orElse("Unknown"))
                .add("Authorization", getAuthHeader())
                .build();
    }

    private static String getAuthHeader() {
        String apiKey = BadgeUpSponge.getConfig().getBadgeUpConfig().getAPIKey();
        return "Basic " + new String(Base64.getEncoder().encode((apiKey + ":").getBytes()));
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
