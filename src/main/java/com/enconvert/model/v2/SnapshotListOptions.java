package com.enconvert.model.v2;

/** Options for {@code EnconvertV2#getWatcherSnapshots}. */
public final class SnapshotListOptions {

    private final Integer limit;

    private SnapshotListOptions(Builder b) {
        this.limit = b.limit;
    }

    /** Page size, 1-100 (default 20). */
    public Integer limit() {
        return limit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer limit;

        private Builder() {
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public SnapshotListOptions build() {
            return new SnapshotListOptions(this);
        }
    }
}
