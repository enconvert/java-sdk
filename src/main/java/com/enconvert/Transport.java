package com.enconvert;

import com.enconvert.exceptions.ApiException;
import com.enconvert.exceptions.AuthenticationException;
import com.enconvert.exceptions.EnconvertException;
import com.enconvert.exceptions.QuotaException;
import com.enconvert.exceptions.RateLimitException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Shared, authenticated, timeout-wrapped HTTP transport bound to a base URL.
 * Used internally by both {@link Enconvert} (V1) and {@link EnconvertV2}.
 */
final class Transport {

    final Gson gson = new Gson();

    private final String apiKey;
    private final String baseUrl;
    private final Duration timeout;
    private final HttpClient httpClient;

    Transport(String apiKey, String baseUrl, Duration timeout) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.timeout = timeout;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
    }

    /** Authenticated request against the API base URL. */
    HttpResponse<byte[]> send(String path, String method, byte[] body, String contentType) {
        HttpRequest.BodyPublisher publisher = body == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofByteArray(body);
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(timeout)
                .header("X-API-Key", apiKey)
                .method(method, publisher);
        if (contentType != null) {
            builder.header("Content-Type", contentType);
        }
        try {
            return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException e) {
            throw new EnconvertException("Request to " + path + " failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EnconvertException("Request to " + path + " was interrupted", e);
        }
    }

    /** Send, raise on error status, and parse the JSON object response body. */
    JsonObject requestJson(String path, String method, byte[] body, String contentType) {
        HttpResponse<byte[]> resp = send(path, method, body, contentType);
        raiseForStatus(resp);
        return parseObject(resp.body());
    }

    JsonObject parseObject(byte[] bytes) {
        String text = new String(bytes, StandardCharsets.UTF_8);
        if (text.isBlank()) return new JsonObject();
        try {
            JsonElement el = JsonParser.parseString(text);
            return el.isJsonObject() ? el.getAsJsonObject() : new JsonObject();
        } catch (JsonParseException e) {
            return new JsonObject();
        }
    }

    int statusCode(HttpResponse<byte[]> resp) {
        return resp.statusCode();
    }

    void raiseForStatus(HttpResponse<byte[]> resp) {
        int status = resp.statusCode();
        if (status < 400) return;

        String text = new String(resp.body(), StandardCharsets.UTF_8);
        String message;
        if (text.isEmpty()) {
            message = "HTTP " + status;
        } else {
            String parsed = null;
            try {
                JsonElement el = JsonParser.parseString(text);
                if (el.isJsonObject()) {
                    JsonObject obj = el.getAsJsonObject();
                    String detail = Json.optStr(obj, "detail");
                    String error = Json.optStr(obj, "error");
                    if (detail != null && !detail.isEmpty()) {
                        parsed = detail;
                    } else if (error != null && !error.isEmpty()) {
                        parsed = error;
                    } else {
                        parsed = obj.toString();
                    }
                } else {
                    parsed = el.toString();
                }
            } catch (JsonParseException e) {
                parsed = text;
            }
            message = parsed;
        }

        if (status == 401 || status == 403) throw new AuthenticationException(message);
        if (status == 402) throw new QuotaException(message);
        if (status == 429) throw new RateLimitException(message);
        throw new ApiException(status, message);
    }

    /**
     * Plain GET with no API key header — used to download presigned S3
     * URLs, which are already signed and must not carry our credentials.
     */
    HttpResponse<InputStream> getUnauthenticated(String url) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(timeout)
                .GET()
                .build();
        try {
            return httpClient.send(req, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException e) {
            throw new EnconvertException("Download from " + url + " failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EnconvertException("Download from " + url + " was interrupted", e);
        }
    }
}
