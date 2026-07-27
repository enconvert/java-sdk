package com.enconvert;

/**
 * Raw bytes with an explicit filename (and optional content type), for
 * {@code Enconvert#convertImage} / {@code Enconvert#convertDocument}.
 *
 * <p>Use this when converting in-memory bytes that don't come from a file on
 * disk. For files on disk pass a {@link java.nio.file.Path} directly; for
 * anonymous bytes (filename "upload.bin") pass a {@code byte[]} directly —
 * both are accepted via method overloads.
 */
public record FileInput(byte[] data, String filename, String contentType) {

    public FileInput(byte[] data, String filename) {
        this(data, filename, null);
    }
}
