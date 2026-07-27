package com.enconvert.model;

/** Result of a single-file or single-URL conversion. */
public record ConversionResult(
        String presignedUrl,
        String objectKey,
        String filename,
        Long fileSize,
        Double conversionTimeSeconds,
        String jobId) {
}
