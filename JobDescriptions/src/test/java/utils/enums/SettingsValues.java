package utils.enums;

import lombok.Getter;

public enum SettingsValues {
    FOLDER("/folder");

    @Getter
    private final String value;

    SettingsValues(String value) {
        this.value = value;
    }
}
