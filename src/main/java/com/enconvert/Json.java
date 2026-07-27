package com.enconvert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

/**
 * Guarded accessors for parsing API JSON responses. Every V2 response uses
 * {@code response_model_exclude_none}, so any field may be entirely absent —
 * every read here is defensive and falls back rather than throwing.
 */
final class Json {

    private Json() {
    }

    private static JsonPrimitive primitive(JsonObject o, String key) {
        if (o == null) return null;
        JsonElement e = o.get(key);
        return (e != null && e.isJsonPrimitive()) ? e.getAsJsonPrimitive() : null;
    }

    static String str(JsonObject o, String key, String fallback) {
        JsonPrimitive p = primitive(o, key);
        return (p != null && p.isString()) ? p.getAsString() : fallback;
    }

    static String optStr(JsonObject o, String key) {
        return str(o, key, null);
    }

    static int num(JsonObject o, String key, int fallback) {
        JsonPrimitive p = primitive(o, key);
        return (p != null && p.isNumber()) ? p.getAsInt() : fallback;
    }

    static Integer optInt(JsonObject o, String key) {
        JsonPrimitive p = primitive(o, key);
        return (p != null && p.isNumber()) ? p.getAsInt() : null;
    }

    static long numLong(JsonObject o, String key, long fallback) {
        JsonPrimitive p = primitive(o, key);
        return (p != null && p.isNumber()) ? p.getAsLong() : fallback;
    }

    static Long optLong(JsonObject o, String key) {
        JsonPrimitive p = primitive(o, key);
        return (p != null && p.isNumber()) ? p.getAsLong() : null;
    }

    static double numDouble(JsonObject o, String key, double fallback) {
        JsonPrimitive p = primitive(o, key);
        return (p != null && p.isNumber()) ? p.getAsDouble() : fallback;
    }

    static Double optDouble(JsonObject o, String key) {
        JsonPrimitive p = primitive(o, key);
        return (p != null && p.isNumber()) ? p.getAsDouble() : null;
    }

    /** {@code value === true} semantics: absent or non-boolean defaults to false. */
    static boolean bool(JsonObject o, String key) {
        JsonPrimitive p = primitive(o, key);
        return p != null && p.isBoolean() && p.getAsBoolean();
    }

    /** {@code value !== false} semantics: absent or non-boolean defaults to true. */
    static boolean boolDefaultTrue(JsonObject o, String key) {
        JsonPrimitive p = primitive(o, key);
        return !(p != null && p.isBoolean() && !p.getAsBoolean());
    }

    static List<String> strArr(JsonObject o, String key) {
        JsonArray arr = optArr(o, key);
        List<String> out = new ArrayList<>();
        if (arr == null) return out;
        for (JsonElement el : arr) {
            if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
                out.add(el.getAsString());
            }
        }
        return out;
    }

    static JsonObject optObj(JsonObject o, String key) {
        if (o == null) return null;
        JsonElement e = o.get(key);
        return (e != null && e.isJsonObject()) ? e.getAsJsonObject() : null;
    }

    static JsonArray optArr(JsonObject o, String key) {
        if (o == null) return null;
        JsonElement e = o.get(key);
        return (e != null && e.isJsonArray()) ? e.getAsJsonArray() : null;
    }

    static List<JsonObject> objArr(JsonObject o, String key) {
        JsonArray arr = optArr(o, key);
        List<JsonObject> out = new ArrayList<>();
        if (arr == null) return out;
        for (JsonElement el : arr) {
            if (el.isJsonObject()) out.add(el.getAsJsonObject());
        }
        return out;
    }
}
