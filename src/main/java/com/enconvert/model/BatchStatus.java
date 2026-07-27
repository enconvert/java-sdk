package com.enconvert.model;

import java.util.List;

/** Aggregate status of an async website-conversion batch. */
public record BatchStatus(
        String batchId,
        /** One of "processing", "completed", "partial", "failed". */
        String status,
        int total,
        int completed,
        int failed,
        int inProgress,
        /** "zip" or "individual". */
        String outputMode,
        /** Presigned URL of the bundled ZIP when outputMode is "zip". */
        String zipDownloadUrl,
        List<BatchItem> items) {
}
