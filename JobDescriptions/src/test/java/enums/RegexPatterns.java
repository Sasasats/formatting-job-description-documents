package enums;

import lombok.Getter;

public enum RegexPatterns {
    CORRECT_LIST_LVL_1("\\d\\.\\s[А-Яа-я]*"),
    LIST_LVL_11("\\d\\s[А-Яа-я]*"),
    LIST_LVL_12("\\d\\.[А-Яа-я]*"),
    LIST_LVL_13("\\d[А-Яа-я]*"),

    LIST_LVL_21("\\d\\.\\d\\s[А-Яа-я]*"),
    LIST_LVL_22("\\d\\.\\d\\.[А-Яа-я]*"),
    LIST_LVL_23("\\d\\.\\d[А-Яа-я]*"),
    LIST_LVL_211("\\d\\.\\d\\d\\s[А-Яа-я]*"),
    LIST_LVL_221("\\d\\.\\d\\d\\.[А-Яа-я]*"),
    LIST_LVL_231("\\d\\.\\d\\d[А-Яа-я]*"),

    LIST_LVL_31("\\d\\.\\d\\.\\d\\s[А-Яа-я]*"),
    LIST_LVL_32("\\d\\.\\d\\.\\d\\.[А-Яа-я]*"),
    LIST_LVL_33("\\d\\.\\d\\.\\d[А-Яа-я]*"),
    LIST_LVL_311("\\d\\.\\d\\d\\.\\d\\s[А-Яа-я]*"),
    LIST_LVL_321("\\d\\.\\d\\d\\.\\d\\.[А-Яа-я]*"),
    LIST_LVL_331("\\d\\.\\d\\d\\.\\d[А-Яа-я]*"),
    LIST_LVL_312("\\d\\.\\d\\.\\d\\d\\s[А-Яа-я]*"),
    LIST_LVL_322("\\d\\.\\d\\.\\d\\d\\.[А-Яа-я]*"),
    LIST_LVL_332("\\d\\.\\d\\.\\d\\d[А-Яа-я]*"),
    LIST_LVL_313("\\d\\.\\d\\d\\.\\d\\d\\s[А-Яа-я]*"),
    LIST_LVL_323("\\d\\.\\d\\d\\.\\d\\d\\.[А-Яа-я]*"),
    LIST_LVL_333("\\d\\.\\d\\d\\.\\d\\d[А-Яа-я]*");

    @Getter
    private final String value;

    RegexPatterns(String value){
        this.value = value;
    }

}
