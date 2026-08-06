package com.enconvert;

import com.enconvert.model.v2.CssField;
import com.enconvert.model.v2.CssSchema;
import com.enconvert.model.v2.PerceiveBatchOptions;
import com.enconvert.model.v2.PerceiveOptions;
import com.enconvert.model.v2.PerceiveViewport;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** Request-body builders mirroring node-sdk's v2.ts serializers. */
final class V2Serializers {

    private V2Serializers() {
    }

    static Map<String, Object> perceiveOptions(PerceiveOptions o) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (o.outputs() != null) out.put("outputs", o.outputs());
        if (o.extract() != null) out.put("extract", o.extract());
        if (o.schema() != null) out.put("schema", o.schema());
        if (o.waitFor() != null) out.put("wait_for", o.waitFor());
        if (o.waitTimeoutMs() != null) out.put("wait_timeout_ms", o.waitTimeoutMs());
        if (o.jsCode() != null) out.put("js_code", o.jsCode());
        if (o.viewport() != null) out.put("viewport", viewport(o.viewport()));
        if (o.headers() != null) out.put("headers", o.headers());
        if (o.cookies() != null) {
            out.put("cookies", o.cookies().stream().map(V1Serializers::browserCookie).collect(Collectors.toList()));
        }
        if (o.auth() != null) out.put("auth", V1Serializers.httpBasicAuth(o.auth()));
        if (o.proxyUrl() != null) out.put("proxy_url", o.proxyUrl());
        if (o.geolocation() != null) out.put("geolocation", o.geolocation());
        if (o.actionChain() != null) out.put("action_chain", o.actionChain());
        if (o.cacheMode() != null) out.put("cache_mode", o.cacheMode());
        if (o.pdfOptions() != null) out.put("pdf_options", V1Serializers.pdfOptions(o.pdfOptions()));
        if (o.blockResources() != null) out.put("block_resources", o.blockResources());
        if (o.respectRobots() != null) out.put("respect_robots", o.respectRobots());
        if (o.mobile() != null) out.put("mobile", o.mobile());
        if (o.onlyMainContent() != null) out.put("only_main_content", o.onlyMainContent());
        if (o.directDownload() != null) out.put("direct_download", o.directDownload());
        return out;
    }

    /** Same field set as {@link #perceiveOptions(PerceiveOptions)}; {@code outputMode} is handled separately by the caller. */
    static Map<String, Object> perceiveOptions(PerceiveBatchOptions o) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (o.outputs() != null) out.put("outputs", o.outputs());
        if (o.extract() != null) out.put("extract", o.extract());
        if (o.schema() != null) out.put("schema", o.schema());
        if (o.waitFor() != null) out.put("wait_for", o.waitFor());
        if (o.waitTimeoutMs() != null) out.put("wait_timeout_ms", o.waitTimeoutMs());
        if (o.jsCode() != null) out.put("js_code", o.jsCode());
        if (o.viewport() != null) out.put("viewport", viewport(o.viewport()));
        if (o.headers() != null) out.put("headers", o.headers());
        if (o.cookies() != null) {
            out.put("cookies", o.cookies().stream().map(V1Serializers::browserCookie).collect(Collectors.toList()));
        }
        if (o.auth() != null) out.put("auth", V1Serializers.httpBasicAuth(o.auth()));
        if (o.proxyUrl() != null) out.put("proxy_url", o.proxyUrl());
        if (o.geolocation() != null) out.put("geolocation", o.geolocation());
        if (o.actionChain() != null) out.put("action_chain", o.actionChain());
        if (o.cacheMode() != null) out.put("cache_mode", o.cacheMode());
        if (o.pdfOptions() != null) out.put("pdf_options", V1Serializers.pdfOptions(o.pdfOptions()));
        if (o.blockResources() != null) out.put("block_resources", o.blockResources());
        if (o.respectRobots() != null) out.put("respect_robots", o.respectRobots());
        if (o.mobile() != null) out.put("mobile", o.mobile());
        if (o.onlyMainContent() != null) out.put("only_main_content", o.onlyMainContent());
        return out;
    }

    private static Map<String, Object> viewport(PerceiveViewport v) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (v.width() != null) out.put("width", v.width());
        if (v.height() != null) out.put("height", v.height());
        return out;
    }

    static Map<String, Object> cssField(CssField f) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("name", f.name());
        out.put("type", f.type());
        if (f.selector() != null) out.put("selector", f.selector());
        if (f.attribute() != null) out.put("attribute", f.attribute());
        if (f.pattern() != null) out.put("pattern", f.pattern());
        if (f.defaultValue() != null) out.put("default", f.defaultValue());
        if (f.transform() != null) out.put("transform", f.transform());
        if (f.fields() != null) {
            out.put("fields", f.fields().stream().map(V2Serializers::cssField).collect(Collectors.toList()));
        }
        return out;
    }

    static Map<String, Object> cssSchema(CssSchema s) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("baseSelector", s.baseSelector());
        out.put("fields", s.fields().stream().map(V2Serializers::cssField).collect(Collectors.toList()));
        if (s.name() != null) out.put("name", s.name());
        if (s.targetField() != null) out.put("target_field", s.targetField());
        return out;
    }

    /** Builds a "?skip=&limit=" style query suffix, omitting unset params. */
    static String listQuery(Integer skip, Integer limit) {
        StringBuilder qs = new StringBuilder();
        if (skip != null) qs.append(qs.length() == 0 ? "?" : "&").append("skip=").append(skip);
        if (limit != null) qs.append(qs.length() == 0 ? "?" : "&").append("limit=").append(limit);
        return qs.toString();
    }

    static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
