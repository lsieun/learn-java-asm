package lsieun.drawing.theme.shape;

import lsieun.drawing.canvas.Canvas;
import lsieun.drawing.canvas.Drawable;
import lsieun.drawing.canvas.TextAlign;

import java.util.List;

public class RectangleWithText extends Rectangle implements Drawable {
    public final List<String> lines;
    public final TextAlign align;


    public RectangleWithText(int width, int height, List<String> lines, TextAlign align) {
        super(width, height);
        this.lines = lines;
        this.align = align;
    }

    @SuppressWarnings({"UnnecessaryLocalVariable"})
    @Override
    public void draw(Canvas canvas, int startRow, int startCol) {
        // draw border
        super.draw(canvas, startRow, startCol);

        int left = startCol;
        int right = left + width + 1;
        int top = startRow;

        // text
        if (lines == null) return;
        int size = Math.min(lines.size(), height);
        int length = width - 2;
        if (length < 1) return;
        for (int i = 0; i < size; i++) {
            String item = lines.get(i);
            if (item == null) continue;
            if (item.length() > length) {
                item = item.substring(0, length);
            }
            int row = top + i + 1;
            switch (align) {
                case LEFT: {
                    canvas.moveTo(row, left + 2);
                    canvas.drawText(item);
                    break;
                }
                case CENTER: {
                    canvas.moveTo(row, left + 2 + (length - item.length()) / 2);
                    canvas.drawText(item);
                    break;
                }
                case RIGHT: {
                    canvas.moveTo(row, right - 1 - item.length());
                    canvas.drawText(item);
                    break;
                }
                default:
                    throw new RuntimeException("unsupported align: " + align);
            }
        }

    }
}
