package lsieun.utils;

import java.util.Formatter;

public class HexUtils {

    public static String format(byte[] bytes, HexFormat format) {
        String separator = format.separator;
        int bytes_column = format.columns;
        return format(bytes, separator, bytes_column);
    }

    public static String format(byte[] bytes, String separator, int bytes_column) {
        if (bytes == null || bytes.length < 1) return "";

        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);

        int length = bytes.length;
        for (int i = 0; i < length - 1; i++) {
            int val = bytes[i] & 0xFF;
            fm.format("%02X", val);
            if (bytes_column > 0 && (i + 1) % bytes_column == 0) {
                fm.format("%n");
            } else {
                fm.format("%s", separator);
            }
        }
        {
            int val = bytes[length - 1] & 0xFF;
            fm.format("%02X", val);
        }

        return sb.toString();
    }

    public static byte[] parse(String str, HexFormat format) {
        char[] chars = format.separator.toCharArray();
        return parse(str, chars);
    }

    public static byte[] parse(String str, char[] chars) {
        int length = str.length();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            if (is_in(ch, chars)) {
                continue;
            }
            sb.append(ch);
        }
        String hex_str = sb.toString();
        return parse(hex_str);
    }

    public static boolean is_in(char ch, char[] chars) {
        for (char item : chars) {
            if (item == ch) {
                return true;
            }
        }
        return false;
    }

    public static byte[] parse(String hex_str) {
        int length = hex_str.length();
        int count = length / 2;

        byte[] bytes = new byte[count];
        for (int i = 0; i < count; i++) {
            String item = hex_str.substring(2 * i, 2 * i + 2);
            int val = Integer.parseInt(item, 16);
            bytes[i] = (byte) val;
        }
        return bytes;
    }

    public static String toHex(byte[] bytes) {
        return format(bytes, HexFormat.FORMAT_FF_FF);
    }
}
