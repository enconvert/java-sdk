package com.enconvert.model.v2;

import java.util.List;
import java.util.Map;

/** Result of {@code EnconvertV2#discover}. */
public record DiscoverResult(
        String url,
        /** "sitemap", "crawl", or "hybrid". */
        String mode,
        int total,
        List<String> urls,
        int pagesCrawled,
        /** True when more URLs were found than maxUrls allowed. */
        boolean truncated,
        boolean robotsRespected,
        /** Raw counts per source before dedup, e.g. {sitemap: 42, crawl: 30}. */
        Map<String, Integer> sources,
        List<String> warnings) {
}
