package com.enconvert.model.v2;

import java.util.List;

/** Options for {@code EnconvertV2#discover}. */
public final class DiscoverOptions {

    private final String mode;
    private final Integer maxUrls;
    private final Integer maxDepth;
    private final List<String> includePatterns;
    private final List<String> excludePatterns;
    private final Boolean sameDomainOnly;
    private final Boolean respectRobots;

    private DiscoverOptions(Builder b) {
        this.mode = b.mode;
        this.maxUrls = b.maxUrls;
        this.maxDepth = b.maxDepth;
        this.includePatterns = b.includePatterns;
        this.excludePatterns = b.excludePatterns;
        this.sameDomainOnly = b.sameDomainOnly;
        this.respectRobots = b.respectRobots;
    }

    /** Default "hybrid" (sitemap + HTTP crawl). One of "sitemap", "crawl", "hybrid". */
    public String mode() {
        return mode;
    }

    /** 1-1000, default 100. */
    public Integer maxUrls() {
        return maxUrls;
    }

    /** 1-5, default 2. */
    public Integer maxDepth() {
        return maxDepth;
    }

    /** Regex allowlist (re.search semantics), max 50. */
    public List<String> includePatterns() {
        return includePatterns;
    }

    /** Regex denylist, applied after includePatterns, max 50. */
    public List<String> excludePatterns() {
        return excludePatterns;
    }

    /** Default true. */
    public Boolean sameDomainOnly() {
        return sameDomainOnly;
    }

    public Boolean respectRobots() {
        return respectRobots;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String mode;
        private Integer maxUrls;
        private Integer maxDepth;
        private List<String> includePatterns;
        private List<String> excludePatterns;
        private Boolean sameDomainOnly;
        private Boolean respectRobots;

        private Builder() {
        }

        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        public Builder maxUrls(int maxUrls) {
            this.maxUrls = maxUrls;
            return this;
        }

        public Builder maxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
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

        public Builder sameDomainOnly(boolean sameDomainOnly) {
            this.sameDomainOnly = sameDomainOnly;
            return this;
        }

        public Builder respectRobots(boolean respectRobots) {
            this.respectRobots = respectRobots;
            return this;
        }

        public DiscoverOptions build() {
            return new DiscoverOptions(this);
        }
    }
}
