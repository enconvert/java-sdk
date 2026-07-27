package com.enconvert.model.v2;

import java.util.List;

/** Free CSS extraction pass run before any LLM escalation. */
public final class CssSchema {

    private final String baseSelector;
    private final List<CssField> fields;
    private final String name;
    private final String targetField;

    private CssSchema(Builder b) {
        this.baseSelector = b.baseSelector;
        this.fields = b.fields;
        this.name = b.name;
        this.targetField = b.targetField;
    }

    /** Matches the repeating container; one extracted record per match. */
    public String baseSelector() {
        return baseSelector;
    }

    public List<CssField> fields() {
        return fields;
    }

    public String name() {
        return name;
    }

    /**
     * Top-level output-schema property the CSS records fill. Array property
     * receives the full list; scalar/object the first record. Inferred when
     * omitted and the schema has exactly one array property.
     */
    public String targetField() {
        return targetField;
    }

    /** {@code baseSelector} and {@code fields} are required. */
    public static Builder builder(String baseSelector, List<CssField> fields) {
        return new Builder(baseSelector, fields);
    }

    public static final class Builder {
        private final String baseSelector;
        private final List<CssField> fields;
        private String name;
        private String targetField;

        private Builder(String baseSelector, List<CssField> fields) {
            this.baseSelector = baseSelector;
            this.fields = fields;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder targetField(String targetField) {
            this.targetField = targetField;
            return this;
        }

        public CssSchema build() {
            return new CssSchema(this);
        }
    }
}
