package net.shadow.utils.wjson;

import com.google.gson.*;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public enum JsonUtils {
    ;

    public static final Gson GSON = new Gson();

    public static final Gson PRETTY_GSON =
            new GsonBuilder().setPrettyPrinting().create();

    public static final JsonParser JSON_PARSER = new JsonParser();

    public static JsonElement parseFile(Path path)
            throws IOException, JsonException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return JSON_PARSER.parse(reader);

        } catch (JsonParseException e) {
            throw new JsonException(e);
        }
    }

    public static JsonElement parseURL(String url)
            throws IOException, JsonException {
        URI uri = URI.create(url);
        try (InputStream input = uri.toURL().openStream()) {
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(reader);
            return new JsonParser().parse(bufferedReader);

        } catch (JsonParseException e) {
            throw new JsonException(e);
        }
    }

    public static WsonArray parseFileToArray(Path path)
            throws IOException, JsonException {
        JsonElement json = parseFile(path);

        if (!json.isJsonArray())
            throw new JsonException();

        return new WsonArray(json.getAsJsonArray());
    }

    public static WsonArray parseURLToArray(String url)
            throws IOException, JsonException {
        JsonElement json = parseURL(url);

        if (!json.isJsonArray())
            throw new JsonException();

        return new WsonArray(json.getAsJsonArray());
    }

    public static WsonObject parseFileToObject(Path path)
            throws IOException, JsonException {
        JsonElement json = parseFile(path);

        if (!json.isJsonObject())
            throw new JsonException();

        return new WsonObject(json.getAsJsonObject());
    }

    public static WsonObject parseURLToObject(String url)
            throws IOException, JsonException {
        JsonElement json = parseURL(url);

        if (!json.isJsonObject())
            throw new JsonException();

        return new WsonObject(json.getAsJsonObject());
    }

    public static void toJson(JsonElement json, Path path)
            throws IOException, JsonException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            JsonUtils.PRETTY_GSON.toJson(json, writer);

        } catch (JsonParseException e) {
            throw new JsonException(e);
        }
    }

    public static boolean isBoolean(JsonElement json) {
        if (json == null || !json.isJsonPrimitive())
            return false;

        JsonPrimitive primitive = json.getAsJsonPrimitive();
        return primitive.isBoolean();
    }

    public static boolean getAsBoolean(JsonElement json) throws JsonException {
        if (!isBoolean(json))
            throw new JsonException();

        return json.getAsBoolean();
    }

    public static boolean getAsBoolean(JsonElement json, boolean fallback) {
        if (!isBoolean(json))
            return fallback;

        return json.getAsBoolean();
    }

    public static boolean isNumber(JsonElement json) {
        if (json == null || !json.isJsonPrimitive())
            return false;

        JsonPrimitive primitive = json.getAsJsonPrimitive();
        return primitive.isNumber();
    }

    public static int getAsInt(JsonElement json) throws JsonException {
        if (!isNumber(json))
            throw new JsonException();

        return json.getAsInt();
    }

    public static int getAsInt(JsonElement json, int fallback) {
        if (!isNumber(json))
            return fallback;

        return json.getAsInt();
    }

    public static long getAsLong(JsonElement json) throws JsonException {
        if (!isNumber(json))
            throw new JsonException();

        return json.getAsLong();
    }

    public static long getAsLong(JsonElement json, long fallback) {
        if (!isNumber(json))
            return fallback;

        return json.getAsLong();
    }

    public static boolean isString(JsonElement json) {
        if (json == null || !json.isJsonPrimitive())
            return false;

        JsonPrimitive primitive = json.getAsJsonPrimitive();
        return primitive.isString();
    }

    public static String getAsString(JsonElement json) throws JsonException {
        if (!isString(json))
            throw new JsonException();

        return json.getAsString();
    }

    public static String getAsString(JsonElement json, String fallback) {
        if (!isString(json))
            return fallback;

        return json.getAsString();
    }

    public static WsonArray getAsArray(JsonElement json) throws JsonException {
        if (!json.isJsonArray())
            throw new JsonException();

        return new WsonArray(json.getAsJsonArray());
    }
}
