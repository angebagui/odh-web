package models.enums;

public enum Mime {

    ADOBE_PDF("application/pdf"),
    // GOOGLE_DOCS_SPREADSHEET("application/vnd.google-apps.spreadsheet"),
    GOOGLE_DOCS_DOCUMENT("application/vnd.google-apps.document"), GOOGLE_DOCS_PRESENTATION("application/vnd.google-apps.presentation"), MS_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    // MS_EXCEL("application/vnd.ms-excel"),
    MS_POWERPOINT("application/vnd.ms-powerpoint"), MS_WORD("application/msword"), OPENDOCS_DOCUMENT("application/vnd.oasis.opendocument.text"),
    // OPENDOCS_SPREADSHEET("application/vnd.oasis.opendocument.spreadsheet"),
    // OPENDOCS_SPREADSHEET_X("application/x-vnd.oasis.opendocument.spreadsheet"),
    OPENDOCS_PRESENTATION("application/vnd.openxmlformats-officedocument.presentationml.presentation"), TEXT_HTML("text/html"), TEXT_PLAIN("text/plain"), TEXT_RTF("application/rtf");

    private String type;

    Mime(String type) {
        this.type = type;
    }

    public String getHumanFriendlyName() {
        if (this.equals(ADOBE_PDF)) {
            return ".pdf";
        } else if (this.equals(MS_DOCX)) {
            return ".docx";
        } else if (this.equals(MS_WORD)) {
            return ".doc";
        } else if (this.equals(MS_POWERPOINT)) {
            return ".ppt";
        } else if (this.equals(TEXT_PLAIN)) {
            return ".txt";
        } else if (this.equals(TEXT_HTML)) {
            return ".html";
        } else if (this.equals(TEXT_RTF)) {
            return ".rtf";
        } else if (this.equals(OPENDOCS_DOCUMENT)) {
            return ".ods";
        } else if (this.equals(OPENDOCS_PRESENTATION)) {
            return ".pptx";
        } else {
            return "Document";
        }
    }

    public String getType() {
        return this.type;
    }

    public boolean isDocument() {
        return (this.equals(MS_WORD) || this.equals(GOOGLE_DOCS_DOCUMENT) || this.equals(OPENDOCS_DOCUMENT) || this.equals(TEXT_RTF) || this.equals(TEXT_PLAIN));
    }

    public boolean isPresentation() {
        return (this.equals(MS_POWERPOINT) || this.equals(GOOGLE_DOCS_PRESENTATION) || this.equals(OPENDOCS_PRESENTATION));
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.getType();
    }

    public static Mime parseName(String name) {
        Mime type = null;
        for (Mime item : Mime.values()) {
            if (item.getType().equals(name)) {
                type = item;
                break;
            }
        }
        return type;
    }

}
