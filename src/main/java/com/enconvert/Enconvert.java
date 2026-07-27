package com.enconvert;

import com.enconvert.exceptions.ApiException;
import com.enconvert.exceptions.EnconvertException;
import com.enconvert.model.BatchStatus;
import com.enconvert.model.BatchSubmission;
import com.enconvert.model.ConversionResult;
import com.enconvert.model.ConvertDocumentOptions;
import com.enconvert.model.ConvertImageOptions;
import com.enconvert.model.ConvertToMarkdownOptions;
import com.enconvert.model.ConvertToPdfOptions;
import com.enconvert.model.JobStatus;
import com.enconvert.model.PdfOptions;
import com.enconvert.model.UrlToMarkdownOptions;
import com.enconvert.model.UrlToPdfOptions;
import com.enconvert.model.UrlToScreenshotOptions;
import com.enconvert.model.WaitForBatchOptions;
import com.enconvert.model.WebsiteToPdfOptions;
import com.enconvert.model.WebsiteToScreenshotOptions;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * Enconvert file conversion client.
 *
 * <pre>{@code
 * Enconvert client = new Enconvert("sk_...");
 * ConversionResult result = client.convertUrlToPdf("https://example.com");
 * System.out.println(result.presignedUrl());
 * }</pre>
 */
public final class Enconvert {

    static final String DEFAULT_BASE_URL = "https://api.enconvert.com";
    static final long DEFAULT_TIMEOUT_MS = 300_000;
    private static final long DEFAULT_BATCH_POLL_INTERVAL_MS = 5_000;
    private static final long DEFAULT_BATCH_TIMEOUT_MS = 1_800_000;
    private static final long POLL_JOB_MAX_WAIT_MS = 300_000;
    private static final long POLL_JOB_INTERVAL_MS = 3_000;

    private final Transport transport;

    /**
     * V2 API namespace: perceive, discover, lookup, distill, ingest, watch.
     * Requires a private API key; endpoints are plan-gated (QuotaException on 402).
     */
    public final EnconvertV2 v2;

    public Enconvert(String apiKey) {
        this(apiKey, DEFAULT_BASE_URL, Duration.ofMillis(DEFAULT_TIMEOUT_MS));
    }

    public Enconvert(String apiKey, String baseUrl) {
        this(apiKey, baseUrl, Duration.ofMillis(DEFAULT_TIMEOUT_MS));
    }

