package lsieun.utils;

import lsieun.asm.analysis.InsnText;
import lsieun.drawing.canvas.BoxDrawing;
import lsieun.drawing.canvas.Canvas;
import lsieun.drawing.canvas.Drawable;
import lsieun.drawing.canvas.TextAlign;
import lsieun.drawing.theme.table.OneLineTable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.List;

public class BoxDrawingUtils {
    public static final String EMPTY = "";

    public static void printInstructionLinks(InsnList instructions, int[] array) {
        if (array == null || array.length < 1) {
            return;
        }

        InsnText insnText = new InsnText();
        int n = 5;

        Canvas canvas = new Canvas();
        int currentRow = 0;

        int size = instructions.size();

        int length = array.length;
        int min = array[0];
        int max = array[length - 1];
        for (int i = 0; i < size; i++) {
            AbstractInsnNode node = instructions.get(i);

            canvas.moveTo(currentRow, 0);
            if (i < min || i > max) {
                canvas.drawPixel(BoxDrawing.SPACE);
            }
            else if ((min == max)) {
                // NOTE: 如果min和max相等，那么 i == min == max。
                //       由于第1个条件的判断，此时min < i < max，再加上min == max，所以i == min == max。
                canvas.drawPixel(BoxDrawing.LIGHT_VERTICAL_AND_RIGHT);
                canvas.right(1);
                canvas.drawHorizontalLine(n - 1);
            }
            else if (i == min) {
                canvas.drawPixel(BoxDrawing.LIGHT_DOWN_AND_RIGHT);
                canvas.right(1);
                canvas.drawHorizontalLine(n - 1);
            }
            else if (i == max) {
                canvas.drawPixel(BoxDrawing.LIGHT_UP_AND_RIGHT);
                canvas.right(1);
                canvas.drawHorizontalLine(n - 1);
            }
            else if (contains(array, i)) {
                canvas.drawPixel(BoxDrawing.LIGHT_VERTICAL_AND_RIGHT);
                canvas.right(1);
                canvas.drawHorizontalLine(n - 1);
            }
            else {
                canvas.drawPixel(BoxDrawing.LIGHT_VERTICAL);
            }

            List<String> lines = insnText.toLines(node);
            String secondPart;
            {
                secondPart = lines.get(0);
                String format1 = "%03d: %s";
                String message = String.format(format1, i, secondPart);

                canvas.moveTo(currentRow, 0);
                canvas.right(5);
                canvas.drawText(message);
            }

            if (lines.size() > 1) {
                BoxDrawing ch;
                String format2 = "%4s %s";
                if (i >= min && i <= max) {
                    ch = BoxDrawing.LIGHT_VERTICAL;
                }
                else {
                    ch = BoxDrawing.SPACE;
                }

                for (int j = 1; j < lines.size(); j++) {
                    secondPart = lines.get(j);
                    String message = String.format(format2, EMPTY, secondPart);
                    currentRow++;
                    canvas.moveTo(currentRow, 0);
                    canvas.drawPixel(ch);
                    canvas.right(5);
                    canvas.drawText(message);
                }
            }

            currentRow++;
        }

        System.out.println(canvas);
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
        Drawable table = new OneLineTable(matrix, TextAlign.CENTER);
        Canvas canvas = new Canvas();
        canvas.draw(0, 0, table);
        System.out.println(canvas);
    }

}
