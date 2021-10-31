package lsieun.utils;

import lsieun.cst.Const;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

public class TextCanvas {
    private static final int ROW_PADDING = 0;
    private static final int COL_PADDING = 1;

    private final List<TextPixel> pixelList = new ArrayList<>();


    public void drawHorizontalLine(int rowStart, int colStart, int horizontalCount) {
        int colStop = colStart + horizontalCount - 1;

        for (int col = colStart; col <= colStop; col++) {
            mergePixel(rowStart, col, BoxDrawing.LIGHT_HORIZONTAL.val);
        }
    }

    public void drawVerticalLine(int rowStart, int colStart, int verticalCount) {
        int rowStop = rowStart + verticalCount - 1;

        for (int row = rowStart; row <= rowStop; row++) {
            mergePixel(row, colStart, BoxDrawing.LIGHT_VERTICAL.val);
        }
    }

    public void drawRectangle(int rowStart, int colStart, int rowCount, int colCount) {
        int rowStop = rowStart + rowCount - 1;
        int colStop = colStart + colCount - 1;

        pixelList.add(TextPixel.valueOf(rowStart, colStart, BoxDrawing.LIGHT_DOWN_AND_RIGHT.val));
        pixelList.add(TextPixel.valueOf(rowStart, colStop, BoxDrawing.LIGHT_DOWN_AND_LEFT.val));
        pixelList.add(TextPixel.valueOf(rowStop, colStart, BoxDrawing.LIGHT_UP_AND_RIGHT.val));
        pixelList.add(TextPixel.valueOf(rowStop, colStop, BoxDrawing.LIGHT_UP_AND_LEFT.val));

        drawHorizontalLine(rowStart, colStart + 1, colCount - 2);
        drawHorizontalLine(rowStop, colStart + 1, colCount - 2);

        drawVerticalLine(rowStart + 1, colStart, rowCount - 2);
        drawVerticalLine(rowStart + 1, colStop, rowCount - 2);

        Collections.sort(pixelList);
    }

    public void drawText(int rowStart, int colStart, String text) {
        int length = text.length();
        for (int i = 0; i < length; i++) {
            String ch = text.substring(i, i + 1);
            setPixel(rowStart, colStart + i, ch);
        }
    }

    public void drawMultiLineText(int rowStart, int colStart, List<String> textList) {
        int size = textList.size();
        for (int i = 0; i < size; i++) {
            String text = textList.get(i);
            drawText(rowStart + i, colStart, text);
        }
    }

    public void drawMultiLineTextWithBorder(int rowStart, int colStart, List<String> textList) {
        int maxStringLength = findMaxStringLength(textList);
        int rowCount = textList.size() + 2 * ROW_PADDING + 2;
        int colCount = maxStringLength + 2 * COL_PADDING + 2/*two border*/;
        drawRectangle(rowStart, colStart, rowCount, colCount);
        drawMultiLineText(rowStart + ROW_PADDING + 1, colStart + COL_PADDING + 1, textList);
    }

    private int findMaxStringLength(List<String> textList) {
        int maxLength = 0;
        for (String text : textList) {
            int length = text.length();
            if (length > maxLength) {
                maxLength = length;
            }
        }
        return maxLength;
    }

    public void setPixel(int row, int col, String value) {
        String firstChar = value.substring(0, 1);
        TextPixel pixel = findPixel(row, col);
        if (pixel != null) {
            pixel.value = firstChar;
        }
        else {
            pixel = TextPixel.valueOf(row, col, firstChar);
            pixelList.add(pixel);
            Collections.sort(pixelList);
        }
    }

    public void mergePixel(int row, int col, String value) {
        String firstChar = value.substring(0, 1);
        TextPixel pixel = findPixel(row, col);
        if (pixel != null) {
            if (BoxDrawing.isValid(pixel.value) && BoxDrawing.isValid(value)) {
                pixel.value = BoxDrawing.merge(pixel.value, firstChar).val;
            }
            else {
                pixel.value = firstChar;
            }
        }
        else {
            pixel = TextPixel.valueOf(row, col, firstChar);
            pixelList.add(pixel);
            Collections.sort(pixelList);
        }
    }

    private TextPixel findPixel(int row, int col) {
        for (TextPixel item : pixelList) {
            if (item.row == row && item.col == col) {
                return item;
            }
        }
        return null;
    }

    public List<String> getLines() {
        List<String> lines = new ArrayList<>();
        Collections.sort(pixelList);

        int maxRow = findMaxRow(pixelList);
        for (int row = 0; row <= maxRow; row++) {
            List<TextPixel> rowList = findRowItems(row);
            int maxCol = findMaxCol(rowList);
            int i = 0;
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col <= maxCol; col++) {

                TextPixel item = null;
                if (i < rowList.size()) {
                    item = rowList.get(i);
                }

                if (item != null && item.col == col) {
                    sb.append(item.value);
                    i++;
                }
                else {
                    sb.append(BoxDrawing.SPACE.val);
                }
            }
            String line = sb.toString();
            lines.add(line);
        }
        return lines;
    }

    private int findMaxRow(List<TextPixel> list) {
        int maxRow = 0;
        for (TextPixel item : list) {
            if (item.row > maxRow) {
                maxRow = item.row;
            }
        }
        return maxRow;
    }

    private int findMaxCol(List<TextPixel> list) {
        int maxCol = 0;
        for (TextPixel item : list) {
            if (item.col > maxCol) {
                maxCol = item.col;
            }
        }
        return maxCol;
    }

    private List<TextPixel> findRowItems(int row) {
        List<TextPixel> list = new ArrayList<>();
        for (TextPixel item : pixelList) {
            if (item.row == row) {
                list.add(item);
            }
        }
        return list;
    }

    public void printPixels() {
        if (Const.DEBUG) {
            StringBuilder sb = new StringBuilder();
            Formatter fm = new Formatter(sb);
            for (TextPixel pixel : pixelList) {
                fm.format(Const.DEBUG_FORMAT, pixel);
            }
            System.out.println(sb);
        }
    }

    public static void main(String[] args) {
        TextCanvas canvas = new TextCanvas();

        // 画线
        canvas.drawHorizontalLine(4, 5, 30);
        canvas.drawVerticalLine(1, 20, 6);

        // 画矩形框
        canvas.drawRectangle(2, 10, 5, 20);

        // 单选文本
        canvas.drawText(8, 0, "You know some birds are not meant to be caged, their feathers are just too bright.");

        // 多行文本
        List<String> textList = new ArrayList<>();
        textList.add("I love three things in the world: the sun, the moon, and you.");
        textList.add("The sun for the day, the moon for the night, and you forever.");
        canvas.drawMultiLineText(10, 5, textList);

        // 多行文本+边框
        canvas.drawMultiLineTextWithBorder(13, 10, textList);

        canvas.printPixels();
        List<String> lines = canvas.getLines();
        lines.forEach(System.out::println);
    }

}
