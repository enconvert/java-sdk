package com.enconvert.model.v2;

/** Compact watcher row from {@code listWatchers}. */
public record WatcherSummary(
        String watcherId,
        String url,
        String status,
        int frequencyMinutes,
        int checksCount,
        int consecutiveErrors,
        String lastCheckAt,
        String nextCheckAt,
        String lastChangeAt,
        String createdAt) {
}
