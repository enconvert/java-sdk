package com.enconvert.model.v2;

import com.google.gson.JsonObject;

/** Full watcher detail. */
public record Watcher(
        String watcherId,
        String url,
        /** "active", "paused", or "deleted". */
        String status,
        int frequencyMinutes,
        /** "auto", "text", "structured", "tables", or "metadata". */
        String diffMode,
        JsonObject trackFields,
        String webhookUrl,
        boolean notifyEmail,
        int consecutiveErrors,
        int checksCount,
        String lastCheckAt,
        String nextCheckAt,
        String lastChangeAt,
        String createdAt,
        String updatedAt) {
}
