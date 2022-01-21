package utils.enums;

import lombok.Getter;

public enum DocNames {
    TEMPLATE_WORD_DOC_NAME("TemplateFile.docx"),
    COPIED_TEMPLATE_WORD_DOC_NAME("CopiedTemplateFile.docx");

    @Getter
    private final String value;

    DocNames(String value) {
        this.value = value;
    }
}
