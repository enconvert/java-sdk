package com.enconvert.model;

/**
 * Cookie injected into the browser context before rendering (plan-gated).
 * The API requires {@code name}, {@code value}, and either {@code domain} or
 * {@code url}. When {@code domain} is set without {@code path}, the API
 * defaults {@code path} to "/".
 */
public final class BrowserCookie {

    private final String name;
    private final String value;
    private final String domain;
    private final String url;
    private final String path;
    private final Long expires;
    private final Boolean httpOnly;
    private final Boolean secure;
    private final String sameSite;

    private BrowserCookie(Builder b) {
        this.name = b.name;
        this.value = b.value;
        this.domain = b.domain;
        this.url = b.url;
        this.path = b.path;
        this.expires = b.expires;
        this.httpOnly = b.httpOnly;
        this.secure = b.secure;
        this.sameSite = b.sameSite;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public String domain() {
        return domain;
    }

    public String url() {
        return url;
    }

    public String path() {
        return path;
    }

    public Long expires() {
        return expires;
    }

    public Boolean httpOnly() {
        return httpOnly;
    }

    public Boolean secure() {
        return secure;
    }

    /** One of "Strict", "Lax", "None". */
    public String sameSite() {
        return sameSite;
    }

    public static Builder builder(String name, String value) {
        return new Builder(name, value);
    }

    public static final class Builder {
        private final String name;
        private final String value;
        private String domain;
        private String url;
        private String path;
        private Long expires;
        private Boolean httpOnly;
        private Boolean secure;
        private String sameSite;

        private Builder(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder expires(long expires) {
            this.expires = expires;
            return this;
        }

        public Builder httpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
            return this;
        }

        public Builder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public Builder sameSite(String sameSite) {
            this.sameSite = sameSite;
            return this;
        }

        public BrowserCookie build() {
            return new BrowserCookie(this);
        }
    }
}
