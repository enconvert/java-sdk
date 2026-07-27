package com.enconvert.model.v2;

/** Result of {@code EnconvertV2#retryIngestWebhook}. */
public record WebhookRetryResult(
        String jobId,
        boolean delivered,
        int attempts,
        /** HTTP status of the last attempt; null on network error. */
        Integer statusCode,
        String detail) {
}
