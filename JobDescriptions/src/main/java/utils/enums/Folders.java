package utils.enums;

import lombok.Getter;

public enum Folders {
    FOLDER_FOR_CONVERTED_TEXT_FILES("../ConvertedTextFiles/"),
    FOLDER_FOR_FORMATTED_FILES("../FormattedFiles/"),
    FOLDER_FOR_FORMATTED_TEXT_FILES("../FormattedTextFiles/"),
    FOLDER_FOR_ORIGINAL_FILES("../OriginalFiles/"),
    FOLDER_FOR_TEMPLATES_FILES("../TemplateFiles/");

    @Getter
    private final String value;

    Folders(String value) {
        this.value = value;
    }
}
