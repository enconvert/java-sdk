package com.enconvert.model.v2;

import com.google.gson.JsonObject;

import java.util.List;

/** Per-URL distill outcome. */
public record DistillItem(
        String url,
        String urlFinal,
        /** "completed" or "failed". */
        String status,
        /** Extracted data matching the requested schema. */
        JsonObject data,
        /** "css", "llm", "mixed", or "none". */
        String extractionTier,
        int fieldsFromCss,
        int fieldsFromLlm,
        Double renderQuality,
        V2Tokens tokens,
        int costCents,
        String error,
        List<String> warnings) {
}
