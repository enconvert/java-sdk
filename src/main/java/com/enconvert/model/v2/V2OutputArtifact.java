package com.enconvert.model.v2;

/** A rendered output stored server-side, addressed by signed URL. */
public record V2OutputArtifact(
        /** Pre-signed download URL (15 minutes). Re-signed on every status GET. */
        String url,
        String objectKey,
        long sizeBytes,
        String contentType,
        int expiresIn) {
}
