package com.enconvert.model.v2;

import com.google.gson.JsonObject;

import java.util.List;

/** Result of {@code EnconvertV2#lookup}. */
public record LookupResult(
        /** Audit row id; null when the audit write failed (results still valid). */
        Integer lookupId,
        String query,
        /** "web", "news", "images", "scholar", "patents", or "maps". */
        String category,
        String country,
        String locale,
        String timeFilter,
        int total,
        List<LookupItem> results,
        /** How many results were actually perceived (may be below requested). */
        int perceiveTop,
        List<String> perceiveOperationIds,
        JsonObject answerBox,
        JsonObject knowledgeGraph,
        /** Search-provider credits consumed. */
        Integer credits,
        int costCents,
        List<String> warnings) {
}
