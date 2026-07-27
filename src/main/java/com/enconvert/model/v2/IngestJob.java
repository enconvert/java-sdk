package com.enconvert.model.v2;

import java.util.List;

/** Full ingest job detail. */
public record IngestJob(
        String jobId,
        /** "queued", "discovering", "processing", "completed", "failed", or "canceled". */
        String status,
        /** "urls", "sitemap", or "crawl". */
        String mode,
        int pagesDiscovered,
        int pagesProcessed,
        int pagesFailed,
        int totalChunks,
        /** Signed URL to the final JSONL, once completed. */
        String outputUrl,
        String errorMessage,
        String webhookUrl,
        boolean webhookDelivered,
        String createdAt,
        String completedAt,
        List<String> warnings) {
}
