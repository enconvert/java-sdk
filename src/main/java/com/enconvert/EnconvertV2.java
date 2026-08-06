package com.enconvert;

import com.enconvert.model.v2.DiscoverOptions;
import com.enconvert.model.v2.DiscoverResult;
import com.enconvert.model.v2.DistillOptions;
import com.enconvert.model.v2.DistillResult;
import com.enconvert.model.v2.IngestChunkOptions;
import com.enconvert.model.v2.IngestFilesOptions;
import com.enconvert.model.v2.IngestJob;
import com.enconvert.model.v2.IngestJobList;
import com.enconvert.model.v2.IngestJobSummary;
import com.enconvert.model.v2.IngestOptions;
import com.enconvert.model.v2.LookupOptions;
import com.enconvert.model.v2.LookupResult;
import com.enconvert.model.v2.PerceiveBatchOptions;
import com.enconvert.model.v2.PerceiveBatchResult;
import com.enconvert.model.v2.PerceiveDirectResult;
import com.enconvert.model.v2.PerceiveOptions;
import com.enconvert.model.v2.PerceiveResult;
import com.enconvert.model.v2.SnapshotListOptions;
import com.enconvert.model.v2.V2ListOptions;
import com.enconvert.model.v2.WatchCreateOptions;
import com.enconvert.model.v2.Watcher;
import com.enconvert.model.v2.WatcherList;
import com.enconvert.model.v2.WatcherSnapshot;
import com.enconvert.model.v2.WatcherSnapshotList;
import com.enconvert.model.v2.WatcherSummary;
import com.enconvert.model.v2.WatcherUpdate;
import com.enconvert.model.v2.WebhookRetryResult;
import com.enconvert.model.v2.WebhookSecret;
import com.google.gson.JsonObject;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * V2 API namespace, reached as {@code client.v2}.
 *
 * <p>One method per V2 endpoint (21 total across six groups: perceive,
 * discover, lookup, distill, ingest, watch). Options are camelCase Java
 * getters, serialized to the API's snake_case wire format; responses are
 * mapped back the same way. User-data payloads (schemas, extracted data,
 * tracked fields, diff changes) pass through untouched.
 *
 * <p>All V2 endpoints require a private API key (public keys are rejected)
 * and are plan-gated: a disabled feature or exhausted monthly quota raises
 * {@link com.enconvert.exceptions.QuotaException} (HTTP 402).
 */
public final class EnconvertV2 {

    private final Transport transport;

    EnconvertV2(Transport transport) {
        this.transport = transport;
    }

    // ------------------------------------------------------------------
    // Perceive — render a URL into agent-ready artifacts
    // ------------------------------------------------------------------

    public PerceiveResult perceive(String url) {
        return perceive(url, PerceiveOptions.builder().build());
    }

    /**
     * Render one URL into the requested outputs (markdown, screenshots, PDF,
     * links, structured data, ...). Synchronous: returns the completed
     * operation with 15-minute signed artifact URLs.
     */
    public PerceiveResult perceive(String url, PerceiveOptions opts) {
        Map<String, Object> body = V2Serializers.perceiveOptions(opts);
        body.put("url", url);
        return V2Mappers.toPerceiveResult(post("/v2/perceive", body));
    }

    /**
     * Re-fetch a perceive operation by id (per_...). Artifact URLs are
     * freshly re-signed on every call.
     */
    public PerceiveResult getPerceiveOperation(String operationId) {
        return V2Mappers.toPerceiveResult(get("/v2/perceive/" + V2Serializers.encode(operationId)));
    }

    public PerceiveBatchResult perceiveBatch(List<String> urls) {
        return perceiveBatch(urls, PerceiveBatchOptions.builder().build());
    }

