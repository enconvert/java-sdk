package com.enconvert;

import com.enconvert.model.v2.DiscoverResult;
import com.enconvert.model.v2.DistillItem;
import com.enconvert.model.v2.DistillResult;
import com.enconvert.model.v2.IngestJob;
import com.enconvert.model.v2.IngestJobSummary;
import com.enconvert.model.v2.LookupItem;
import com.enconvert.model.v2.LookupResult;
import com.enconvert.model.v2.PerceiveBatchResult;
import com.enconvert.model.v2.PerceiveDirectResult;
import com.enconvert.model.v2.PerceiveResult;
import com.enconvert.model.v2.V2OutputArtifact;
import com.enconvert.model.v2.V2Tokens;
import com.enconvert.model.v2.Watcher;
import com.enconvert.model.v2.WatcherSnapshot;
import com.enconvert.model.v2.WatcherSummary;
import com.enconvert.model.v2.WebhookSecret;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Response mappers mirroring node-sdk's v2.ts. Every field is read
 * defensively — V2 responses use {@code response_model_exclude_none}, so any
 * field may be entirely absent.
 */
final class V2Mappers {

    private V2Mappers() {
    }

    static V2Tokens toTokens(JsonObject d) {
        JsonObject t = d;
        return new V2Tokens(Json.num(t, "input", 0), Json.num(t, "output", 0));
    }

    static V2OutputArtifact toOutputArtifact(JsonObject d) {
        return new V2OutputArtifact(
                Json.optStr(d, "url"),
                Json.str(d, "object_key", ""),
                Json.numLong(d, "size_bytes", 0),
                Json.str(d, "content_type", "application/octet-stream"),
                Json.num(d, "expires_in", 900));
    }

