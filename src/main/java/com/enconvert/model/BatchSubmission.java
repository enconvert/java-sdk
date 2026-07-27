package com.enconvert.model;

/** 202 response from an async batch submission (website conversions). */
public record BatchSubmission(
        String batchId,
        /** Always "processing" on submission. */
        String status,
        /** Number of pages queued for conversion. */
        int urlCount,
        /** Total URLs found during discovery (before plan limits applied). */
        Integer totalDiscovered,
        /** How URLs were discovered: "sitemap" or "full_crawl". */
        String discoveryMethod,
        /** Output packaging, "zip" for website conversions. */
        String outputFormat) {
}
