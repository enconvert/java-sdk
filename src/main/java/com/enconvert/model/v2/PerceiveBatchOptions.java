package com.enconvert.model.v2;

import com.enconvert.model.BrowserCookie;
import com.enconvert.model.HttpBasicAuth;
import com.enconvert.model.PdfOptions;

import java.util.List;
import java.util.Map;

/**
 * Options for {@code perceiveBatch}: every {@link PerceiveOptions} field plus
 * the batch output mode.
 */
public final class PerceiveBatchOptions {

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
    private final String outputMode;

    private PerceiveBatchOptions(Builder b) {
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
        this.outputMode = b.outputMode;
    }

    public List<String> outputs() {
        return outputs;
    }

    public List<String> extract() {
        return extract;
    }

    public Map<String, Object> schema() {
        return schema;
    }

    public String waitFor() {
        return waitFor;
    }

    public Integer waitTimeoutMs() {
        return waitTimeoutMs;
    }

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

    public HttpBasicAuth auth() {
        return auth;
    }

    public String proxyUrl() {
        return proxyUrl;
    }

    public Map<String, Object> geolocation() {
        return geolocation;
    }

    public List<Map<String, Object>> actionChain() {
        return actionChain;
    }

    public String cacheMode() {
        return cacheMode;
    }

    public PdfOptions pdfOptions() {
        return pdfOptions;
    }

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

    /** "manifest" (default) or "zip" (bundle all artifacts once complete). See {@link V2Enums.PerceiveBatchOutputMode}. */
    public String outputMode() {
        return outputMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private List<String> outputs;
        private List<String> extract;
        private Map<String, Object> schema;
        private String waitFor;
        private Integer waitTimeoutMs;
        private String jsCode;
        private PerceiveViewport viewport;
        private Map<String, String> headers;
        private List<BrowserCookie> cookies;
        private HttpBasicAuth auth;
        private String proxyUrl;
        private Map<String, Object> geolocation;
        private List<Map<String, Object>> actionChain;
        private String cacheMode;
        private PdfOptions pdfOptions;
        private List<String> blockResources;
        private Boolean respectRobots;
        private Boolean mobile;
        private Boolean onlyMainContent;
        private String outputMode;

        private Builder() {
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

        public Builder outputMode(String outputMode) {
            this.outputMode = outputMode;
            return this;
        }

        public PerceiveBatchOptions build() {
            return new PerceiveBatchOptions(this);
        }
    }
}
