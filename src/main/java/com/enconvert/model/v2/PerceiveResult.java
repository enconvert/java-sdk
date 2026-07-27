package com.enconvert.model.v2;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

/** Result of a single perceive operation. */
public record PerceiveResult(
        String operationId,
        /** "queued", "processing", "completed", or "failed". */
        String status,
        String url,
        String urlFinal,
        String contentHash,
        /** 0.0-1.0 render quality score. */
        Double renderQuality,
        boolean cacheHit,
        /** Keyed by output name (e.g. "markdown", "screenshot_full_page"). */
        Map<String, V2OutputArtifact> outputs,
        /** Present when extract/schema was requested. Shape is caller-defined. */
        JsonObject structured,
        /** "heuristic", "css", or "llm". */
        String extractionTier,
        V2Tokens tokens,
        int costCents,
        Integer durationMs,
        String error,
        List<String> warnings) {
}
