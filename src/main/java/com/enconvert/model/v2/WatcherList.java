package com.enconvert.model.v2;

import java.util.List;

/** Result of {@code EnconvertV2#listWatchers}. */
public record WatcherList(List<WatcherSummary> watchers, int skip, int limit, boolean hasMore) {
}
