package com.enconvert.model.v2;

import com.enconvert.model.BrowserCookie;

import java.util.List;
import java.util.Map;

/**
 * Options for {@code EnconvertV2#distill}. Exactly one of {@code urls} /
 * {@code discoverFrom} must be provided; {@code schema} is always required.
 * {@link EnconvertV2} validates this before sending the request.
 */
public final class DistillOptions {

    private final List<String> urls;
    private final DistillDiscoverFrom discoverFrom;
    private final Map<String, Object> schema;
    private final CssSchema cssSchema;
    private final String waitFor;
    private final Integer waitTimeoutMs;
    private final Map<String, String> headers;
    private final List<BrowserCookie> cookies;
    private final Boolean respectRobots;

    private DistillOptions(Builder b) {
        this.urls = b.urls;
        this.discoverFrom = b.discoverFrom;
        this.schema = b.schema;
        this.cssSchema = b.cssSchema;
        this.waitFor = b.waitFor;
        this.waitTimeoutMs = b.waitTimeoutMs;
        this.headers = b.headers;
        this.cookies = b.cookies;
        this.respectRobots = b.respectRobots;
    }

    /** Explicit URLs to distill (max 50). Exactly one of urls/discoverFrom. */
    public List<String> urls() {
        return urls;
    }

    /** Discover a site's URLs first, then distill each. */
    public DistillDiscoverFrom discoverFrom() {
        return discoverFrom;
    }

    /**
     * Required output shape: a JSON-Schema object
     * ({@code {type: "object", properties: {...}}}) or a flat
     * {@code {field: description}} map. The response data matches this shape.
     */
    public Map<String, Object> schema() {
        return schema;
    }

    /** Optional free CSS pass; missing fields escalate to the LLM tier. */
    public CssSchema cssSchema() {
        return cssSchema;
    }

    public String waitFor() {
        return waitFor;
    }

    /** 0-60000, default 30000. */
    public Integer waitTimeoutMs() {
        return waitTimeoutMs;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public List<BrowserCookie> cookies() {
        return cookies;
    }

    public Boolean respectRobots() {
        return respectRobots;
    }

    /** {@code schema} is required. */
    public static Builder builder(Map<String, Object> schema) {
        return new Builder(schema);
    }

    public static final class Builder {
        private final Map<String, Object> schema;
        private List<String> urls;
        private DistillDiscoverFrom discoverFrom;
        private CssSchema cssSchema;
        private String waitFor;
        private Integer waitTimeoutMs;
        private Map<String, String> headers;
        private List<BrowserCookie> cookies;
        private Boolean respectRobots;

        private Builder(Map<String, Object> schema) {
            this.schema = schema;
        }

        public Builder urls(List<String> urls) {
            this.urls = urls;
            return this;
        }

        public Builder discoverFrom(DistillDiscoverFrom discoverFrom) {
            this.discoverFrom = discoverFrom;
            return this;
        }

        public Builder cssSchema(CssSchema cssSchema) {
            this.cssSchema = cssSchema;
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

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder cookies(List<BrowserCookie> cookies) {
            this.cookies = cookies;
            return this;
        }

        public Builder respectRobots(boolean respectRobots) {
            this.respectRobots = respectRobots;
            return this;
        }

        public DistillOptions build() {
            return new DistillOptions(this);
        }
    }
}
