package com.enconvert.model.v2;

/**
 * Discover-then-distill seed for {@code DistillOptions}.
 *
 * @param url      seed URL.
 * @param mode     default "hybrid". See {@link V2Enums.DiscoverMode}.
 * @param maxPages 1-50, default 10. Cap on URLs discovered AND distilled.
 */
public record DistillDiscoverFrom(String url, String mode, Integer maxPages) {

    public DistillDiscoverFrom(String url) {
        this(url, null, null);
    }
}
