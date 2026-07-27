package com.enconvert.model.v2;

/**
 * Chunking options for {@code EnconvertV2#ingest}.
 *
 * @param maxWords        Words per chunk, 32-4000, default 512.
 * @param sentenceOverlap Sentences repeated between consecutive chunks, 0-10, default 1.
 */
public record IngestChunkOptions(Integer maxWords, Integer sentenceOverlap) {
}
