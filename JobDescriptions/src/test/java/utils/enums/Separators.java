package utils.enums;

import lombok.Getter;

public enum Separators {
    SPACE(" "),
    DOT("."),
    DOT_AND_SPACE(". ");

    @Getter
    private final String value;

    Separators(String value) {
        this.value = value;
    }
}
