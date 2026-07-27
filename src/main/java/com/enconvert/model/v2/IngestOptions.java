package com.enconvert.model.v2;

import java.util.List;

/**
 * Options for {@code EnconvertV2#ingest}. Mode defaults to "urls"; mode
 * "urls" requires a non-empty {@code urls} list and forbids {@code url},
 * every other mode requires a seed {@code url} and forbids {@code urls}.
 * {@link EnconvertV2} validates this before sending the request.
 */
public final class IngestOptions {

    private final String mode;
    private final String url;
    private final List<String> urls;
    private final Integer maxPages;
    private final Integer maxDepth;
    private final Boolean sameDomainOnly;
    private final List<String> includePatterns;
    private final List<String> excludePatterns;
    private final Boolean respectRobots;
    private final String waitFor;
    private final Integer waitTimeoutMs;
    private final IngestChunkOptions chunk;
    private final String webhookUrl;

    private IngestOptions(Builder b) {
        this.mode = b.mode;
        this.url = b.url;
        this.urls = b.urls;
        this.maxPages = b.maxPages;
        this.maxDepth = b.maxDepth;
        this.sameDomainOnly = b.sameDomainOnly;
        this.includePatterns = b.includePatterns;
        this.excludePatterns = b.excludePatterns;
        this.respectRobots = b.respectRobots;
        this.waitFor = b.waitFor;
        this.waitTimeoutMs = b.waitTimeoutMs;
        this.chunk = b.chunk;
        this.webhookUrl = b.webhookUrl;
    }

    /** Default "urls". One of "urls", "sitemap", "crawl". */
    public String mode() {
        return mode;
    }

    /** Seed URL — required for "sitemap"/"crawl", forbidden for "urls". */
    public String url() {
        return url;
    }

    /** Explicit URLs (max 1000) — required for "urls", forbidden otherwise. */
    public List<String> urls() {
        return urls;
    }

    /** Discovery cap for sitemap/crawl, 1-1000, default 50. */
    public Integer maxPages() {
        return maxPages;
    }

    /** 1-5, default 2. */
    public Integer maxDepth() {
        return maxDepth;
    }

    /** Default true. */
    public Boolean sameDomainOnly() {
        return sameDomainOnly;
    }

    public List<String> includePatterns() {
        return includePatterns;
    }

    public List<String> excludePatterns() {
        return excludePatterns;
    }

    public Boolean respectRobots() {
        return respectRobots;
    }

    public String waitFor() {
        return waitFor;
    }

    public Integer waitTimeoutMs() {
        return waitTimeoutMs;
    }

    public IngestChunkOptions chunk() {
        return chunk;
    }

    /** Completion webhook, HMAC-signed (see {@code getWebhookSecret}). */
    public String webhookUrl() {
        return webhookUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String mode;
        private String url;
        private List<String> urls;
        private Integer maxPages;
        private Integer maxDepth;
        private Boolean sameDomainOnly;
        private List<String> includePatterns;
        private List<String> excludePatterns;
        private Boolean respectRobots;
        private String waitFor;
        private Integer waitTimeoutMs;
        private IngestChunkOptions chunk;
        private String webhookUrl;

        private Builder() {
        }

        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder urls(List<String> urls) {
            this.urls = urls;
            return this;
        }

        public Builder maxPages(int maxPages) {
            this.maxPages = maxPages;
            return this;
        }

        public Builder maxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public Builder sameDomainOnly(boolean sameDomainOnly) {
            this.sameDomainOnly = sameDomainOnly;
            return this;
        }

        public Builder includePatterns(List<String> includePatterns) {
            this.includePatterns = includePatterns;
            return this;
        }

        public Builder excludePatterns(List<String> excludePatterns) {
            this.excludePatterns = excludePatterns;
            return this;
        }

        public Builder respectRobots(boolean respectRobots) {
            this.respectRobots = respectRobots;
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

        public Builder chunk(IngestChunkOptions chunk) {
            this.chunk = chunk;
            return this;
        }

        public Builder webhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
            return this;
        }

        public IngestOptions build() {
            return new IngestOptions(this);
        }
    }
}
