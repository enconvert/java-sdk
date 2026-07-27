package com.enconvert.model;

import java.util.List;
import java.util.Map;

/** Options for {@code Enconvert#convertUrlToPdf}. */
public final class UrlToPdfOptions {

    private final Integer viewportWidth;
    private final Integer viewportHeight;
    private final Boolean loadMedia;
    private final Boolean enableScroll;
    private final String outputFilename;
    private final HttpBasicAuth auth;
    private final List<BrowserCookie> cookies;
    private final Map<String, String> headers;
    private final String saveTo;
    private final Boolean singlePage;
    private final PdfOptions pdfOptions;

    private UrlToPdfOptions(Builder b) {
        this.viewportWidth = b.viewportWidth;
        this.viewportHeight = b.viewportHeight;
        this.loadMedia = b.loadMedia;
        this.enableScroll = b.enableScroll;
        this.outputFilename = b.outputFilename;
        this.auth = b.auth;
        this.cookies = b.cookies;
        this.headers = b.headers;
        this.saveTo = b.saveTo;
        this.singlePage = b.singlePage;
        this.pdfOptions = b.pdfOptions;
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

    /** HTTP Basic Auth for protected pages (plan-gated). */
    public HttpBasicAuth auth() {
        return auth;
    }

    /** Cookies injected before rendering, max 50 (plan-gated). */
    public List<BrowserCookie> cookies() {
        return cookies;
    }

    /** Extra request headers, max 20; hop-by-hop headers rejected (plan-gated). */
    public Map<String, String> headers() {
        return headers;
    }

    public String saveTo() {
        return saveTo;
    }

    /** Defaults to {@code true} when unset. */
    public Boolean singlePage() {
        return singlePage;
    }

    public PdfOptions pdfOptions() {
        return pdfOptions;
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
        private Boolean singlePage;
        private PdfOptions pdfOptions;

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

        public Builder singlePage(boolean singlePage) {
            this.singlePage = singlePage;
            return this;
        }

        public Builder pdfOptions(PdfOptions pdfOptions) {
            this.pdfOptions = pdfOptions;
            return this;
        }

        public UrlToPdfOptions build() {
            return new UrlToPdfOptions(this);
        }
    }
}
