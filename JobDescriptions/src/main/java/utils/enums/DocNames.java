package utils.enums;

import lombok.Getter;

public enum DocNames {
    INPUT_WORD_DOC_NAME("InputFile.docx"),
    OUTPUT_WORD_DOC_NAME("OutputFile.docx"),
    TEMPLATE_WORD_DOC_NAME("TemplateFile.docx"),
    COPIED_TEMPLATE_WORD_DOC_NAME("CopiedTemplateFile.docx"),
    CONVERTED_TXT_FILE_NAME("Text.txt"),
    FORMATTED_TXT_FILE_NAME("FormattedText.txt");

    @Getter
    private final String value;

    DocNames(String value) {
        this.value = value;
    }
}
