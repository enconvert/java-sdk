package com.enconvert;

import com.enconvert.model.BrowserCookie;
import com.enconvert.model.HttpBasicAuth;
import com.enconvert.model.PdfHeaderFooter;
import com.enconvert.model.PdfMargins;
import com.enconvert.model.PdfOptions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Request-body builders mirroring node-sdk's client.ts / internal.ts serializers. */
final class V1Serializers {

    private V1Serializers() {
    }

    /** Shared body for the three single-URL conversions. Render defaults always sent. */
    static Map<String, Object> urlBody(
            String url,
            Integer viewportWidth,
            Integer viewportHeight,
            Boolean loadMedia,
            Boolean enableScroll,
            String outputFilename,
            HttpBasicAuth auth,
            List<BrowserCookie> cookies,
            Map<String, String> headers) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url", url);
        body.put("direct_download", false);
        body.put("viewport_width", viewportWidth != null ? viewportWidth : 1920);
        body.put("viewport_height", viewportHeight != null ? viewportHeight : 1080);
        body.put("load_media", loadMedia != null ? loadMedia : true);
        body.put("enable_scroll", enableScroll != null ? enableScroll : true);
        if (outputFilename != null) body.put("output_filename", outputFilename);
        appendBrowserAccess(body, auth, cookies, headers);
        return body;
    }

    /**
     * Body for website (whole-site) conversions. Render options are only
     * sent when set — the gateway applies the same defaults per page.
     */
    static Map<String, Object> websiteBody(
            String url,
            String crawlMode,
            List<String> includePatterns,
            List<String> excludePatterns,
            String notificationEmail,
            String callbackUrl,
            String outputFilename,
            Integer viewportWidth,
            Integer viewportHeight,
            Boolean loadMedia,
            Boolean enableScroll,
            HttpBasicAuth auth,
            List<BrowserCookie> cookies,
            Map<String, String> headers) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url", url);
        if (crawlMode != null) body.put("crawl_mode", crawlMode);
        if (includePatterns != null) body.put("include_patterns", includePatterns);
        if (excludePatterns != null) body.put("exclude_patterns", excludePatterns);
        if (notificationEmail != null) body.put("notification_email", notificationEmail);
        if (callbackUrl != null) body.put("callback_url", callbackUrl);
        if (outputFilename != null) body.put("output_filename", outputFilename);
        if (viewportWidth != null) body.put("viewport_width", viewportWidth);
        if (viewportHeight != null) body.put("viewport_height", viewportHeight);
        if (loadMedia != null) body.put("load_media", loadMedia);
        if (enableScroll != null) body.put("enable_scroll", enableScroll);
        appendBrowserAccess(body, auth, cookies, headers);
        return body;
    }

    /** Attach the plan-gated auth/cookies/headers fields when provided. */
    private static void appendBrowserAccess(
            Map<String, Object> body, HttpBasicAuth auth, List<BrowserCookie> cookies, Map<String, String> headers) {
        if (auth != null) body.put("auth", httpBasicAuth(auth));
        if (cookies != null) {
            body.put("cookies", cookies.stream().map(V1Serializers::browserCookie).collect(Collectors.toList()));
        }
        if (headers != null) body.put("headers", headers);
    }

    static Map<String, Object> pdfOptions(PdfOptions o) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (o.pageSize() != null) out.put("page_size", o.pageSize());
        if (o.pageWidth() != null) out.put("page_width", o.pageWidth());
        if (o.pageHeight() != null) out.put("page_height", o.pageHeight());
        if (o.orientation() != null) out.put("orientation", o.orientation());
        if (o.margins() != null) out.put("margins", pdfMargins(o.margins()));
        if (o.scale() != null) out.put("scale", o.scale());
        if (o.grayscale() != null) out.put("grayscale", o.grayscale());
        if (o.header() != null) out.put("header", pdfHeaderFooter(o.header()));
        if (o.footer() != null) out.put("footer", pdfHeaderFooter(o.footer()));
        return out;
    }

    private static Map<String, Object> pdfMargins(PdfMargins m) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (m.top() != null) out.put("top", m.top());
        if (m.bottom() != null) out.put("bottom", m.bottom());
        if (m.left() != null) out.put("left", m.left());
        if (m.right() != null) out.put("right", m.right());
        return out;
    }

    private static Map<String, Object> pdfHeaderFooter(PdfHeaderFooter h) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (h.content() != null) out.put("content", h.content());
        if (h.height() != null) out.put("height", h.height());
        return out;
    }

    // Package-private (not private): reused by V2Serializers for perceive's auth/cookies fields.
    static Map<String, Object> httpBasicAuth(HttpBasicAuth a) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("username", a.username());
        out.put("password", a.password());
        return out;
    }

    // Cookie field names are forwarded as-is (httpOnly, sameSite) — they pass
    // through to the browser/CDP layer untouched, same as node-sdk.
    static Map<String, Object> browserCookie(BrowserCookie c) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("name", c.name());
        out.put("value", c.value());
        if (c.domain() != null) out.put("domain", c.domain());
        if (c.url() != null) out.put("url", c.url());
        if (c.path() != null) out.put("path", c.path());
        if (c.expires() != null) out.put("expires", c.expires());
        if (c.httpOnly() != null) out.put("httpOnly", c.httpOnly());
        if (c.secure() != null) out.put("secure", c.secure());
        if (c.sameSite() != null) out.put("sameSite", c.sameSite());
        return out;
    }
}
