package com.enconvert.model.v2;

/** Ingest completion webhook signing secret and verification details. */
public record WebhookSecret(
        String secret,
        /** Header carrying the HMAC signature, e.g. "X-Enconvert-Signature". */
        String signatureHeader,
        String timestampHeader,
        String signatureScheme,
        int replayToleranceSeconds,
        /** True when this response just replaced the previous secret. */
        boolean rotated) {
}