    public Enconvert(String apiKey, String baseUrl, Duration timeout) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("Enconvert: 'apiKey' is required");
        }
        String normalizedBaseUrl = (baseUrl == null ? DEFAULT_BASE_URL : baseUrl).replaceAll("/+$", "");
        Duration effectiveTimeout = timeout != null ? timeout : Duration.ofMillis(DEFAULT_TIMEOUT_MS);
        this.transport = new Transport(apiKey, normalizedBaseUrl, effectiveTimeout);
        this.v2 = new EnconvertV2(transport);
    }

    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }

    /** Builder for {@link Enconvert} with optional {@code baseUrl} / {@code timeout} overrides. */
    public static final class Builder {
        private final String apiKey;
        private String baseUrl = DEFAULT_BASE_URL;
        private Duration timeout = Duration.ofMillis(DEFAULT_TIMEOUT_MS);

        private Builder(String apiKey) {
            this.apiKey = apiKey;
        }

        /** Override the API base URL. Defaults to https://api.enconvert.com */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /** Request timeout. Defaults to 300 seconds. */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Enconvert build() {
            return new Enconvert(apiKey, baseUrl, timeout);
        }
    }

    // ------------------------------------------------------------------
    // URL conversions (single page)
    // ------------------------------------------------------------------

    public ConversionResult convertUrlToPdf(String url) {
        return convertUrlToPdf(url, UrlToPdfOptions.builder().build());
    }

    /** Convert a URL to PDF. */
    public ConversionResult convertUrlToPdf(String url, UrlToPdfOptions opts) {
        Map<String, Object> body = V1Serializers.urlBody(
                url, opts.viewportWidth(), opts.viewportHeight(), opts.loadMedia(), opts.enableScroll(),
                opts.outputFilename(), opts.auth(), opts.cookies(), opts.headers());
        body.put("single_page", opts.singlePage() != null ? opts.singlePage() : true);
        if (opts.pdfOptions() != null) {
            body.put("pdf_options", V1Serializers.pdfOptions(opts.pdfOptions()));
        }

        JsonObject data = postJson("/v1/convert/url-to-pdf", body, true);
        ConversionResult result = V1Mappers.toConversionResult(data);
        if (opts.saveTo() != null) download(result.presignedUrl(), Path.of(opts.saveTo()));
        return result;
    }

    public ConversionResult convertUrlToScreenshot(String url) {
        return convertUrlToScreenshot(url, UrlToScreenshotOptions.builder().build());
    }

    /** Convert a URL to a PNG screenshot. */
    public ConversionResult convertUrlToScreenshot(String url, UrlToScreenshotOptions opts) {
        Map<String, Object> body = V1Serializers.urlBody(
                url, opts.viewportWidth(), opts.viewportHeight(), opts.loadMedia(), opts.enableScroll(),
                opts.outputFilename(), opts.auth(), opts.cookies(), opts.headers());

        JsonObject data = postJson("/v1/convert/url-to-screenshot", body, true);
        ConversionResult result = V1Mappers.toConversionResult(data);
        if (opts.saveTo() != null) download(result.presignedUrl(), Path.of(opts.saveTo()));
        return result;
    }

    public ConversionResult convertUrlToMarkdown(String url) {
        return convertUrlToMarkdown(url, UrlToMarkdownOptions.builder().build());
    }

    /**
     * Convert a URL to clean GitHub-Flavored Markdown with YAML frontmatter
     * (title, description, url, links, images). Strips nav/footer/ads/scripts
     * and extracts the main article content.
     */
    public ConversionResult convertUrlToMarkdown(String url, UrlToMarkdownOptions opts) {
        Map<String, Object> body = V1Serializers.urlBody(
                url, opts.viewportWidth(), opts.viewportHeight(), opts.loadMedia(), opts.enableScroll(),
                opts.outputFilename(), opts.auth(), opts.cookies(), opts.headers());

        JsonObject data = postJson("/v1/convert/url-to-markdown", body, true);
        ConversionResult result = V1Mappers.toConversionResult(data);
        if (opts.saveTo() != null) download(result.presignedUrl(), Path.of(opts.saveTo()));
        return result;
    }

    // ------------------------------------------------------------------
    // Website conversions (async batch, whole-site crawl)
    // ------------------------------------------------------------------

    public BatchSubmission convertWebsiteToPdf(String url) {
        return convertWebsiteToPdf(url, WebsiteToPdfOptions.builder().build());
    }

    /**
     * Convert every discovered page of a website to PDF. Async-only: pages are
     * discovered via sitemap or full crawl (plan-dependent), converted in the
     * background, and bundled into a single ZIP. Poll with getBatchStatus or
     * block with waitForBatch. Requires a private API key with crawl access.
     */
    public BatchSubmission convertWebsiteToPdf(String url, WebsiteToPdfOptions opts) {
        Map<String, Object> body = V1Serializers.websiteBody(
                url, opts.crawlMode(), opts.includePatterns(), opts.excludePatterns(), opts.notificationEmail(),
                opts.callbackUrl(), opts.outputFilename(), opts.viewportWidth(), opts.viewportHeight(),
                opts.loadMedia(), opts.enableScroll(), opts.auth(), opts.cookies(), opts.headers());
        if (opts.singlePage() != null) body.put("single_page", opts.singlePage());
        if (opts.pdfOptions() != null) body.put("pdf_options", V1Serializers.pdfOptions(opts.pdfOptions()));

        // No job-polling fallback: website submissions have no per-job row, so a
        // 5xx here means the submission itself failed and must surface directly.
        JsonObject data = postJson("/v1/convert/website-to-pdf", body, false);
        return V1Mappers.toBatchSubmission(data);
    }

    public BatchSubmission convertWebsiteToScreenshot(String url) {
        return convertWebsiteToScreenshot(url, WebsiteToScreenshotOptions.builder().build());
    }

    /**
     * Screenshot every discovered page of a website (PNG). Async-only, bundled
     * into a single ZIP. Poll with getBatchStatus or block with waitForBatch.
     * Requires a private API key with crawl access.
     */
    public BatchSubmission convertWebsiteToScreenshot(String url, WebsiteToScreenshotOptions opts) {
        Map<String, Object> body = V1Serializers.websiteBody(
                url, opts.crawlMode(), opts.includePatterns(), opts.excludePatterns(), opts.notificationEmail(),
                opts.callbackUrl(), opts.outputFilename(), opts.viewportWidth(), opts.viewportHeight(),
                opts.loadMedia(), opts.enableScroll(), opts.auth(), opts.cookies(), opts.headers());

        JsonObject data = postJson("/v1/convert/website-to-screenshot", body, false);
        return V1Mappers.toBatchSubmission(data);
    }

    // ------------------------------------------------------------------
    // File conversions
    // ------------------------------------------------------------------

    /**
     * Convert an image between formats (jpeg, png, svg, heic, webp), or
     * rasterize a PDF to JPEG. Only pairs implemented by the API are accepted;
     * unsupported pairs throw before any request is made.
     */
    public ConversionResult convertImage(Path file, ConvertImageOptions opts) {
        return convertImage(toFilePart(file), opts);
    }

    public ConversionResult convertImage(byte[] data, ConvertImageOptions opts) {
        return convertImage(toFilePart(data), opts);
    }

    public ConversionResult convertImage(FileInput file, ConvertImageOptions opts) {
        return convertImage(toFilePart(file), opts);
    }

    private ConversionResult convertImage(FilePart part, ConvertImageOptions opts) {
        String inputFormat = Formats.resolveInputFormat(part.filename(), Formats.IMAGE_FORMATS);
        String outputFormat = Formats.normalizeOutputFormat(opts.outputFormat());
        String endpoint = "/v1/convert/" + Formats.assertConversionImplemented(inputFormat, outputFormat);
        JsonObject data = postFile(endpoint, part, opts.outputFilename(), null);
        ConversionResult result = V1Mappers.toConversionResult(data);
        if (opts.saveTo() != null) download(result.presignedUrl(), Path.of(opts.saveTo()));
        return result;
    }

    public ConversionResult convertDocument(Path file) {
        return convertDocument(file, ConvertDocumentOptions.builder().build());
    }

    public ConversionResult convertDocument(byte[] data) {
        return convertDocument(data, ConvertDocumentOptions.builder().build());
    }

    public ConversionResult convertDocument(FileInput file) {
        return convertDocument(file, ConvertDocumentOptions.builder().build());
    }

    /**
     * Convert a document (doc, excel, ppt, odt, ods, odp, ots, pages, numbers,
     * epub, html, markdown, csv, json, xml, yaml, toml). Output defaults to
     * pdf. Only pairs implemented by the API are accepted; unsupported pairs
     * throw before any request is made.
     */
    public ConversionResult convertDocument(Path file, ConvertDocumentOptions opts) {
        return convertDocument(toFilePart(file), opts);
    }

    public ConversionResult convertDocument(byte[] data, ConvertDocumentOptions opts) {
        return convertDocument(toFilePart(data), opts);
    }

    public ConversionResult convertDocument(FileInput file, ConvertDocumentOptions opts) {
        return convertDocument(toFilePart(file), opts);
    }

    private ConversionResult convertDocument(FilePart part, ConvertDocumentOptions opts) {
        String inputFormat = Formats.resolveInputFormat(part.filename(), Formats.DOCUMENT_FORMATS);
        String outputFormat = Formats.normalizeOutputFormat(
                opts.outputFormat() != null ? opts.outputFormat() : "pdf");
        String endpoint = "/v1/convert/" + Formats.assertConversionImplemented(inputFormat, outputFormat);
        JsonObject data = postFile(endpoint, part, opts.outputFilename(), opts.pdfOptions());
        ConversionResult result = V1Mappers.toConversionResult(data);
        if (opts.saveTo() != null) download(result.presignedUrl(), Path.of(opts.saveTo()));
        return result;
    }

    public ConversionResult convertToMarkdown(Path file) {
        return convertToMarkdown(file, ConvertToMarkdownOptions.builder().build());
    }

    public ConversionResult convertToMarkdown(byte[] data) {
        return convertToMarkdown(data, ConvertToMarkdownOptions.builder().build());
    }

    public ConversionResult convertToMarkdown(FileInput file) {
        return convertToMarkdown(file, ConvertToMarkdownOptions.builder().build());
    }

    /**
     * Convert an uploaded file of (almost) any document format to clean Markdown
     * — PDF, DOCX, PPTX, XLSX, CSV, HTML, EPUB, TXT/MD, and legacy/ODF office.
     * The format is auto-detected server-side; a RAG-ingestion building block.
     * No client-side format resolution — any file is accepted. Images are not
     * supported (the API rejects them with 400).
     */
    public ConversionResult convertToMarkdown(Path file, ConvertToMarkdownOptions opts) {
        return convertToMarkdown(toFilePart(file), opts);
    }

    public ConversionResult convertToMarkdown(byte[] data, ConvertToMarkdownOptions opts) {
        return convertToMarkdown(toFilePart(data), opts);
    }

    public ConversionResult convertToMarkdown(FileInput file, ConvertToMarkdownOptions opts) {
        return convertToMarkdown(toFilePart(file), opts);
    }

    private ConversionResult convertToMarkdown(FilePart part, ConvertToMarkdownOptions opts) {
        JsonObject data = postFile("/v1/convert/anything-to-markdown", part, opts.outputFilename(), null);
        ConversionResult result = V1Mappers.toConversionResult(data);
        if (opts.saveTo() != null) download(result.presignedUrl(), Path.of(opts.saveTo()));
        return result;
    }

    public ConversionResult convertToPdf(Path file) {
        return convertToPdf(file, ConvertToPdfOptions.builder().build());
    }

    public ConversionResult convertToPdf(byte[] data) {
        return convertToPdf(data, ConvertToPdfOptions.builder().build());
    }

    public ConversionResult convertToPdf(FileInput file) {
        return convertToPdf(file, ConvertToPdfOptions.builder().build());
    }

    /**
     * Convert an uploaded file of (almost) any format to PDF — office/ODF/Pages/
     * Numbers/RTF/CSV, HTML, Markdown, text, raster images, SVG, EPUB, or an
     * existing PDF (passthrough/normalise). The format is auto-detected
     * server-side; no client-side format resolution. Only
     * {@code pdfOptions.grayscale} is honored on this endpoint.
     */
    public ConversionResult convertToPdf(Path file, ConvertToPdfOptions opts) {
        return convertToPdf(toFilePart(file), opts);
    }

    public ConversionResult convertToPdf(byte[] data, ConvertToPdfOptions opts) {
        return convertToPdf(toFilePart(data), opts);
    }

    public ConversionResult convertToPdf(FileInput file, ConvertToPdfOptions opts) {
        return convertToPdf(toFilePart(file), opts);
    }

    private ConversionResult convertToPdf(FilePart part, ConvertToPdfOptions opts) {
        JsonObject data = postFile("/v1/convert/anything-to-pdf", part, opts.outputFilename(), opts.pdfOptions());
        ConversionResult result = V1Mappers.toConversionResult(data);
        if (opts.saveTo() != null) download(result.presignedUrl(), Path.of(opts.saveTo()));
        return result;
    }

    // ------------------------------------------------------------------
    // Job + batch status
    // ------------------------------------------------------------------

    /** Poll the status of an async conversion job. */
    public JobStatus getJobStatus(String jobId) {
        JsonObject data = transport.requestJson("/v1/convert/status/" + jobId, "GET", null, null);
        return V1Mappers.toJobStatus(data);
    }

    /**
     * Get the status of an async batch (website conversion). Returns aggregate
     * counts, per-URL statuses, and download URLs. Private API keys only.
     */
    public BatchStatus getBatchStatus(String batchId) {
        JsonObject data = transport.requestJson("/v1/convert/batch/" + batchId, "GET", null, null);
        return V1Mappers.toBatchStatus(data);
    }

    public BatchStatus waitForBatch(String batchId) {
        return waitForBatch(batchId, WaitForBatchOptions.builder().build());
    }

    /**
     * Poll a batch until it leaves "processing", then return its final status.
     * With {@code saveTo}, downloads the batch ZIP once available. Throws
     * ApiException(504) on timeout.
     */
    public BatchStatus waitForBatch(String batchId, WaitForBatchOptions opts) {
        long intervalMs = opts.intervalMs() != null ? opts.intervalMs() : DEFAULT_BATCH_POLL_INTERVAL_MS;
        long timeoutMs = opts.timeoutMs() != null ? opts.timeoutMs() : DEFAULT_BATCH_TIMEOUT_MS;
        long deadline = System.currentTimeMillis() + timeoutMs;

        while (true) {
            BatchStatus status = getBatchStatus(batchId);
            if (!"processing".equals(status.status())) {
                if (opts.saveTo() != null) {
                    if (status.zipDownloadUrl() == null) {
                        throw new ApiException(500, "Batch " + batchId + " finished with status '"
                                + status.status() + "' but no ZIP is available to save");
                    }
                    download(status.zipDownloadUrl(), Path.of(opts.saveTo()));
                }
                return status;
            }
            if (System.currentTimeMillis() >= deadline) {
                throw new ApiException(504, "Batch " + batchId + " did not complete within " + timeoutMs + "ms");
            }
            sleep(intervalMs);
        }
    }

    // ------------------------------------------------------------------
    // Internal helpers (mirror of node-sdk's _post_json / _post_file /
    // _poll_job / _download / _resolve_format / _raise_for_status)
    // ------------------------------------------------------------------

    private JsonObject postJson(String endpoint, Map<String, Object> body, boolean jobFallback) {
        String jobId = jobFallback ? newJobId() : null;
        if (jobId != null) body.put("job_id", jobId);
        byte[] payload = transport.gson.toJson(body).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        try {
            JsonObject data = transport.requestJson(endpoint, "POST", payload, "application/json");
            // Some success responses omit job_id (URL sync path); backfill the
            // client-generated id so callers can still poll getJobStatus with it.
            if (jobId != null && (!data.has("job_id") || data.get("job_id").isJsonNull())) {
                data.addProperty("job_id", jobId);
            }
            return data;
        } catch (ApiException e) {
            if (jobId != null && e.getStatusCode() >= 500) {
                return pollJob(jobId);
            }
            throw e;
        }
    }

    private JsonObject postFile(String endpoint, FilePart part, String outputFilename, PdfOptions pdfOptions) {
        String jobId = newJobId();
        Multipart mp = new Multipart();
        mp.addFile("file", part.filename(), part.contentType(), part.bytes());
        mp.addField("direct_download", "false");
        mp.addField("job_id", jobId);
        if (outputFilename != null) mp.addField("output_filename", outputFilename);
        if (pdfOptions != null) {
            mp.addField("pdf_options", transport.gson.toJson(V1Serializers.pdfOptions(pdfOptions)));
        }
        byte[] payload = mp.build();
        try {
            JsonObject data = transport.requestJson(endpoint, "POST", payload, mp.contentType());
            if (!data.has("job_id") || data.get("job_id").isJsonNull()) {
                data.addProperty("job_id", jobId);
            }
            return data;
        } catch (ApiException e) {
            if (e.getStatusCode() >= 500) {
                return pollJob(jobId);
            }
            throw e;
        }
    }

    /** Poll job status until success/failure. Used as fallback when the HTTP request fails. */
    private JsonObject pollJob(String jobId) {
        long deadline = System.currentTimeMillis() + POLL_JOB_MAX_WAIT_MS;
        while (System.currentTimeMillis() < deadline) {
            sleep(POLL_JOB_INTERVAL_MS);
            HttpResponse<byte[]> resp = transport.send("/v1/convert/status/" + jobId, "GET", null, null);
            if (transport.statusCode(resp) == 404) continue;
            transport.raiseForStatus(resp);
            JsonObject data = transport.parseObject(resp.body());
            String status = Json.optStr(data, "status");
            if ("success".equals(status)) return data;
            if ("failed".equals(status)) {
                String error = Json.optStr(data, "error");
                throw new ApiException(500, error != null ? error : "Conversion failed");
            }
        }
        throw new ApiException(504, "Conversion timed out");
    }

    /** Save a presigned URL to a local file. No API key is sent — it's a signed S3 URL. */
    private void download(String url, Path dest) {
        HttpResponse<InputStream> resp = transport.getUnauthenticated(url);
        int status = resp.statusCode();
        if (status < 200 || status >= 300) {
            throw new ApiException(status, "Failed to download: HTTP " + status);
        }
        try {
            Path parent = dest.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            try (InputStream in = resp.body()) {
                Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save download to " + dest, e);
        }
    }

    private FilePart toFilePart(Path file) {
        try {
            byte[] bytes = Files.readAllBytes(file);
            String filename = file.getFileName().toString();
            return new FilePart(bytes, filename, Formats.mimeFor(filename));
        } catch (IOException e) {
            throw new EnconvertException("Failed to read file: " + file, e);
        }
    }

    private FilePart toFilePart(byte[] data) {
        return new FilePart(data, "upload.bin", "application/octet-stream");
    }

    private FilePart toFilePart(FileInput file) {
        String contentType = file.contentType() != null ? file.contentType() : Formats.mimeFor(file.filename());
        return new FilePart(file.data(), file.filename(), contentType);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EnconvertException("Interrupted while waiting", e);
        }
    }

    static String newJobId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /** Normalized file part ready for multipart upload. */
    private record FilePart(byte[] bytes, String filename, String contentType) {
    }
}
