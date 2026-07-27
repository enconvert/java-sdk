package com.enconvert.model;

/** Options for {@code Enconvert#waitForBatch}. */
public final class WaitForBatchOptions {

    private final Long intervalMs;
    private final Long timeoutMs;
    private final String saveTo;

    private WaitForBatchOptions(Builder b) {
        this.intervalMs = b.intervalMs;
        this.timeoutMs = b.timeoutMs;
        this.saveTo = b.saveTo;
    }

    /** Poll interval in milliseconds. Defaults to 5_000. */
    public Long intervalMs() {
        return intervalMs;
    }

    /** Give up after this many milliseconds. Defaults to 1_800_000 (30 minutes). */
    public Long timeoutMs() {
        return timeoutMs;
    }

    /** Save the batch ZIP to this local path once available. */
    public String saveTo() {
        return saveTo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long intervalMs;
        private Long timeoutMs;
        private String saveTo;

        private Builder() {
        }

        public Builder intervalMs(long intervalMs) {
            this.intervalMs = intervalMs;
            return this;
        }

        public Builder timeoutMs(long timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public Builder saveTo(String saveTo) {
            this.saveTo = saveTo;
            return this;
        }

        public WaitForBatchOptions build() {
            return new WaitForBatchOptions(this);
        }
    }
}
