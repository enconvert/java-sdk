package com.enconvert.model.v2;

import com.google.gson.JsonObject;

/** One search hit, optionally carrying its full perceive result. */
public record LookupItem(
        String title,
        String url,
        String snippet,
        Integer position,
        String source,
        String date,
        String imageUrl,
        String thumbnailUrl,
        /** Provider-specific passthrough fields. */
        JsonObject extra,
        /** Present for the top-N results when perceiveTop &gt; 0 and it succeeded. */
        PerceiveResult perceive) {
}
