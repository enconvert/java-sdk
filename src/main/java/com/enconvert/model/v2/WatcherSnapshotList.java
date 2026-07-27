package com.enconvert.model.v2;

import java.util.List;

/** Result of {@code EnconvertV2#getWatcherSnapshots}. */
public record WatcherSnapshotList(String watcherId, List<WatcherSnapshot> snapshots, int limit) {
}
