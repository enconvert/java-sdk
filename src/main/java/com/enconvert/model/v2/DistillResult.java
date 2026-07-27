package com.enconvert.model.v2;

import java.util.List;

/** Result of {@code EnconvertV2#distill}. */
public record DistillResult(
        String operationId,
        int total,
        int completed,
        int failed,
        List<DistillItem> results,
        int totalCostCents,
        List<String> warnings) {
}
