package com.enconvert.model;

/** Per-URL entry in a batch status response. */
public record BatchItem(
        String sourceUrl,
        /** Raw activity status: "In Progress", "Success", or "Failed". */
        String status,
        String downloadUrl,
        Long outputFileSize,
        String duration) {
}
