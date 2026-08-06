package com.enconvert.model.v2;

/**
 * Result of a direct-download perceive call ({@code perceiveDirect} /
 * {@code downloadPerceiveArtifact}): the raw artifact bytes plus the
 * metadata the server carries on response headers.
 */
public record PerceiveDirectResult(
        /** The artifact bytes. */
        byte[] content,
        /** Artifact media type, e.g. "text/markdown; charset=utf-8". */
        String contentType,
        /** Filename parsed from Content-Disposition (&lt;operation&gt;_&lt;output&gt;.&lt;ext&gt;). Null when the header is absent. */
        String filename,
        String operationId,
        String objectKey,
        boolean cacheHit,
        /** 0.0-1.0 render quality score. Null when the header is absent. */
        Double renderQuality,
        /** HTTP status of the upstream main-document response. Null when the header is absent. */
        Integer sourceStatusCode,
        /** SHA-256 of the rendered content. Null when the header is absent. */
        String contentHash,
        /** Number of warnings the render produced. 0 when the header is absent. */
        int warningsCount) {
}
