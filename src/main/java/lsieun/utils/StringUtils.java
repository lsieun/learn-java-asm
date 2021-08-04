package lsieun.utils;

public class StringUtils {
    public static byte[] array2Bytes(String str) {
        String[] array = str.replace("[", "").replace("]", "").split(",");
        int length = array.length;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int val = Integer.parseInt(array[i].trim());
            bytes[i] = (byte) val;
        }
        return bytes;
    }
}
