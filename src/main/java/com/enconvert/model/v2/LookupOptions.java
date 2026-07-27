package com.enconvert.model.v2;

/** Options for {@code EnconvertV2#lookup}. */
public final class LookupOptions {

    private final String category;
    private final String country;
    private final String locale;
    private final String timeFilter;
    private final Integer numResults;
    private final Integer page;
    private final String location;
    private final Boolean autocorrect;
    private final Integer perceiveTop;

    private LookupOptions(Builder b) {
        this.category = b.category;
        this.country = b.country;
        this.locale = b.locale;
        this.timeFilter = b.timeFilter;
        this.numResults = b.numResults;
        this.page = b.page;
        this.location = b.location;
        this.autocorrect = b.autocorrect;
        this.perceiveTop = b.perceiveTop;
    }

    /** Default "web". See {@link V2Enums.LookupCategory}. */
    public String category() {
        return category;
    }

    /** Google "gl" country code, e.g. "us", "in". */
    public String country() {
        return country;
    }

    /** Google "hl" interface language, e.g. "en". */
    public String locale() {
        return locale;
    }

    /** One of "hour", "day", "week", "month", "year". */
    public String timeFilter() {
        return timeFilter;
    }

    /** 1-100, default 10. */
    public Integer numResults() {
        return numResults;
    }

    /** 1-10, default 1. */
    public Integer page() {
        return page;
    }

    /** Free-text location, e.g. "Austin, Texas". */
    public String location() {
        return location;
    }

    /** Default true. */
    public Boolean autocorrect() {
        return autocorrect;
    }

    /**
     * Auto-perceive the top-N result URLs (0-10, default 0). Each consumes
     * one perceive-quota unit and runs a full browser render.
     */
    public Integer perceiveTop() {
        return perceiveTop;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String category;
        private String country;
        private String locale;
        private String timeFilter;
        private Integer numResults;
        private Integer page;
        private String location;
        private Boolean autocorrect;
        private Integer perceiveTop;

        private Builder() {
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder locale(String locale) {
            this.locale = locale;
            return this;
        }

        public Builder timeFilter(String timeFilter) {
            this.timeFilter = timeFilter;
            return this;
        }

        public Builder numResults(int numResults) {
            this.numResults = numResults;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder autocorrect(boolean autocorrect) {
            this.autocorrect = autocorrect;
            return this;
        }

        public Builder perceiveTop(int perceiveTop) {
            this.perceiveTop = perceiveTop;
            return this;
        }

        public LookupOptions build() {
            return new LookupOptions(this);
        }
    }
}
