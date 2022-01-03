package enums;

import lombok.Getter;

public enum DocNames {
    IMPORT_WORD_DOC_NAME("test.docx"),
    CONVERTED_WORD_TO_TXT_DOC_NAME("output.txt"),
    EXPORT_TXT_DOC_NAME("test.txt");

    @Getter
    private final String value;

    DocNames(String value) {
        this.value = value;
    }
}
