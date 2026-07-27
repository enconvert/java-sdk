package com.enconvert.model.v2;

/**
 * Browser viewport for perceive rendering.
 *
 * @param width  320-3840, default 1920.
 * @param height 240-2160, default 1080.
 */
public record PerceiveViewport(Integer width, Integer height) {
}
