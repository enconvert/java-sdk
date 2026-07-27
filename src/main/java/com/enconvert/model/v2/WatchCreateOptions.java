package com.enconvert.model.v2;

import java.util.Map;

/** Options for {@code EnconvertV2#createWatcher}. */
public final class WatchCreateOptions {

    private final Integer frequencyMinutes;
    private final String diffMode;
    private final Map<String, Object> trackFields;
    private final String webhookUrl;
    private final Boolean notifyEmail;

    private WatchCreateOptions(Builder b) {
        this.frequencyMinutes = b.frequencyMinutes;
        this.diffMode = b.diffMode;
        this.trackFields = b.trackFields;
        this.webhookUrl = b.webhookUrl;
        this.notifyEmail = b.notifyEmail;
    }

    /** Minutes between checks, 60-43200 (hourly floor is hard). Default 60. */
    public Integer frequencyMinutes() {
        return frequencyMinutes;
    }

    /** Default "auto" (diff engine picks by content type). See {@link V2Enums.WatchDiffMode}. */
    public String diffMode() {
        return diffMode;
    }

    /** Optional field/selector subset for the diff engine. */
    public Map<String, Object> trackFields() {
        return trackFields;
    }

    /** Change-notification webhook, HMAC-signed. */
    public String webhookUrl() {
        return webhookUrl;
    }

    /** Email the project owner on changes. Default true. */
    public Boolean notifyEmail() {
        return notifyEmail;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer frequencyMinutes;
        private String diffMode;
        private Map<String, Object> trackFields;
        private String webhookUrl;
        private Boolean notifyEmail;

        private Builder() {
        }

        public Builder frequencyMinutes(int frequencyMinutes) {
            this.frequencyMinutes = frequencyMinutes;
            return this;
        }

        public Builder diffMode(String diffMode) {
            this.diffMode = diffMode;
            return this;
        }

        public Builder trackFields(Map<String, Object> trackFields) {
            this.trackFields = trackFields;
            return this;
        }

        public Builder webhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
            return this;
        }

        public Builder notifyEmail(boolean notifyEmail) {
            this.notifyEmail = notifyEmail;
            return this;
        }

        public WatchCreateOptions build() {
            return new WatchCreateOptions(this);
        }
    }
}
