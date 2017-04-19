package io.badgeup.sponge.util;

import org.json.JSONObject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Util {

    public static Optional<String> parseAppIdFromAPIKey(String apiKey) {
        try {
            // Base64 decode the API key
            final byte[] decodedKey = Base64.getDecoder().decode(apiKey);
            JSONObject keyObj = new JSONObject(new String(decodedKey));
            final String appId = keyObj.getString("applicationId");
            return appId.isEmpty() ? Optional.empty() : Optional.of(appId);
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    public static Optional<Double> safeParseDouble(String raw) {
        try {
            return Optional.of(Double.parseDouble(raw));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<Object> safeGet(JSONObject obj, String key) {
        try {
            return Optional.of(obj.get(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<JSONObject> safeGetJSONObject(JSONObject obj, String key) {
        try {
            return Optional.of(obj.getJSONObject(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<BigDecimal> safeGetBigDecimal(JSONObject obj, String key) {
        try {
            return Optional.of(obj.getBigDecimal(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<Integer> safeGetInt(JSONObject obj, String key) {
        try {
            return Optional.of(obj.getInt(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> safeGetDouble(JSONObject obj, String key) {
        try {
            return Optional.of(obj.getDouble(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<String> safeGetString(JSONObject obj, String key) {
        try {
            return Optional.of(obj.getString(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<Boolean> safeGetBoolean(JSONObject obj, String key) {
        try {
            return Optional.of(obj.getBoolean(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<List<Object>> safeGetList(JSONObject obj, String key) {
        try {
            List<Object> entries = new ArrayList<>();
            obj.getJSONArray(key).forEach(entries::add);
            return Optional.of(entries);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<Text> deserializeText(Object serialized) {
        if (serialized == null) {
            return Optional.empty();
        } else if (serialized instanceof String) {
            return Optional.of(TextSerializers.FORMATTING_CODE.deserializeUnchecked((String) serialized));
        } else if (serialized instanceof JSONObject) {
            return Optional.of(TextSerializers.FORMATTING_CODE.deserializeUnchecked(serialized.toString()));
        } else {
            return Optional.empty();
        }
    }

    // From
    // http://www.nurkiewicz.com/2013/05/java-8-completablefuture-in-action.html
    public static <T> CompletableFuture<List<T>> sequence(Collection<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v -> futures.stream().map(future -> future.join()).collect(Collectors.<T>toList()));
    }

}
