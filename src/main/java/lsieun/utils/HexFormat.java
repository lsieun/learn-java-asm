package lsieun.utils;

public enum HexFormat {
    FORMAT_FF_FF("", 0),
    FORMAT_FF_FF_8("", 8),
    FORMAT_FF_FF_16("", 16),
    FORMAT_FF_FF_32("", 32),

    FORMAT_FF_SPACE_FF(" ", 0),
    FORMAT_FF_SPACE_FF_8(" ", 8),
    FORMAT_FF_SPACE_FF_16(" ", 16),
    FORMAT_FF_SPACE_FF_32(" ", 32),

    FORMAT_FF_COLON_FF(":", 0),
    FORMAT_FF_COLON_FF_8(":", 8),
    FORMAT_FF_COLON_FF_16(":", 16),
    FORMAT_FF_COLON_FF_32(":", 32),
    ;

    public final String separator;
    public final int columns;

    HexFormat(String separator, int columns) {
        this.separator = separator;
        this.columns = columns;
    }
}
