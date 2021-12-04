package lsieun.drawing.theme.table;

import lsieun.drawing.canvas.Drawable;
import lsieun.drawing.canvas.TextAlign;

public class FixedWidthOneLineTable extends OneLineTable implements Drawable {
    public final int fixedWidth;

    public FixedWidthOneLineTable(String[][] matrix, TextAlign align, int fixedWidth) {
        super(matrix, align);
        this.fixedWidth = fixedWidth;
    }

    @Override
    protected int getCellLength(int row, int col) {
        return fixedWidth;
    }
}
