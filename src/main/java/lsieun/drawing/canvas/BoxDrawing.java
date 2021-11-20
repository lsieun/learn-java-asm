package lsieun.drawing.canvas;

/**
 * <ul>
 *     <li> LEFT: 0b1000</li>
 *     <li>   UP: 0b0100</li>
 *     <li>RIGHT: 0b0010</li>
 *     <li> DOWN: 0b0001</li>
 * </ul>
 */
public enum BoxDrawing {
    SPACE(0b0000, " "),
    LIGHT_HORIZONTAL(0b1010, "─"),
    LIGHT_VERTICAL(0b0101, "│"),
    LIGHT_DOWN_AND_RIGHT(0b0011, "┌"),
    LIGHT_DOWN_AND_LEFT(0b1001, "┐"),
    LIGHT_UP_AND_RIGHT(0b0110, "└"),
    LIGHT_UP_AND_LEFT(0b1100, "┘"),
    LIGHT_VERTICAL_AND_RIGHT(0b0111, "├"),
    LIGHT_VERTICAL_AND_LEFT(0b1101, "┤"),
    LIGHT_DOWN_AND_HORIZONTAL(0b1011, "┬"),
    LIGHT_UP_AND_HORIZONTAL(0b1110, "┴"),
    LIGHT_VERTICAL_AND_HORIZONTAL(0b1111, "┼"),
    ;

    public final int flag;
    public final String val;

    BoxDrawing(int flag, String val) {
        this.flag = flag;
        this.val = val;
    }

    public BoxDrawing merge(BoxDrawing another) {
        int flag = this.flag | another.flag;
        return fromFlag(flag);
    }

    public static BoxDrawing merge(String val1, String val2) {
        BoxDrawing one = fromString(val1);
        BoxDrawing another = fromString(val2);
        return one.merge(another);
    }

    public static BoxDrawing fromString(String val) {
        BoxDrawing[] values = values();
        for (BoxDrawing item : values) {
            if (item.val.equals(val)) {
                return item;
            }
        }
        throw new RuntimeException("Unexpected Value: " + val);
    }

    public static BoxDrawing fromFlag(int flag) {
        BoxDrawing[] values = values();
        for (BoxDrawing item : values) {
            if (item.flag == flag) {
                return item;
            }
        }
        throw new RuntimeException("Unexpected flag: " + flag);
    }

    public static boolean isValid(String val) {
        BoxDrawing[] values = values();
        for (BoxDrawing item : values) {
            if (item.val.equals(val)) {
                return true;
            }
        }
        return false;
    }
}
