package com.enconvert;

import com.enconvert.model.BatchItem;
import com.enconvert.model.BatchStatus;
import com.enconvert.model.BatchSubmission;
import com.enconvert.model.ConversionResult;
import com.enconvert.model.JobStatus;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/** Response mappers mirroring node-sdk's client.ts module-level helpers. */
final class V1Mappers {

    private V1Mappers() {
    }

    static ConversionResult toConversionResult(JsonObject d) {
        // Job-status fallback responses omit `filename`; recover it from the
        // object key so callers never see an empty string unexpectedly.
        String objectKey = Json.str(d, "object_key", "");
        String filename = Json.optStr(d, "filename");
        if (filename == null) {
            int idx = objectKey.lastIndexOf('/');
            filename = idx >= 0 ? objectKey.substring(idx + 1) : objectKey;
        }
        return new ConversionResult(
                Json.str(d, "presigned_url", ""),
                objectKey,
                filename,
                Json.optLong(d, "file_size"),
                Json.optDouble(d, "conversion_time_seconds"),
                Json.optStr(d, "job_id"));
    }

    static JobStatus toJobStatus(JsonObject d) {
        return new JobStatus(
                Json.str(d, "status", ""),
                Json.optStr(d, "presigned_url"),
                Json.optStr(d, "object_key"),
                Json.optStr(d, "error"));
    }

    static BatchSubmission toBatchSubmission(JsonObject d) {
        return new BatchSubmission(
                Json.str(d, "batch_id", ""),
                Json.str(d, "status", "processing"),
                Json.num(d, "url_count", 0),
                Json.optInt(d, "total_discovered"),
                Json.optStr(d, "discovery_method"),
                Json.optStr(d, "output_format"));
    }

    static BatchStatus toBatchStatus(JsonObject d) {
        List<BatchItem> items = new ArrayList<>();
        for (JsonObject item : Json.objArr(d, "items")) {
            items.add(new BatchItem(
                    Json.str(item, "source_url", ""),
                    Json.str(item, "status", ""),
                    Json.optStr(item, "download_url"),
                    Json.optLong(item, "output_file_size"),
                    Json.optStr(item, "duration")));
        }
        return new BatchStatus(
                Json.str(d, "batch_id", ""),
                Json.str(d, "status", ""),
                Json.num(d, "total", 0),
                Json.num(d, "completed", 0),
                Json.num(d, "failed", 0),
                Json.num(d, "in_progress", 0),
                Json.str(d, "output_mode", ""),
                Json.optStr(d, "zip_download_url"),
                items);
    }
}
