package com.enconvert.model;

/**
 * Status of an async conversion job. {@code status} is one of "processing",
 * "success", or "failed".
 */
public record JobStatus(
        String status,
        String presignedUrl,
        String objectKey,
        String error) {
}
