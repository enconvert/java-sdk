package com.enconvert.model;

import java.util.List;
import java.util.Map;

/** Options for {@code Enconvert#convertUrlToScreenshot}. */
public final class UrlToScreenshotOptions {

    private final Integer viewportWidth;
    private final Integer viewportHeight;
    private final Boolean loadMedia;
    private final Boolean enableScroll;
    private final String outputFilename;
    private final HttpBasicAuth auth;
    private final List<BrowserCookie> cookies;
    private final Map<String, String> headers;
    private final String saveTo;

    private UrlToScreenshotOptions(Builder b) {
        this.viewportWidth = b.viewportWidth;
        this.viewportHeight = b.viewportHeight;
        this.loadMedia = b.loadMedia;
        this.enableScroll = b.enableScroll;
        this.outputFilename = b.outputFilename;
        this.auth = b.auth;
        this.cookies = b.cookies;
        this.headers = b.headers;
        this.saveTo = b.saveTo;
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

    public String outputFilename() {
        return outputFilename;
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

    public String saveTo() {
        return saveTo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer viewportWidth;
        private Integer viewportHeight;
        private Boolean loadMedia;
        private Boolean enableScroll;
        private String outputFilename;
        private HttpBasicAuth auth;
        private List<BrowserCookie> cookies;
        private Map<String, String> headers;
        private String saveTo;

        private Builder() {
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

        public Builder outputFilename(String outputFilename) {
            this.outputFilename = outputFilename;
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

        public Builder saveTo(String saveTo) {
            this.saveTo = saveTo;
            return this;
        }

        public UrlToScreenshotOptions build() {
            return new UrlToScreenshotOptions(this);
        }
    }
}
