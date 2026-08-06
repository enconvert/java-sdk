package com.enconvert.model.v2;

import com.enconvert.model.BrowserCookie;
import com.enconvert.model.HttpBasicAuth;
import com.enconvert.model.PdfOptions;

import java.util.List;
import java.util.Map;

/** Per-render options shared by {@code perceive} and {@code perceiveBatch}. */
public final class PerceiveOptions {

    private final List<String> outputs;
    private final List<String> extract;
    private final Map<String, Object> schema;
    private final String waitFor;
    private final Integer waitTimeoutMs;
    private final String jsCode;
    private final PerceiveViewport viewport;
    private final Map<String, String> headers;
    private final List<BrowserCookie> cookies;
    private final HttpBasicAuth auth;
    private final String proxyUrl;
    private final Map<String, Object> geolocation;
    private final List<Map<String, Object>> actionChain;
    private final String cacheMode;
    private final PdfOptions pdfOptions;
    private final List<String> blockResources;
    private final Boolean respectRobots;
    private final Boolean mobile;
    private final Boolean onlyMainContent;
    private final Boolean directDownload;

    private PerceiveOptions(Builder b) {
        this.outputs = b.outputs;
        this.extract = b.extract;
        this.schema = b.schema;
        this.waitFor = b.waitFor;
        this.waitTimeoutMs = b.waitTimeoutMs;
        this.jsCode = b.jsCode;
        this.viewport = b.viewport;
        this.headers = b.headers;
        this.cookies = b.cookies;
        this.auth = b.auth;
        this.proxyUrl = b.proxyUrl;
        this.geolocation = b.geolocation;
        this.actionChain = b.actionChain;
        this.cacheMode = b.cacheMode;
        this.pdfOptions = b.pdfOptions;
        this.blockResources = b.blockResources;
        this.respectRobots = b.respectRobots;
        this.mobile = b.mobile;
        this.onlyMainContent = b.onlyMainContent;
        this.directDownload = b.directDownload;
    }

    /** Artifacts to produce. Default: ["markdown", "structured"]. See {@link V2Enums.PerceiveOutputs}. */
    public List<String> outputs() {
        return outputs;
    }

    /** Heuristic extraction targets. See {@link V2Enums.PerceiveExtract}. */
    public List<String> extract() {
        return extract;
    }

    /** JSON schema for structured extraction (LLM tier, plan-gated). */
    public Map<String, Object> schema() {
        return schema;
    }

    /** CSS selector (optionally "css:...") or "js:&lt;expr&gt;" to await. */
    public String waitFor() {
        return waitFor;
    }

    /** 0-60000, default 30000. */
    public Integer waitTimeoutMs() {
        return waitTimeoutMs;
    }

    /** JavaScript executed after navigation. Max 20000 chars. */
    public String jsCode() {
        return jsCode;
    }

    public PerceiveViewport viewport() {
        return viewport;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public List<BrowserCookie> cookies() {
        return cookies;
    }

    /** HTTP Basic Auth (plan-gated). */
    public HttpBasicAuth auth() {
        return auth;
    }

    /** Not yet available server-side — currently rejected with 422. */
    public String proxyUrl() {
        return proxyUrl;
    }

    /** Not yet available server-side — currently rejected with 422. */
    public Map<String, Object> geolocation() {
        return geolocation;
    }

    /** Not yet available server-side — currently rejected with 422. */
    public List<Map<String, Object>> actionChain() {
        return actionChain;
    }

    /** Default "enabled" (1h cache). "bypass" skips, "refresh" re-renders. See {@link V2Enums.PerceiveCacheMode}. */
    public String cacheMode() {
        return cacheMode;
    }

    /** Only meaningful when outputs includes "pdf". */
    public PdfOptions pdfOptions() {
        return pdfOptions;
    }

    /** Resource types the browser should not load. See {@link V2Enums.PerceiveResourceType}. */
    public List<String> blockResources() {
        return blockResources;
    }

    public Boolean respectRobots() {
        return respectRobots;
    }

    public Boolean mobile() {
        return mobile;
    }

    /** Strip site chrome (nav, header, footer, cookie banners) from the markdown artifact and main_content extract. API default: true. */
    public Boolean onlyMainContent() {
        return onlyMainContent;
    }

    /** Respond with the artifact bytes directly (perceive only — perceiveBatch rejects it with 422). Requires exactly one artifact-producing output. */
    public Boolean directDownload() {
        return directDownload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        List<String> outputs;
        List<String> extract;
        Map<String, Object> schema;
        String waitFor;
        Integer waitTimeoutMs;
        String jsCode;
        PerceiveViewport viewport;
        Map<String, String> headers;
        List<BrowserCookie> cookies;
        HttpBasicAuth auth;
        String proxyUrl;
        Map<String, Object> geolocation;
        List<Map<String, Object>> actionChain;
        String cacheMode;
        PdfOptions pdfOptions;
        List<String> blockResources;
        Boolean respectRobots;
        Boolean mobile;
        Boolean onlyMainContent;
        Boolean directDownload;

        Builder() {
        }

        public Builder outputs(List<String> outputs) {
            this.outputs = outputs;
            return this;
        }

        public Builder extract(List<String> extract) {
            this.extract = extract;
            return this;
        }

        public Builder schema(Map<String, Object> schema) {
            this.schema = schema;
            return this;
        }

        public Builder waitFor(String waitFor) {
            this.waitFor = waitFor;
            return this;
        }

        public Builder waitTimeoutMs(int waitTimeoutMs) {
            this.waitTimeoutMs = waitTimeoutMs;
            return this;
        }

        public Builder jsCode(String jsCode) {
            this.jsCode = jsCode;
            return this;
        }

        public Builder viewport(PerceiveViewport viewport) {
            this.viewport = viewport;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder cookies(List<BrowserCookie> cookies) {
            this.cookies = cookies;
            return this;
        }

        public Builder auth(HttpBasicAuth auth) {
            this.auth = auth;
            return this;
        }

        public Builder proxyUrl(String proxyUrl) {
            this.proxyUrl = proxyUrl;
            return this;
        }

        public Builder geolocation(Map<String, Object> geolocation) {
            this.geolocation = geolocation;
            return this;
        }

        public Builder actionChain(List<Map<String, Object>> actionChain) {
            this.actionChain = actionChain;
            return this;
        }

        public Builder cacheMode(String cacheMode) {
            this.cacheMode = cacheMode;
            return this;
        }

        public Builder pdfOptions(PdfOptions pdfOptions) {
            this.pdfOptions = pdfOptions;
            return this;
        }

        public Builder blockResources(List<String> blockResources) {
            this.blockResources = blockResources;
            return this;
        }

        public Builder respectRobots(boolean respectRobots) {
            this.respectRobots = respectRobots;
            return this;
        }

        public Builder mobile(boolean mobile) {
            this.mobile = mobile;
            return this;
        }

        public Builder onlyMainContent(boolean onlyMainContent) {
            this.onlyMainContent = onlyMainContent;
            return this;
        }

        public Builder directDownload(boolean directDownload) {
            this.directDownload = directDownload;
            return this;
        }

        public PerceiveOptions build() {
            return new PerceiveOptions(this);
        }
    }
}
