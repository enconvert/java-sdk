package com.enconvert.model;

/** Options for {@code Enconvert#convertImage}. */
public final class ConvertImageOptions {

    private final String outputFormat;
    private final String saveTo;
    private final String outputFilename;

    private ConvertImageOptions(Builder b) {
        this.outputFormat = b.outputFormat;
        this.saveTo = b.saveTo;
        this.outputFilename = b.outputFilename;
    }

    /** Target format, e.g. "webp", "png", "jpeg", "svg", "heic". Required. */
    public String outputFormat() {
        return outputFormat;
    }

    public String saveTo() {
        return saveTo;
    }

    public String outputFilename() {
        return outputFilename;
    }

    /** {@code outputFormat} is required. */
    public static Builder builder(String outputFormat) {
        return new Builder(outputFormat);
    }

    public static final class Builder {
        private final String outputFormat;
        private String saveTo;
        private String outputFilename;

        private Builder(String outputFormat) {
            this.outputFormat = outputFormat;
        }

        public Builder saveTo(String saveTo) {
            this.saveTo = saveTo;
            return this;
        }

        public Builder outputFilename(String outputFilename) {
            this.outputFilename = outputFilename;
            return this;
        }

        public ConvertImageOptions build() {
            return new ConvertImageOptions(this);
        }
    }
}
