package com.enconvert.model;

import java.util.List;
import java.util.Map;

/**
 * Options for {@code Enconvert#convertWebsiteToScreenshot}.
 *
 * <p>Render fields are only sent to the API when explicitly set here — the
 * gateway applies its own per-page defaults otherwise.
 */
public final class WebsiteToScreenshotOptions {

    private final String crawlMode;
    private final List<String> includePatterns;
    private final List<String> excludePatterns;
    private final String notificationEmail;
    private final String callbackUrl;
    private final String outputFilename;
    private final Integer viewportWidth;
    private final Integer viewportHeight;
    private final Boolean loadMedia;
    private final Boolean enableScroll;
    private final HttpBasicAuth auth;
    private final List<BrowserCookie> cookies;
    private final Map<String, String> headers;

    private WebsiteToScreenshotOptions(Builder b) {
        this.crawlMode = b.crawlMode;
        this.includePatterns = b.includePatterns;
        this.excludePatterns = b.excludePatterns;
        this.notificationEmail = b.notificationEmail;
        this.callbackUrl = b.callbackUrl;
        this.outputFilename = b.outputFilename;
        this.viewportWidth = b.viewportWidth;
        this.viewportHeight = b.viewportHeight;
        this.loadMedia = b.loadMedia;
        this.enableScroll = b.enableScroll;
        this.auth = b.auth;
        this.cookies = b.cookies;
        this.headers = b.headers;
    }

    public String crawlMode() {
        return crawlMode;
    }

    public List<String> includePatterns() {
        return includePatterns;
    }

    public List<String> excludePatterns() {
        return excludePatterns;
    }

    public String notificationEmail() {
        return notificationEmail;
    }

    public String callbackUrl() {
        return callbackUrl;
    }

    public String outputFilename() {
        return outputFilename;
    }

    public Integer viewportWidth() {
        return viewportWidth;
    }

    public Integer viewportHeight() {
        return viewportHeight;
    }

    public Boolean loadMedia() {
        return loadMedia;
    }

    public Boolean enableScroll() {
        return enableScroll;
    }

    public HttpBasicAuth auth() {
        return auth;
    }

    public List<BrowserCookie> cookies() {
        return cookies;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String crawlMode;
        private List<String> includePatterns;
        private List<String> excludePatterns;
        private String notificationEmail;
        private String callbackUrl;
        private String outputFilename;
        private Integer viewportWidth;
        private Integer viewportHeight;
        private Boolean loadMedia;
        private Boolean enableScroll;
        private HttpBasicAuth auth;
        private List<BrowserCookie> cookies;
        private Map<String, String> headers;

        private Builder() {
        }

        public Builder crawlMode(String crawlMode) {
            this.crawlMode = crawlMode;
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

        public Builder notificationEmail(String notificationEmail) {
            this.notificationEmail = notificationEmail;
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public Builder outputFilename(String outputFilename) {
            this.outputFilename = outputFilename;
            return this;
        }

        public Builder viewportWidth(int viewportWidth) {
            this.viewportWidth = viewportWidth;
            return this;
        }

        public Builder viewportHeight(int viewportHeight) {
            this.viewportHeight = viewportHeight;
            return this;
        }

        public Builder loadMedia(boolean loadMedia) {
            this.loadMedia = loadMedia;
            return this;
        }

        public Builder enableScroll(boolean enableScroll) {
            this.enableScroll = enableScroll;
            return this;
        }

        public Builder auth(HttpBasicAuth auth) {
            this.auth = auth;
            return this;
        }

        public Builder cookies(List<BrowserCookie> cookies) {
            this.cookies = cookies;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public WebsiteToScreenshotOptions build() {
            return new WebsiteToScreenshotOptions(this);
        }
    }
}
