package utils.enums;

import lombok.Getter;

public enum Separators {
    DOT("."),
    COMMA(","),
    COLON(":"),
    SEMICOLON(";"),

    SPACE(" "),
    NOTHING("");

    @Getter
    private final String value;

    Separators(String value) {
        this.value = value;
    }
}
