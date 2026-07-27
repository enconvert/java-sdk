package com.enconvert.model.v2;

/**
 * Options for {@code EnconvertV2#ingestFiles} (POST /v2/ingest/files). Uploaded
 * documents are converted to Markdown and chunked through the same pipeline as
 * {@code ingest}.
 */
public final class IngestFilesOptions {

    private final IngestChunkOptions chunk;
    private final String webhookUrl;

    private IngestFilesOptions(Builder b) {
        this.chunk = b.chunk;
        this.webhookUrl = b.webhookUrl;
    }

    /** Heading-aware chunker parameters. */
    public IngestChunkOptions chunk() {
        return chunk;
    }

    /** Completion webhook, HMAC-signed (see {@code getWebhookSecret}). */
    public String webhookUrl() {
        return webhookUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private IngestChunkOptions chunk;
        private String webhookUrl;

        private Builder() {
        }

        public Builder chunk(IngestChunkOptions chunk) {
            this.chunk = chunk;
            return this;
        }

        public Builder webhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
            return this;
        }

        public IngestFilesOptions build() {
            return new IngestFilesOptions(this);
        }
    }
}