    static PerceiveResult toPerceiveResult(JsonObject d) {
        Map<String, V2OutputArtifact> outputs = new LinkedHashMap<>();
        JsonObject rawOutputs = Json.optObj(d, "outputs");
        if (rawOutputs != null) {
            for (Map.Entry<String, JsonElement> entry : rawOutputs.entrySet()) {
                if (entry.getValue().isJsonObject()) {
                    outputs.put(entry.getKey(), toOutputArtifact(entry.getValue().getAsJsonObject()));
                }
            }
        }
        Map<String, Double> deductions = new LinkedHashMap<>();
        JsonObject rawDeductions = Json.optObj(d, "deductions");
        if (rawDeductions != null) {
            for (Map.Entry<String, JsonElement> entry : rawDeductions.entrySet()) {
                if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isNumber()) {
                    deductions.put(entry.getKey(), entry.getValue().getAsDouble());
                }
            }
        }
        JsonObject tokens = Json.optObj(d, "tokens");
        return new PerceiveResult(
                Json.str(d, "operation_id", ""),
                Json.str(d, "status", ""),
                Json.str(d, "url", ""),
                Json.optStr(d, "url_final"),
                Json.optStr(d, "content_hash"),
                Json.optDouble(d, "render_quality"),
                Json.optInt(d, "status_code"),
                deductions,
                Json.bool(d, "cache_hit"),
                outputs,
                Json.optObj(d, "structured"),
                Json.optStr(d, "extraction_tier"),
                tokens != null ? toTokens(tokens) : new V2Tokens(0, 0),
                Json.num(d, "cost_cents", 0),
                Json.optInt(d, "duration_ms"),
                Json.optStr(d, "error"),
                Json.strArr(d, "warnings"),
                Json.optObj(d, "options_echo"));
    }

    /** Builds a direct-download result from the raw body + response headers. */
    static PerceiveDirectResult toPerceiveDirectResult(HttpResponse<byte[]> resp) {
        HttpHeaders h = resp.headers();
        Integer warningsCount = headerInt(h, "X-Warnings-Count");
        return new PerceiveDirectResult(
                resp.body(),
                h.firstValue("Content-Type").orElse("application/octet-stream"),
                filenameFromContentDisposition(h.firstValue("Content-Disposition").orElse(null)),
                h.firstValue("X-Operation-Id").orElse(""),
                h.firstValue("X-Object-Key").orElse(""),
                "true".equals(h.firstValue("X-Cache-Hit").orElse(null)),
                headerDouble(h, "X-Render-Quality"),
                headerInt(h, "X-Source-Status-Code"),
                h.firstValue("X-Content-Hash").orElse(null),
                warningsCount != null ? warningsCount : 0);
    }

    /** Extracts the filename="..." (or bare-token) value, or null when absent. */
    private static String filenameFromContentDisposition(String value) {
        if (value == null) return null;
        for (String part : value.split(";")) {
            String trimmed = part.trim();
            if (!trimmed.toLowerCase().startsWith("filename=")) continue;
            String filename = trimmed.substring("filename=".length());
            if (filename.length() >= 2 && filename.startsWith("\"") && filename.endsWith("\"")) {
                filename = filename.substring(1, filename.length() - 1);
            }
            return filename.isEmpty() ? null : filename;
        }
        return null;
    }

    private static Double headerDouble(HttpHeaders headers, String name) {
        try {
            return headers.firstValue(name).map(Double::valueOf).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer headerInt(HttpHeaders headers, String name) {
        try {
            return headers.firstValue(name).map(Integer::valueOf).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static PerceiveBatchResult toPerceiveBatchResult(JsonObject d) {
        List<PerceiveResult> items = new ArrayList<>();
        for (JsonObject item : Json.objArr(d, "items")) {
            items.add(toPerceiveResult(item));
        }
        JsonObject zip = Json.optObj(d, "zip");
        return new PerceiveBatchResult(
                Json.str(d, "job_id", ""),
                Json.str(d, "status", ""),
                Json.str(d, "output_mode", "manifest"),
                Json.num(d, "total", 0),
                Json.num(d, "completed", 0),
                Json.num(d, "failed", 0),
                Json.num(d, "pending", 0),
                zip != null ? toOutputArtifact(zip) : null,
                items,
                Json.strArr(d, "warnings"));
    }

    static DiscoverResult toDiscoverResult(JsonObject d) {
        Map<String, Integer> sources = new LinkedHashMap<>();
        JsonObject rawSources = Json.optObj(d, "sources");
        if (rawSources != null) {
            for (Map.Entry<String, JsonElement> entry : rawSources.entrySet()) {
                if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isNumber()) {
                    sources.put(entry.getKey(), entry.getValue().getAsInt());
                }
            }
        }
        return new DiscoverResult(
                Json.str(d, "url", ""),
                Json.str(d, "mode", ""),
                Json.num(d, "total", 0),
                Json.strArr(d, "urls"),
                Json.num(d, "pages_crawled", 0),
                Json.bool(d, "truncated"),
                Json.bool(d, "robots_respected"),
                sources,
                Json.strArr(d, "warnings"));
    }

    static LookupItem toLookupItem(JsonObject d) {
        JsonObject perceive = Json.optObj(d, "perceive");
        JsonObject extra = Json.optObj(d, "extra");
        return new LookupItem(
                Json.optStr(d, "title"),
                Json.optStr(d, "url"),
                Json.optStr(d, "snippet"),
                Json.optInt(d, "position"),
                Json.optStr(d, "source"),
                Json.optStr(d, "date"),
                Json.optStr(d, "image_url"),
                Json.optStr(d, "thumbnail_url"),
                extra != null ? extra : new JsonObject(),
                perceive != null ? toPerceiveResult(perceive) : null);
    }

    static LookupResult toLookupResult(JsonObject d) {
        List<LookupItem> results = new ArrayList<>();
        for (JsonObject item : Json.objArr(d, "results")) {
            results.add(toLookupItem(item));
        }
        return new LookupResult(
                Json.optInt(d, "lookup_id"),
                Json.str(d, "query", ""),
                Json.str(d, "category", ""),
                Json.optStr(d, "country"),
                Json.optStr(d, "locale"),
                Json.optStr(d, "time_filter"),
                Json.num(d, "total", 0),
                results,
                Json.num(d, "perceive_top", 0),
                Json.strArr(d, "perceive_operation_ids"),
                Json.optObj(d, "answer_box"),
                Json.optObj(d, "knowledge_graph"),
                Json.optInt(d, "credits"),
                Json.num(d, "cost_cents", 0),
                Json.strArr(d, "warnings"));
    }

    static DistillItem toDistillItem(JsonObject d) {
        JsonObject tokens = Json.optObj(d, "tokens");
        return new DistillItem(
                Json.str(d, "url", ""),
                Json.optStr(d, "url_final"),
                Json.str(d, "status", "completed"),
                Json.optObj(d, "data"),
                Json.str(d, "extraction_tier", "none"),
                Json.num(d, "fields_from_css", 0),
                Json.num(d, "fields_from_llm", 0),
                Json.optDouble(d, "render_quality"),
                tokens != null ? toTokens(tokens) : new V2Tokens(0, 0),
                Json.num(d, "cost_cents", 0),
                Json.optStr(d, "error"),
                Json.strArr(d, "warnings"));
    }

    static DistillResult toDistillResult(JsonObject d) {
        List<DistillItem> results = new ArrayList<>();
        for (JsonObject item : Json.objArr(d, "results")) {
            results.add(toDistillItem(item));
        }
        return new DistillResult(
                Json.str(d, "operation_id", ""),
                Json.num(d, "total", 0),
                Json.num(d, "completed", 0),
                Json.num(d, "failed", 0),
                results,
                Json.num(d, "total_cost_cents", 0),
                Json.strArr(d, "warnings"));
    }

    static IngestJob toIngestJob(JsonObject d) {
        return new IngestJob(
                Json.str(d, "job_id", ""),
                Json.str(d, "status", ""),
                Json.str(d, "mode", ""),
                Json.num(d, "pages_discovered", 0),
                Json.num(d, "pages_processed", 0),
                Json.num(d, "pages_failed", 0),
                Json.num(d, "total_chunks", 0),
                Json.optStr(d, "output_url"),
                Json.optStr(d, "error_message"),
                Json.optStr(d, "webhook_url"),
                Json.bool(d, "webhook_delivered"),
                Json.optStr(d, "created_at"),
                Json.optStr(d, "completed_at"),
                Json.strArr(d, "warnings"));
    }

    static IngestJobSummary toIngestJobSummary(JsonObject d) {
        return new IngestJobSummary(
                Json.str(d, "job_id", ""),
                Json.str(d, "status", ""),
                Json.str(d, "mode", ""),
                Json.num(d, "pages_discovered", 0),
                Json.num(d, "pages_processed", 0),
                Json.num(d, "pages_failed", 0),
                Json.num(d, "total_chunks", 0),
                Json.optStr(d, "output_url"),
                Json.optStr(d, "error_message"),
                Json.bool(d, "webhook_configured"),
                Json.bool(d, "webhook_delivered"),
                Json.optStr(d, "created_at"),
                Json.optStr(d, "completed_at"));
    }

    static WebhookSecret toWebhookSecret(JsonObject d) {
        return new WebhookSecret(
                Json.str(d, "secret", ""),
                Json.str(d, "signature_header", ""),
                Json.str(d, "timestamp_header", ""),
                Json.str(d, "signature_scheme", ""),
                Json.num(d, "replay_tolerance_seconds", 0),
                Json.bool(d, "rotated"));
    }

    static Watcher toWatcher(JsonObject d) {
        return new Watcher(
                Json.str(d, "watcher_id", ""),
                Json.str(d, "url", ""),
                Json.str(d, "status", ""),
                Json.num(d, "frequency_minutes", 0),
                Json.str(d, "diff_mode", ""),
                Json.optObj(d, "track_fields"),
                Json.optStr(d, "webhook_url"),
                Json.boolDefaultTrue(d, "notify_email"),
                Json.num(d, "consecutive_errors", 0),
                Json.num(d, "checks_count", 0),
                Json.optStr(d, "last_check_at"),
                Json.optStr(d, "next_check_at"),
                Json.optStr(d, "last_change_at"),
                Json.optStr(d, "created_at"),
                Json.optStr(d, "updated_at"));
    }

    static WatcherSummary toWatcherSummary(JsonObject d) {
        return new WatcherSummary(
                Json.str(d, "watcher_id", ""),
                Json.str(d, "url", ""),
                Json.str(d, "status", ""),
                Json.num(d, "frequency_minutes", 0),
                Json.num(d, "checks_count", 0),
                Json.num(d, "consecutive_errors", 0),
                Json.optStr(d, "last_check_at"),
                Json.optStr(d, "next_check_at"),
                Json.optStr(d, "last_change_at"),
                Json.optStr(d, "created_at"));
    }

    static WatcherSnapshot toWatcherSnapshot(JsonObject d) {
        return new WatcherSnapshot(
                Json.str(d, "checked_at", ""),
                Json.bool(d, "has_changes"),
                Json.optDouble(d, "similarity"),
                Json.optDouble(d, "render_quality"),
                Json.num(d, "change_count", 0),
                Json.objArr(d, "changes"));
    }
}
