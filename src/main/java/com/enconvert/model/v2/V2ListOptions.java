package com.enconvert.model.v2;

/** Shared pagination options for listIngestJobs / listWatchers. */
public final class V2ListOptions {

    private final Integer skip;
    private final Integer limit;

    private V2ListOptions(Builder b) {
        this.skip = b.skip;
        this.limit = b.limit;
    }

    /** Rows to skip (default 0). */
    public Integer skip() {
        return skip;
    }

    /** Page size, 1-100 (default 20). */
    public Integer limit() {
        return limit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer skip;
        private Integer limit;

        private Builder() {
        }

        public Builder skip(int skip) {
            this.skip = skip;
            return this;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public V2ListOptions build() {
            return new V2ListOptions(this);
        }
    }
}
