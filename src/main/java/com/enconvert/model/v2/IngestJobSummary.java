package com.enconvert.model.v2;

/** Compact job row from {@code listIngestJobs} (webhookUrl replaced by a flag). */
public record IngestJobSummary(
        String jobId,
        String status,
        String mode,
        int pagesDiscovered,
        int pagesProcessed,
        int pagesFailed,
        int totalChunks,
        String outputUrl,
        String errorMessage,
        boolean webhookConfigured,
        boolean webhookDelivered,
        String createdAt,
        String completedAt) {
}
