package com.enconvert.model;

/** Options for {@code Enconvert#convertToPdf} (anything-to-pdf). */
public final class ConvertToPdfOptions {

    private final String saveTo;
    private final String outputFilename;
    private final PdfOptions pdfOptions;

    private ConvertToPdfOptions(Builder b) {
        this.saveTo = b.saveTo;
        this.outputFilename = b.outputFilename;
        this.pdfOptions = b.pdfOptions;
    }

    public String saveTo() {
        return saveTo;
    }

    public String outputFilename() {
        return outputFilename;
    }

    /** Only {@code grayscale} is honored by the anything-to-pdf endpoint. */
    public PdfOptions pdfOptions() {
        return pdfOptions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String saveTo;
        private String outputFilename;
        private PdfOptions pdfOptions;

        private Builder() {
        }

        public Builder saveTo(String saveTo) {
            this.saveTo = saveTo;
            return this;
        }

        public Builder outputFilename(String outputFilename) {
            this.outputFilename = outputFilename;
            return this;
        }

        public Builder pdfOptions(PdfOptions pdfOptions) {
            this.pdfOptions = pdfOptions;
            return this;
        }

        public ConvertToPdfOptions build() {
            return new ConvertToPdfOptions(this);
        }
    }
}
