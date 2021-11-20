package lsieun.drawing.theme.table;

import lsieun.drawing.canvas.Canvas;
import lsieun.drawing.canvas.Drawable;
import lsieun.drawing.canvas.TextAlign;

public class OneLineTable implements Drawable {
    public final String[][] matrix;
    public final TextAlign align;

    private final int row_padding;
    private final int col_padding;

    public OneLineTable(String[][] matrix, TextAlign align) {
        this(matrix, align, 0, 3);
    }

    public OneLineTable(String[][] matrix, TextAlign align, int row_padding, int col_padding) {
        this.matrix = matrix;
        this.align = align;
        this.row_padding = row_padding;
        this.col_padding = col_padding;
    }

    @Override
    public void draw(Canvas canvas, int startRow, int startCol) {
        int rowCount = matrix.length;
        int colCount = matrix[0].length;

        int[] rowHeightArray = new int[rowCount];
        int[] colWidthArray = new int[colCount];

        for (int i = 0; i < rowCount; i++) {
            rowHeightArray[i] = 1 + 2 * row_padding;
        }
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                String item = matrix[i][j];
                if (item == null) continue;
                int length = item.length() + 2 * col_padding;
                if (length > colWidthArray[j]) {
                    colWidthArray[j] = length;
                }
            }
        }

        // draw border
        canvas.moveTo(startRow, startCol);
        canvas.drawTable(rowHeightArray, colWidthArray);

        // draw text
        int currentRow = startRow;
        for (int i = 0; i < rowCount; i++) {
            if (i > 0) {
                currentRow += rowHeightArray[i - 1] + 1;
            }

            int currentCol = startCol;
            for (int j = 0; j < colCount; j++) {
                if (j > 0) {
                    currentCol += colWidthArray[j - 1] + 1;
                }

                String item = matrix[i][j];
                if (item == null) item = "";

                int currentWidth = colWidthArray[j];

                canvas.moveTo(currentRow + 1 + row_padding, currentCol);
                switch (align) {
                    case LEFT: {
                        int padding = col_padding + 1;
                        canvas.right(padding);
                        canvas.drawText(item);
                        break;
                    }
                    case CENTER: {
                        int padding = (currentWidth - item.length()) / 2 + 1;
                        canvas.right(padding);
                        canvas.drawText(item);
                        break;
                    }
                    case RIGHT: {
                        int padding = currentWidth - col_padding - item.length() + 1;
                        canvas.right(padding);
                        canvas.drawText(item);
                        break;
                    }
                    default:
                        assert false : "impossible";
                }
            }

        }
    }
}