    /**
     * Perceive up to 1000 URLs with one shared options block. Small batches
     * run inline (completed result); larger ones return status "queued" —
     * poll getPerceiveBatch with the jobId.
     */
    public PerceiveBatchResult perceiveBatch(List<String> urls, PerceiveBatchOptions opts) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("urls", urls);
        body.put("options", V2Serializers.perceiveOptions(opts));
        if (opts.outputMode() != null) body.put("output_mode", opts.outputMode());
        return V2Mappers.toPerceiveBatchResult(post("/v2/perceive/batch", body));
    }

    /** Poll a perceive batch by jobId. Items fill in as URLs complete. */
    public PerceiveBatchResult getPerceiveBatch(String jobId) {
        return V2Mappers.toPerceiveBatchResult(get("/v2/perceive/batch/" + V2Serializers.encode(jobId)));
    }

    /** Artifact-producing outputs accepted by {@link #perceiveDirect} (everything except "structured"). */
    private static final List<String> ARTIFACT_OUTPUTS = List.of(
            "markdown", "html_cleaned", "html_raw", "screenshot",
            "screenshot_full_page", "pdf", "links", "images");

    public PerceiveDirectResult perceiveDirect(String url) {
        return perceiveDirect(url, PerceiveOptions.builder().build());
    }

    /**
     * Render one URL and stream the artifact bytes back directly
     * (direct_download), skipping the JSON envelope and the signed-URL round
     * trip. Requires exactly one artifact-producing output in opts.outputs;
     * metadata is returned via response headers.
     */
    public PerceiveDirectResult perceiveDirect(String url, PerceiveOptions opts) {
        List<String> outputs = opts.outputs() != null ? opts.outputs() : List.of("markdown", "structured");
        long artifactCount = outputs.stream().filter(ARTIFACT_OUTPUTS::contains).count();
        if (artifactCount != 1) {
            throw new IllegalArgumentException(
                    "perceiveDirect: requires exactly one artifact-producing output ("
                            + String.join(", ", ARTIFACT_OUTPUTS) + "); got " + artifactCount);
        }
        Map<String, Object> body = V2Serializers.perceiveOptions(opts);
        body.put("url", url);
        body.put("direct_download", true);
        byte[] payload = transport.gson.toJson(body).getBytes(StandardCharsets.UTF_8);
        HttpResponse<byte[]> resp = transport.send("/v2/perceive", "POST", payload, "application/json");
        transport.raiseForStatus(resp);
        return V2Mappers.toPerceiveDirectResult(resp);
    }

    public PerceiveDirectResult downloadPerceiveArtifact(String operationId) {
        return downloadPerceiveArtifact(operationId, null);
    }

    /**
     * Stream one stored artifact of an earlier perceive operation. output may
     * be null when the operation produced exactly one artifact (otherwise 400
     * listing the available outputs); 410 once the artifact passes the plan's
     * retention window.
     */
    public PerceiveDirectResult downloadPerceiveArtifact(String operationId, String output) {
        String path = "/v2/perceive/" + V2Serializers.encode(operationId) + "?direct_download=true";
        if (output != null) {
            path += "&output=" + V2Serializers.encode(output);
        }
        HttpResponse<byte[]> resp = transport.send(path, "GET", null, null);
        transport.raiseForStatus(resp);
        return V2Mappers.toPerceiveDirectResult(resp);
    }

    // ------------------------------------------------------------------
    // Discover — enumerate a site's URLs without rendering
    // ------------------------------------------------------------------

    public DiscoverResult discover(String url) {
        return discover(url, DiscoverOptions.builder().build());
    }

    /**
     * List a site's URLs via sitemap, HTTP crawl, or both. No browser
     * rendering — fast and does not consume perceive quota.
     */
    public DiscoverResult discover(String url, DiscoverOptions opts) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url", url);
        if (opts.mode() != null) body.put("mode", opts.mode());
        if (opts.maxUrls() != null) body.put("max_urls", opts.maxUrls());
        if (opts.maxDepth() != null) body.put("max_depth", opts.maxDepth());
        if (opts.includePatterns() != null) body.put("include_patterns", opts.includePatterns());
        if (opts.excludePatterns() != null) body.put("exclude_patterns", opts.excludePatterns());
        if (opts.sameDomainOnly() != null) body.put("same_domain_only", opts.sameDomainOnly());
        if (opts.respectRobots() != null) body.put("respect_robots", opts.respectRobots());
        return V2Mappers.toDiscoverResult(post("/v2/discover", body));
    }

    // ------------------------------------------------------------------
    // Lookup — web search with optional auto-perceive
    // ------------------------------------------------------------------

    public LookupResult lookup(String query) {
        return lookup(query, LookupOptions.builder().build());
    }

    /**
     * Run a categorized web search. With perceiveTop &gt; 0, the top-N result
     * URLs are auto-perceived (each consumes one perceive-quota unit) and
     * carry their full PerceiveResult inline.
     */
    public LookupResult lookup(String query, LookupOptions opts) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("query", query);
        if (opts.category() != null) body.put("category", opts.category());
        if (opts.country() != null) body.put("country", opts.country());
        if (opts.locale() != null) body.put("locale", opts.locale());
        if (opts.timeFilter() != null) body.put("time_filter", opts.timeFilter());
        if (opts.numResults() != null) body.put("num_results", opts.numResults());
        if (opts.page() != null) body.put("page", opts.page());
        if (opts.location() != null) body.put("location", opts.location());
        if (opts.autocorrect() != null) body.put("autocorrect", opts.autocorrect());
        if (opts.perceiveTop() != null) body.put("perceive_top", opts.perceiveTop());
        return V2Mappers.toLookupResult(post("/v2/lookup", body));
    }

    // ------------------------------------------------------------------
    // Distill — schema-driven structured extraction
    // ------------------------------------------------------------------

    /**
     * Extract structured data matching {@code schema} from explicit URLs or
     * from a discovered site. An optional cssSchema answers fields for free;
     * anything it misses escalates to the LLM tier (plan-gated).
     */
    public DistillResult distill(DistillOptions opts) {
        boolean hasUrls = opts.urls() != null && !opts.urls().isEmpty();
        boolean hasDiscover = opts.discoverFrom() != null;
        if (hasUrls == hasDiscover) {
            throw new IllegalArgumentException("distill: provide exactly one of 'urls' or 'discoverFrom'");
        }
        if (opts.schema() == null) {
            throw new IllegalArgumentException("distill: 'schema' is required and must be an object");
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("schema", opts.schema());
        if (hasUrls) body.put("urls", opts.urls());
        if (opts.discoverFrom() != null) {
            Map<String, Object> df = new LinkedHashMap<>();
            df.put("url", opts.discoverFrom().url());
            if (opts.discoverFrom().mode() != null) df.put("mode", opts.discoverFrom().mode());
            if (opts.discoverFrom().maxPages() != null) df.put("max_pages", opts.discoverFrom().maxPages());
            body.put("discover_from", df);
        }
        if (opts.cssSchema() != null) body.put("css_schema", V2Serializers.cssSchema(opts.cssSchema()));
        if (opts.waitFor() != null) body.put("wait_for", opts.waitFor());
        if (opts.waitTimeoutMs() != null) body.put("wait_timeout_ms", opts.waitTimeoutMs());
        if (opts.headers() != null) body.put("headers", opts.headers());
        if (opts.cookies() != null) {
            body.put("cookies", opts.cookies().stream().map(V1Serializers::browserCookie).collect(Collectors.toList()));
        }
        if (opts.respectRobots() != null) body.put("respect_robots", opts.respectRobots());
        return V2Mappers.toDistillResult(post("/v2/distill", body));
    }

    // ------------------------------------------------------------------
    // Ingest — site to RAG-ready JSONL chunks (always async)
    // ------------------------------------------------------------------

    /**
     * Start an ingest job: turn explicit URLs or a discovered site into
     * chunked, RAG-ready JSONL. Always asynchronous — returns the queued
     * job; poll getIngestJob or configure webhookUrl for completion.
     */
    public IngestJob ingest(IngestOptions opts) {
        String mode = opts.mode() != null ? opts.mode() : "urls";
        if ("urls".equals(mode)) {
            if (opts.urls() == null || opts.urls().isEmpty()) {
                throw new IllegalArgumentException("ingest: mode 'urls' requires a non-empty 'urls' list");
            }
            if (opts.url() != null) {
                throw new IllegalArgumentException("ingest: mode 'urls' does not accept 'url'");
            }
        } else {
            if (opts.url() == null || opts.url().isEmpty()) {
                throw new IllegalArgumentException("ingest: mode '" + mode + "' requires a seed 'url'");
            }
            if (opts.urls() != null) {
                throw new IllegalArgumentException("ingest: mode '" + mode + "' does not accept 'urls'");
            }
        }

        Map<String, Object> body = new LinkedHashMap<>();
        if (opts.mode() != null) body.put("mode", opts.mode());
        if (opts.url() != null) body.put("url", opts.url());
        if (opts.urls() != null) body.put("urls", opts.urls());
        if (opts.maxPages() != null) body.put("max_pages", opts.maxPages());
        if (opts.maxDepth() != null) body.put("max_depth", opts.maxDepth());
        if (opts.sameDomainOnly() != null) body.put("same_domain_only", opts.sameDomainOnly());
        if (opts.includePatterns() != null) body.put("include_patterns", opts.includePatterns());
        if (opts.excludePatterns() != null) body.put("exclude_patterns", opts.excludePatterns());
        if (opts.respectRobots() != null) body.put("respect_robots", opts.respectRobots());
        if (opts.waitFor() != null) body.put("wait_for", opts.waitFor());
        if (opts.waitTimeoutMs() != null) body.put("wait_timeout_ms", opts.waitTimeoutMs());
        if (opts.chunk() != null) {
            Map<String, Object> chunk = new LinkedHashMap<>();
            if (opts.chunk().maxWords() != null) chunk.put("max_words", opts.chunk().maxWords());
            if (opts.chunk().sentenceOverlap() != null) chunk.put("sentence_overlap", opts.chunk().sentenceOverlap());
            body.put("chunk", chunk);
        }
        if (opts.webhookUrl() != null) body.put("webhook_url", opts.webhookUrl());
        return V2Mappers.toIngestJob(post("/v2/ingest", body));
    }

    public IngestJob ingestFiles(List<FileInput> files) {
        return ingestFiles(files, IngestFilesOptions.builder().build());
    }

    /**
     * Ingest one or more uploaded FILES into RAG-ready JSONL chunks — the file
     * counterpart of ingest(), sharing the same job lifecycle (mode "files").
     * PDF, DOCX, PPTX, XLSX, CSV, HTML, EPUB, TXT/MD and legacy/ODF office are
     * accepted. Always asynchronous; poll getIngestJob or configure a webhook.
     */
    public IngestJob ingestFiles(List<FileInput> files, IngestFilesOptions opts) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("ingestFiles: provide at least one file");
        }
        Multipart mp = new Multipart();
        for (FileInput file : files) {
            String contentType = file.contentType() != null ? file.contentType() : Formats.mimeFor(file.filename());
            mp.addFile("files", file.filename(), contentType, file.data());
        }
        IngestChunkOptions chunk = opts.chunk();
        if (chunk != null && chunk.maxWords() != null) mp.addField("max_words", String.valueOf(chunk.maxWords()));
        if (chunk != null && chunk.sentenceOverlap() != null) {
            mp.addField("sentence_overlap", String.valueOf(chunk.sentenceOverlap()));
        }
        if (opts.webhookUrl() != null) mp.addField("webhook_url", opts.webhookUrl());
        JsonObject data = transport.requestJson("/v2/ingest/files", "POST", mp.build(), mp.contentType());
        return V2Mappers.toIngestJob(data);
    }

    public IngestJobList listIngestJobs() {
        return listIngestJobs(V2ListOptions.builder().build());
    }

    /** List ingest jobs, newest first. */
    public IngestJobList listIngestJobs(V2ListOptions opts) {
        JsonObject d = get("/v2/ingest" + V2Serializers.listQuery(opts.skip(), opts.limit()));
        List<IngestJobSummary> jobs = new ArrayList<>();
        for (JsonObject job : Json.objArr(d, "jobs")) {
            jobs.add(V2Mappers.toIngestJobSummary(job));
        }
        return new IngestJobList(jobs, Json.num(d, "skip", 0), Json.num(d, "limit", 20), Json.bool(d, "has_more"));
    }

    /** Get one ingest job by id (ing_...). */
    public IngestJob getIngestJob(String jobId) {
        return V2Mappers.toIngestJob(get("/v2/ingest/" + V2Serializers.encode(jobId)));
    }

    /**
     * Cancel a queued/processing ingest job. Idempotent: canceling an
     * already-terminal job returns it unchanged.
     */
    public IngestJob cancelIngestJob(String jobId) {
        return V2Mappers.toIngestJob(delete("/v2/ingest/" + V2Serializers.encode(jobId)));
    }

    /**
     * Re-deliver the completion webhook of a completed job (409 if the job
     * is not completed, 400 if it has no webhook configured).
     */
    public WebhookRetryResult retryIngestWebhook(String jobId) {
        JsonObject d = post("/v2/ingest/" + V2Serializers.encode(jobId) + "/retry-webhook", null);
        return new WebhookRetryResult(
                Json.str(d, "job_id", ""),
                Json.bool(d, "delivered"),
                Json.num(d, "attempts", 0),
                Json.optInt(d, "status_code"),
                Json.str(d, "detail", ""));
    }

    /**
     * Get (creating on first call) the project's webhook signing secret and
     * the header/scheme details needed to verify deliveries.
     */
    public WebhookSecret getWebhookSecret() {
        return V2Mappers.toWebhookSecret(get("/v2/ingest/webhook-secret"));
    }

    /**
     * Rotate the webhook signing secret. Signatures made with the previous
     * secret stop verifying immediately.
     */
    public WebhookSecret rotateWebhookSecret() {
        return V2Mappers.toWebhookSecret(post("/v2/ingest/webhook-secret/rotate", null));
    }

    // ------------------------------------------------------------------
    // Watch — recurring change monitoring
    // ------------------------------------------------------------------

    public Watcher createWatcher(String url) {
        return createWatcher(url, WatchCreateOptions.builder().build());
    }

    /**
     * Create a watcher that re-renders {@code url} on a fixed cadence
     * (hourly floor) and notifies on changes via email and/or webhook.
     */
    public Watcher createWatcher(String url, WatchCreateOptions opts) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url", url);
        if (opts.frequencyMinutes() != null) body.put("frequency_minutes", opts.frequencyMinutes());
        if (opts.diffMode() != null) body.put("diff_mode", opts.diffMode());
        if (opts.trackFields() != null) body.put("track_fields", opts.trackFields());
        if (opts.webhookUrl() != null) body.put("webhook_url", opts.webhookUrl());
        if (opts.notifyEmail() != null) body.put("notify_email", opts.notifyEmail());
        return V2Mappers.toWatcher(post("/v2/watch", body));
    }

    public WatcherList listWatchers() {
        return listWatchers(V2ListOptions.builder().build());
    }

    /** List watchers, newest first. */
    public WatcherList listWatchers(V2ListOptions opts) {
        JsonObject d = get("/v2/watch" + V2Serializers.listQuery(opts.skip(), opts.limit()));
        List<WatcherSummary> watchers = new ArrayList<>();
        for (JsonObject w : Json.objArr(d, "watchers")) {
            watchers.add(V2Mappers.toWatcherSummary(w));
        }
        return new WatcherList(watchers, Json.num(d, "skip", 0), Json.num(d, "limit", 20), Json.bool(d, "has_more"));
    }

    /** Get one watcher by id (wat_...). Deleted watchers read as 404. */
    public Watcher getWatcher(String watcherId) {
        return V2Mappers.toWatcher(get("/v2/watch/" + V2Serializers.encode(watcherId)));
    }

    public WatcherSnapshotList getWatcherSnapshots(String watcherId) {
        return getWatcherSnapshots(watcherId, SnapshotListOptions.builder().build());
    }

    /** Page through a watcher's check history, newest first. */
    public WatcherSnapshotList getWatcherSnapshots(String watcherId, SnapshotListOptions opts) {
        String query = opts.limit() != null ? "?limit=" + opts.limit() : "";
        JsonObject d = get("/v2/watch/" + V2Serializers.encode(watcherId) + "/snapshots" + query);
        List<WatcherSnapshot> snapshots = new ArrayList<>();
        for (JsonObject s : Json.objArr(d, "snapshots")) {
            snapshots.add(V2Mappers.toWatcherSnapshot(s));
        }
        return new WatcherSnapshotList(Json.str(d, "watcher_id", ""), snapshots, Json.num(d, "limit", 20));
    }

    /**
     * Update a watcher. At least one field is required. Set webhookUrl to
     * "" to clear the webhook; resuming a paused watcher re-checks the
     * plan's watcher cap.
     */
    public Watcher updateWatcher(String watcherId, WatcherUpdate updates) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (updates.frequencyMinutes() != null) body.put("frequency_minutes", updates.frequencyMinutes());
        if (updates.diffMode() != null) body.put("diff_mode", updates.diffMode());
        if (updates.trackFields() != null) body.put("track_fields", updates.trackFields());
        if (updates.webhookUrl() != null) body.put("webhook_url", updates.webhookUrl());
        if (updates.notifyEmail() != null) body.put("notify_email", updates.notifyEmail());
        if (updates.status() != null) body.put("status", updates.status());
        if (body.isEmpty()) {
            throw new IllegalArgumentException("updateWatcher: provide at least one field to update");
        }
        return V2Mappers.toWatcher(patch("/v2/watch/" + V2Serializers.encode(watcherId), body));
    }

    /**
     * Soft-delete a watcher (idempotent). Returns the tombstoned watcher
     * with status "deleted".
     */
    public Watcher deleteWatcher(String watcherId) {
        return V2Mappers.toWatcher(delete("/v2/watch/" + V2Serializers.encode(watcherId)));
    }

    // ------------------------------------------------------------------
    // HTTP helpers
    // ------------------------------------------------------------------

    private JsonObject post(String path, Map<String, Object> body) {
        byte[] payload = body != null ? transport.gson.toJson(body).getBytes(StandardCharsets.UTF_8) : null;
        return transport.requestJson(path, "POST", payload, payload != null ? "application/json" : null);
    }

    private JsonObject patch(String path, Map<String, Object> body) {
        byte[] payload = transport.gson.toJson(body).getBytes(StandardCharsets.UTF_8);
        return transport.requestJson(path, "PATCH", payload, "application/json");
    }

    private JsonObject get(String path) {
        return transport.requestJson(path, "GET", null, null);
    }

    private JsonObject delete(String path) {
        return transport.requestJson(path, "DELETE", null, null);
    }
}
