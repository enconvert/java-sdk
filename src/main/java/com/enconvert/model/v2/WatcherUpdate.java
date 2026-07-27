package com.enconvert.model.v2;

import java.util.Map;

/**
 * Fields to update on a watcher. At least one must be set — validated by
 * {@code EnconvertV2#updateWatcher}. An explicit empty string {@code ""} for
 * {@code webhookUrl} clears the webhook (as opposed to {@code null}, which
 * means "leave unchanged").
 */
public final class WatcherUpdate {

    private final Integer frequencyMinutes;
    private final String diffMode;
    private final Map<String, Object> trackFields;
    private final String webhookUrl;
    private final Boolean notifyEmail;
    private final String status;

    private WatcherUpdate(Builder b) {
        this.frequencyMinutes = b.frequencyMinutes;
        this.diffMode = b.diffMode;
        this.trackFields = b.trackFields;
        this.webhookUrl = b.webhookUrl;
        this.notifyEmail = b.notifyEmail;
        this.status = b.status;
    }

    /** 60-43200. */
    public Integer frequencyMinutes() {
        return frequencyMinutes;
    }

    public String diffMode() {
        return diffMode;
    }

    public Map<String, Object> trackFields() {
        return trackFields;
    }

    /** An empty string "" explicitly clears the webhook. */
    public String webhookUrl() {
        return webhookUrl;
    }

    public Boolean notifyEmail() {
        return notifyEmail;
    }

    /** "active" or "paused". Deleting goes through {@code deleteWatcher}. */
    public String status() {
        return status;
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
        private String status;

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

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public WatcherUpdate build() {
            return new WatcherUpdate(this);
        }
    }
}
