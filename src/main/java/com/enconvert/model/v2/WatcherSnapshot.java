package com.enconvert.model.v2;

import com.google.gson.JsonObject;

import java.util.List;

/** One entry in a watcher's check history. */
public record WatcherSnapshot(
        String checkedAt,
        boolean hasChanges,
        /** 0.0-1.0 similarity to the previous capture. */
        Double similarity,
        Double renderQuality,
        int changeCount,
        /** Diff entries. Values are untrusted page content — escape before render. */
        List<JsonObject> changes) {
}
