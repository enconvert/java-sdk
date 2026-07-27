package com.enconvert.model;

/** PDF rendering options for url-to-pdf, website-to-pdf, and document conversions. */
public final class PdfOptions {

    private final String pageSize;
    private final Double pageWidth;
    private final Double pageHeight;
    private final String orientation;
    private final PdfMargins margins;
    private final Double scale;
    private final Boolean grayscale;
    private final PdfHeaderFooter header;
    private final PdfHeaderFooter footer;

    private PdfOptions(Builder b) {
        this.pageSize = b.pageSize;
        this.pageWidth = b.pageWidth;
        this.pageHeight = b.pageHeight;
        this.orientation = b.orientation;
        this.margins = b.margins;
        this.scale = b.scale;
        this.grayscale = b.grayscale;
        this.header = b.header;
        this.footer = b.footer;
    }

    public String pageSize() {
        return pageSize;
    }

    /** Custom page width; overrides pageSize when set together with pageHeight. */
    public Double pageWidth() {
        return pageWidth;
    }

    /** Custom page height; overrides pageSize when set together with pageWidth. */
    public Double pageHeight() {
        return pageHeight;
    }

    /** "portrait" or "landscape". */
    public String orientation() {
        return orientation;
    }

    public PdfMargins margins() {
        return margins;
    }

    public Double scale() {
        return scale;
    }

    public Boolean grayscale() {
        return grayscale;
    }

    public PdfHeaderFooter header() {
        return header;
    }

    public PdfHeaderFooter footer() {
        return footer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String pageSize;
        private Double pageWidth;
        private Double pageHeight;
        private String orientation;
        private PdfMargins margins;
        private Double scale;
        private Boolean grayscale;
        private PdfHeaderFooter header;
        private PdfHeaderFooter footer;

        private Builder() {
        }

        public Builder pageSize(String pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder pageWidth(double pageWidth) {
            this.pageWidth = pageWidth;
            return this;
        }

        public Builder pageHeight(double pageHeight) {
            this.pageHeight = pageHeight;
            return this;
        }

        public Builder orientation(String orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder margins(PdfMargins margins) {
            this.margins = margins;
            return this;
        }

        public Builder scale(double scale) {
            this.scale = scale;
            return this;
        }

        public Builder grayscale(boolean grayscale) {
            this.grayscale = grayscale;
            return this;
        }

        public Builder header(PdfHeaderFooter header) {
            this.header = header;
            return this;
        }

        public Builder footer(PdfHeaderFooter footer) {
            this.footer = footer;
            return this;
        }

        public PdfOptions build() {
            return new PdfOptions(this);
        }
    }
}
