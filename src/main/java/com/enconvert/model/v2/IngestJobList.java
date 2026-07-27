package com.enconvert.model.v2;

import java.util.List;

/** Result of {@code EnconvertV2#listIngestJobs}. */
public record IngestJobList(List<IngestJobSummary> jobs, int skip, int limit, boolean hasMore) {
}
