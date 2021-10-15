package lsieun.utils;

import lsieun.asm.analysis.InsnText;
import lsieun.asm.analysis.cfg.ControlFlowGraph;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.List;

public class BoxDrawingUtils {
    public static final String EMPTY = "";
    public static final String SPACE = " ";

    // Box-drawing character
    public static final String LIGHT_HORIZONTAL = "─";
    public static final String LIGHT_VERTICAL = "│";
    public static final String LIGHT_DOWN_AND_RIGHT = "┌";
    public static final String LIGHT_DOWN_AND_LEFT = "┐";
    public static final String LIGHT_UP_AND_RIGHT = "└";
    public static final String LIGHT_UP_AND_LEFT = "┘";
    public static final String LIGHT_VERTICAL_AND_RIGHT = "├";
    public static final String LIGHT_VERTICAL_AND_LEFT = "┤";
    public static final String LIGHT_DOWN_AND_HORIZONTAL = "┬";
    public static final String LIGHT_UP_AND_HORIZONTAL = "┴";
    public static final String LIGHT_VERTICAL_AND_HORIZONTAL = "┼";
    public static final String LIGHT_DIAGONAL_CROSS = "╳";


    public static String getBlank(int n) {
        return getItem(0, EMPTY, n, SPACE, 0, EMPTY);
    }

    public static String getRight(int middle, String middleItem, int right, String rightItem) {
        return getItem(0, EMPTY, middle, middleItem, right, rightItem);
    }

    public static String getItem(int left, String leftItem, int middle, String middleItem, int right, String rightItem) {
        return getItem(EMPTY, left, leftItem, middle, middleItem, right, rightItem, EMPTY);
    }

    public static String getItem(String prefix, int left, String leftItem, int middle, String middleItem, int right, String rightItem, String suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        for (int i = 0; i < left; i++) {
            sb.append(leftItem);
        }
        for (int i = 0; i < middle; i++) {
            sb.append(middleItem);
        }
        for (int i = 0; i < right; i++) {
            sb.append(rightItem);
        }
        sb.append(suffix);
        return sb.toString();
    }

    public static void printInstructionLinks(InsnList instructions, int[] array) {
        if (array == null || array.length < 1) {
            return;
        }

        InsnText insnText = new InsnText();
        int n = 5;

        int size = instructions.size();

        int length = array.length;
        int min = array[0];
        int max = array[length - 1];
        for (int i = 0; i < size; i++) {
            AbstractInsnNode node = instructions.get(i);


            String firstPart;
            if (i < min || i > max) {
                firstPart = BoxDrawingUtils.getBlank(n);
            }
            else if ((min == max)) {
                // NOTE: 如果min和max相等，那么 i == min == max。
                //       由于第1个条件的判断，此时min < i < max，再加上min == max，所以i == min == max。
                firstPart = BoxDrawingUtils.getRight(1, LIGHT_DIAGONAL_CROSS, n - 1, LIGHT_HORIZONTAL);
            }
            else if (i == min) {
                firstPart = BoxDrawingUtils.getRight(1, LIGHT_DOWN_AND_RIGHT, n - 1, LIGHT_HORIZONTAL);
            }
            else if (i == max) {
                firstPart = BoxDrawingUtils.getRight(1, LIGHT_UP_AND_RIGHT, n - 1, LIGHT_HORIZONTAL);
            }
            else if (contains(array, i)) {
                firstPart = BoxDrawingUtils.getRight(1, LIGHT_VERTICAL_AND_RIGHT, n - 1, LIGHT_HORIZONTAL);
            }
            else {
                firstPart = BoxDrawingUtils.getRight(1, LIGHT_VERTICAL, n - 1, SPACE);
            }

            List<String> lines = insnText.toLines(node);
            String secondPart;
            {
                secondPart = lines.get(0);
                String format1 = "%s %03d: %s";

                String message = String.format(format1, firstPart, i, secondPart);
                System.out.println(message);
            }

            if (lines.size() > 1) {
                String format2 = "%s %4s %s";
                if (i >= min && i <= max) {
                    firstPart = BoxDrawingUtils.getRight(1, LIGHT_VERTICAL, n - 1, SPACE);
                }
                else {
                    firstPart = BoxDrawingUtils.getBlank(n);
                }

                for (int j = 1; j < lines.size(); j++) {
                    secondPart = lines.get(j);
                    String message = String.format(format2, firstPart, EMPTY, secondPart);
                    System.out.println(message);
                }
            }

        }
    }

