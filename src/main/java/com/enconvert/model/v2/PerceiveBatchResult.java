package com.enconvert.model.v2;

import java.util.List;

/** Result of a {@code perceiveBatch} submission or poll. */
public record PerceiveBatchResult(
        String jobId,
        /** "queued", "processing", "completed", "failed", or "partial". */
        String status,
        /** "manifest" or "zip". */
        String outputMode,
        int total,
        int completed,
        int failed,
        int pending,
        /** Bundle of every successful artifact (outputMode "zip", once done). */
        V2OutputArtifact zip,
        /** One entry per URL. Empty on the initial 202 — poll getPerceiveBatch. */
        List<PerceiveResult> items,
        List<String> warnings) {
}
