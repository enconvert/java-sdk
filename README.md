# Enconvert Java SDK

Honest eyes for your AI agent — the Java SDK for [Enconvert](https://enconvert.com). Java 17+.

Read any web page or file into clean Markdown, JSON, or screenshots, and get a `renderQuality` score (0.0–1.0) on **every** read — so a blocked, challenge, or empty-SPA page comes back flagged with a low score and warnings, never mistaken for real content. Perceive, discover, look up, distill, ingest, and watch the web; convert 43+ file and document formats through the same key.

> Wiring an agent (Claude, Cursor, Windsurf, n8n, …)? The [MCP server](https://enconvert.com/mcp) is the native path — `npx @enconvert/mcp setup`. This SDK is the programmatic REST path for everything else.

## Install

### Gradle

```groovy
dependencies {
    implementation 'com.enconvert:enconvert-sdk:0.1.1'
}
```

### Maven

```xml
<dependency>
    <groupId>com.enconvert</groupId>
    <artifactId>enconvert-sdk</artifactId>
    <version>0.1.1</version>
</dependency>
```

The SDK has a single third-party dependency: [Gson](https://github.com/google/gson) (JSON). HTTP is handled by the JDK's built-in `java.net.http.HttpClient` — no other runtime dependencies.

## Quick Start

```java
import com.enconvert.Enconvert;
import com.enconvert.model.v2.PerceiveOptions;
import com.enconvert.model.v2.PerceiveResult;

import java.util.List;

Enconvert client = new Enconvert("sk_...");

// Read a page the way your agent should — with a quality score attached.
PerceiveResult op = client.v2.perceive("https://example.com",
        PerceiveOptions.builder()
                .outputs(List.of("markdown", "structured"))
                .build());
System.out.println(op.outputs().get("markdown").url() + " " + op.renderQuality()); // e.g. 0.93
```

---

# V2 — agent-ready data (`client.v2`)

The V2 namespace turns web pages into agent-ready data: render, search, extract, ingest, and monitor. All V2 endpoints require a **private API key** and are plan-gated — a disabled feature or exhausted monthly quota throws `QuotaException` (HTTP 402).

Every render carries `renderQuality` (0.0–1.0). A low score means the page didn't render cleanly (challenge page, cookie wall, empty shell); the content is still returned, flagged, so a bad read never quietly enters your agent's context.

### Perceive — render a URL into artifacts

```java
import com.enconvert.model.v2.PerceiveBatchResult;
import com.enconvert.model.v2.PerceiveOptions;
import com.enconvert.model.v2.PerceiveResult;

PerceiveResult op = client.v2.perceive("https://example.com",
        PerceiveOptions.builder()
                .outputs(List.of("markdown", "screenshot", "structured"))
                .extract(List.of("tables", "metadata"))
                .build());
System.out.println(op.renderQuality());                  // honesty score, 0.0-1.0
System.out.println(op.outputs().get("markdown").url());  // 15-min signed URL
System.out.println(op.structured());

// Re-sign artifact URLs later:
PerceiveResult again = client.v2.getPerceiveOperation(op.operationId());

// Batch (<=1000 URLs; small batches run inline, larger return "queued" — poll):
PerceiveBatchResult batch = client.v2.perceiveBatch(List.of("https://a.com", "https://b.com"),
        com.enconvert.model.v2.PerceiveBatchOptions.builder()
                .outputs(List.of("markdown"))
                .outputMode("zip")
                .build());
PerceiveBatchResult done = client.v2.getPerceiveBatch(batch.jobId());

// Direct download — stream the artifact bytes, no signed-URL round trip.
// Requires exactly one artifact-producing output:
com.enconvert.model.v2.PerceiveDirectResult direct = client.v2.perceiveDirect("https://example.com",
        PerceiveOptions.builder()
                .outputs(List.of("pdf"))
                .build());
java.nio.file.Files.write(java.nio.file.Path.of(direct.filename()), direct.content());

// Re-download a stored artifact of an earlier operation (410 past retention):
com.enconvert.model.v2.PerceiveDirectResult bytes =
        client.v2.downloadPerceiveArtifact(op.operationId(), "markdown");
```

### Discover — enumerate a site's URLs (no rendering)

```java
import com.enconvert.model.v2.DiscoverOptions;
import com.enconvert.model.v2.DiscoverResult;

DiscoverResult found = client.v2.discover("https://example.com",
        DiscoverOptions.builder()
                .mode("hybrid")              // "sitemap" | "crawl" | "hybrid"
                .maxUrls(200)
                .excludePatterns(List.of("/tag/"))
                .build());
System.out.println(found.total() + " " + found.urls());
```

### Lookup — web search with optional auto-perceive

```java
import com.enconvert.model.v2.LookupOptions;
import com.enconvert.model.v2.LookupResult;

LookupResult search = client.v2.lookup("best static site generators",
        LookupOptions.builder()
                .category("web")             // web | news | images | scholar | patents | maps
                .numResults(10)
                .perceiveTop(3)               // auto-render top 3 results (uses perceive quota)
                .build());
for (var hit : search.results()) {
    System.out.println(hit.title() + " " + hit.url() + " " + hit.perceive());
}
```

### Distill — schema-driven structured extraction

```java
import com.enconvert.model.v2.CssField;
import com.enconvert.model.v2.CssSchema;
import com.enconvert.model.v2.DistillOptions;
import com.enconvert.model.v2.DistillResult;

import java.util.Map;

DistillResult extraction = client.v2.distill(DistillOptions.builder(Map.of("plans", "list of plan names with monthly prices"))
        .urls(List.of("https://example.com/pricing"))
        .cssSchema(CssSchema.builder(".plan-card", List.of(         // optional free CSS pass before the LLM tier
                        CssField.builder("name", "text").selector("h3").build(),
                        CssField.builder("price", "text").selector(".price").build()))
                .build())
        .build());
System.out.println(extraction.results().get(0).data() + " " + extraction.results().get(0).extractionTier());

// Or discover-then-distill:
client.v2.distill(DistillOptions.builder(Map.of("title", "page title", "summary", "one-line summary"))
        .discoverFrom(new com.enconvert.model.v2.DistillDiscoverFrom("https://example.com", "sitemap", 10))
        .build());
```

### Ingest — site or files to RAG-ready JSONL (always async)

Turn a whole site — or a set of uploaded documents — into chunked, RAG-ready JSONL through one pipeline.

```java
import com.enconvert.FileInput;
import com.enconvert.model.v2.IngestChunkOptions;
import com.enconvert.model.v2.IngestFilesOptions;
import com.enconvert.model.v2.IngestJob;
import com.enconvert.model.v2.IngestOptions;
import com.enconvert.model.v2.WebhookSecret;

import java.nio.file.Files;
import java.nio.file.Path;

// From a site:
IngestJob job = client.v2.ingest(IngestOptions.builder()
        .mode("sitemap")
        .url("https://docs.example.com")
        .maxPages(100)
        .chunk(new IngestChunkOptions(512, 1))
        .webhookUrl("https://my.app/hooks/enconvert")
        .build());

// Or from uploaded files (PDF, DOCX, PPTX, XLSX, CSV, HTML, EPUB, TXT/MD, legacy/ODF office):
IngestJob fileJob = client.v2.ingestFiles(
        List.of(new FileInput(Files.readAllBytes(Path.of("handbook.pdf")), "handbook.pdf"),
                new FileInput(Files.readAllBytes(Path.of("notes.docx")), "notes.docx")),
        IngestFilesOptions.builder()
                .chunk(new IngestChunkOptions(512, 1))
                .build());

IngestJob status = client.v2.getIngestJob(job.jobId());              // poll
if ("completed".equals(status.status())) System.out.println(status.outputUrl()); // JSONL

client.v2.listIngestJobs(com.enconvert.model.v2.V2ListOptions.builder().limit(20).build());
client.v2.cancelIngestJob(job.jobId());                               // idempotent

// Webhook signing (HMAC):
WebhookSecret secret = client.v2.getWebhookSecret();
client.v2.rotateWebhookSecret();                                      // invalidates old secret
client.v2.retryIngestWebhook(job.jobId());                            // re-deliver
```

### Watch — recurring change monitoring

```java
import com.enconvert.model.v2.WatchCreateOptions;
import com.enconvert.model.v2.Watcher;
import com.enconvert.model.v2.WatcherUpdate;

Watcher watcher = client.v2.createWatcher("https://example.com/pricing", WatchCreateOptions.builder()
        .frequencyMinutes(60)         // hourly floor
        .diffMode("auto")             // auto | text | structured | tables | metadata
        .webhookUrl("https://my.app/hooks/changes")
        .notifyEmail(true)
        .build());

client.v2.listWatchers();
client.v2.getWatcher(watcher.watcherId());
client.v2.getWatcherSnapshots(watcher.watcherId(), com.enconvert.model.v2.SnapshotListOptions.builder().limit(10).build());
client.v2.updateWatcher(watcher.watcherId(), WatcherUpdate.builder().status("paused").build());
client.v2.updateWatcher(watcher.watcherId(), WatcherUpdate.builder().webhookUrl("").build()); // clears webhook
client.v2.deleteWatcher(watcher.watcherId());              // soft-delete, idempotent
```

### V2 error handling

```java
import com.enconvert.exceptions.QuotaException;

try {
    client.v2.ingest(IngestOptions.builder().urls(List.of("https://example.com")).build());
} catch (QuotaException e) {
    System.err.println("Upgrade plan or wait for quota reset");
}
```

---

# File conversion

The same key also converts 43+ formats. Two "anything → X" endpoints auto-detect the input; the format-specific endpoints below give you a validated, typed path.

### Anything to Markdown / PDF

```java
import com.enconvert.model.ConvertToMarkdownOptions;
import com.enconvert.model.ConvertToPdfOptions;
import com.enconvert.model.PdfOptions;

import java.nio.file.Path;

// Any document → clean Markdown (a RAG-ingestion building block):
client.convertToMarkdown(Path.of("report.docx"), ConvertToMarkdownOptions.builder().saveTo("report.md").build());
// PDF, DOCX, PPTX, XLSX, CSV, HTML, EPUB, TXT/MD, and legacy/ODF office. (Images not supported.)

// Almost anything → PDF:
client.convertToPdf(Path.of("slides.pptx"), ConvertToPdfOptions.builder().saveTo("slides.pdf").build());
// office/ODF/Pages/Numbers/RTF/CSV, HTML, Markdown, text, images, SVG, EPUB, or a PDF passthrough.
// Only pdfOptions.grayscale is honored on this endpoint:
client.convertToPdf(Path.of("scan.pdf"), ConvertToPdfOptions.builder()
        .pdfOptions(PdfOptions.builder().grayscale(true).build())
        .saveTo("gray.pdf")
        .build());
```

`convertToMarkdown` / `convertToPdf` accept a `java.nio.file.Path`, a raw `byte[]`, or a `com.enconvert.FileInput` — same overloads as `convertImage` / `convertDocument`. Unlike those two, there is no client-side format check: any file is uploaded as-is and the server auto-detects the format.

### Image conversion

```java
import com.enconvert.model.ConvertImageOptions;

ConversionResult result = client.convertImage(Path.of("photo.heic"),
        ConvertImageOptions.builder("webp").saveTo("photo.webp").build());
```

Any pair among `jpeg`, `png`, `svg`, `heic`, `webp` — plus PDF rasterization:

```java
client.convertImage(Path.of("scan.pdf"), ConvertImageOptions.builder("jpeg").saveTo("scan.jpeg").build());
```

`convertImage` / `convertDocument` accept a `java.nio.file.Path` (read from disk), a raw `byte[]` (filename defaults to `upload.bin`), or a `com.enconvert.FileInput` when you need to pair in-memory bytes with an explicit filename:

```java
import com.enconvert.FileInput;

client.convertImage(new FileInput(bytes, "photo.heic"),
        ConvertImageOptions.builder("webp").build());
```

### Document Conversion

```java
import com.enconvert.model.ConvertDocumentOptions;

client.convertDocument(Path.of("report.docx"), ConvertDocumentOptions.builder().saveTo("report.pdf").build());
client.convertDocument(Path.of("data.json"), ConvertDocumentOptions.builder().outputFormat("yaml").saveTo("data.yaml").build());
client.convertDocument(Path.of("notes.md"), ConvertDocumentOptions.builder().outputFormat("html").saveTo("notes.html").build());
```

Supported inputs: `doc`/`docx`, `xls`/`xlsx`, `ppt`/`pptx`, `odt`, `ods`, `odp`, `ots`, `pages`, `numbers`, `html`, `markdown`, `csv`, `json`, `xml`, `yaml`, `toml`. (EPUB has no dedicated document pair — use `convertToPdf` / `convertToMarkdown`.)

The SDK validates every `{input}-to-{output}` pair against the conversions the API actually implements and throws immediately — with the list of valid outputs for that input — instead of sending a doomed request. Introspect programmatically:

```java
import com.enconvert.Formats;

Formats.validOutputsFor("json");  // ["csv", "toml", "xml", "yaml"]
Formats.validOutputsFor("pdf");   // ["jpeg"]
```

### Supported conversions

| Input | Outputs |
|-------|---------|
| json | csv, toml, xml, yaml |
| xml | csv, json |
| yaml | json |
| csv | json, xml |
| toml | json |
| markdown | html, pdf |
| html | pdf |
| doc, excel, ppt, odt, ods, odp, ots, pages, numbers | pdf |
| jpeg, png, svg, heic, webp | each other (all 20 pairs) |
| pdf | jpeg |

### URL to PDF / Screenshot / Markdown

```java
import com.enconvert.model.UrlToMarkdownOptions;
import com.enconvert.model.UrlToPdfOptions;
import com.enconvert.model.UrlToScreenshotOptions;

client.convertUrlToPdf("https://example.com", UrlToPdfOptions.builder().saveTo("page.pdf").build());
client.convertUrlToScreenshot("https://example.com",
        UrlToScreenshotOptions.builder().viewportWidth(1440).saveTo("screenshot.png").build());
client.convertUrlToMarkdown("https://example.com/article", UrlToMarkdownOptions.builder().saveTo("article.md").build());
```

`convertUrlToMarkdown` extracts clean GitHub-Flavored Markdown — strips nav/footer/ads/scripts, keeps the main article content, and adds YAML frontmatter (title, description, url, links, images).

### Website to PDF / Screenshot (whole-site batch)

Discover every page of a website (via sitemap, or full crawl on higher plans), convert each one in the background, and receive a single ZIP. Requires a private API key with crawl access.

```java
import com.enconvert.model.BatchStatus;
import com.enconvert.model.BatchSubmission;
import com.enconvert.model.WaitForBatchOptions;
import com.enconvert.model.WebsiteToPdfOptions;

BatchSubmission batch = client.convertWebsiteToPdf("https://example.com",
        WebsiteToPdfOptions.builder()
                .crawlMode("sitemap")                    // "auto" (default) | "sitemap" | "full"
                .excludePatterns(List.of("/blog/tag/"))  // full crawl mode only
                .build());
System.out.println(batch.batchId() + " " + batch.urlCount() + " " + batch.discoveryMethod());

// Block until done and save the ZIP:
BatchStatus status = client.waitForBatch(batch.batchId(),
        WaitForBatchOptions.builder().saveTo("site.zip").build());
System.out.println(status.completed() + " of " + status.total() + " pages converted");

// Or poll yourself:
BatchStatus s = client.getBatchStatus(batch.batchId());
if (!"processing".equals(s.status())) System.out.println(s.zipDownloadUrl());
```

`convertWebsiteToScreenshot` works the same way and produces a ZIP of PNGs.

### PDF options & authenticated pages

```java
import com.enconvert.model.BrowserCookie;
import com.enconvert.model.HttpBasicAuth;
import com.enconvert.model.PdfHeaderFooter;
import com.enconvert.model.PdfMargins;

client.convertUrlToPdf("https://internal.example.com/report",
        UrlToPdfOptions.builder()
                .pdfOptions(PdfOptions.builder()
                        .pageSize("A4")               // or custom dimensions via pageWidth + pageHeight
                        .orientation("landscape")
                        .margins(new PdfMargins(10.0, 10.0, 15.0, 15.0))
                        .header(new PdfHeaderFooter("Quarterly Report", 15.0))
                        .footer(new PdfHeaderFooter("Confidential", 12.0))
                        .build())
                .auth(new HttpBasicAuth("user", "pass"))
                // or cookies / headers:
                .cookies(List.of(BrowserCookie.builder("session", "abc123").domain("internal.example.com").build()))
                .headers(Map.of("X-Tenant", "acme"))
                .saveTo("report.pdf")
                .build());
```

Do not combine `auth` with an `Authorization` header — the API rejects the conflict.

### Job status (async polling)

```java
import com.enconvert.model.JobStatus;

JobStatus status = client.getJobStatus("job_abc123");
if ("success".equals(status.status())) {
    System.out.println(status.presignedUrl());
}
```

---

## Error Handling

```java
import com.enconvert.exceptions.ApiException;
import com.enconvert.exceptions.AuthenticationException;
import com.enconvert.exceptions.RateLimitException;

try {
    client.convertUrlToPdf("https://example.com");
} catch (AuthenticationException e) {
    System.err.println("Invalid API key");
} catch (RateLimitException e) {
    System.err.println("Too many requests — slow down");
} catch (ApiException e) {
    System.err.println("API error [" + e.getStatusCode() + "]: " + e.getMessage());
}
```

## Configuration

```java
Enconvert client = Enconvert.builder("sk_...")
        .baseUrl("https://api.enconvert.com")
        .timeout(Duration.ofSeconds(300)) // default
        .build();
```

## Get an API Key

Sign up at [enconvert.com](https://enconvert.com). Free tier: 100 ops/month, no credit card.

## License

MIT