    private static boolean contains(int[] array, int val) {
        for (int item : array) {
            if (item == val) {
                return true;
            }
        }
        return false;
    }

    public static void printTable(String[][] matrix) {
        if (matrix == null) return;

        int row = matrix.length;
        int column = matrix[0].length;
        if (column < 1) return;

        int[] cellWidthArray = new int[column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                String val = matrix[i][j];
                if (val == null) continue;
                int length = val.length();
                if (length > cellWidthArray[j]) {
                    cellWidthArray[j] = length;
                }
            }
        }

        {
            String firstLine = getTableCellBorder(cellWidthArray, LIGHT_DOWN_AND_RIGHT, LIGHT_DOWN_AND_HORIZONTAL, LIGHT_DOWN_AND_LEFT);
            System.out.println(firstLine);
        }
        for (int i = 0; i < row - 1; i++) {
            String[] values = matrix[i];
            String cellValue = getTableCellValue(cellWidthArray, values);
            System.out.println(cellValue);
            String middleLine = getTableCellBorder(cellWidthArray, LIGHT_VERTICAL_AND_RIGHT, LIGHT_VERTICAL_AND_HORIZONTAL, LIGHT_VERTICAL_AND_LEFT);
            System.out.println(middleLine);
        }
        {
            String[] values = matrix[row - 1];
            String cellValue = getTableCellValue(cellWidthArray, values);
            System.out.println(cellValue);
            String lastLine = getTableCellBorder(cellWidthArray, LIGHT_UP_AND_RIGHT, LIGHT_UP_AND_HORIZONTAL, LIGHT_UP_AND_LEFT);
            System.out.println(lastLine);
        }
    }

    private static String getTableCellBorder(int[] cellWidthArray, String leftItem, String middleItem, String rightItem) {
        int length = cellWidthArray.length;

        StringBuilder sb = new StringBuilder();
        sb.append(leftItem);
        for (int i = 0; i < length - 1; i++) {
            int cellWidth = cellWidthArray[i] + 2;
            for (int j = 0; j < cellWidth; j++) {
                sb.append(LIGHT_HORIZONTAL);
            }
            sb.append(middleItem);
        }

        {
            int cellWidth = cellWidthArray[length - 1] + 2;
            for (int j = 0; j < cellWidth; j++) {
                sb.append(LIGHT_HORIZONTAL);
            }
            sb.append(rightItem);
        }

        return sb.toString();
    }

    private static String getTableCellValue(int[] cellWidthArray, String[] values) {
        int length = cellWidthArray.length;
        StringBuilder sb = new StringBuilder();
        sb.append(LIGHT_VERTICAL);

        for (int i = 0; i < length - 1; i++) {
            int expectedCellWidth = cellWidthArray[i] + 2;
            String val = values[i];
            int valLength = val.length();
            int leftBlank = (expectedCellWidth - valLength) / 2;
            for (int j = 0; j < leftBlank; j++) {
                sb.append(SPACE);
            }
            sb.append(val);
            int rightBlank = expectedCellWidth - leftBlank - valLength;
            for (int j = 0; j < rightBlank; j++) {
                sb.append(SPACE);
            }
            sb.append(LIGHT_VERTICAL);
        }

        {
            int expectedCellWidth = cellWidthArray[length - 1] + 2;
            String val = values[length - 1];
            int valLength = val.length();
            int leftBlank = (expectedCellWidth - valLength) / 2;
            for (int j = 0; j < leftBlank; j++) {
                sb.append(SPACE);
            }
            sb.append(val);
            int rightBlank = expectedCellWidth - leftBlank - valLength;
            for (int j = 0; j < rightBlank; j++) {
                sb.append(SPACE);
            }
            sb.append(LIGHT_VERTICAL);
        }
        return sb.toString();
    }

    public static void printCFG(InsnList instructions, ControlFlowGraph cfg) {
        //
    }
}
