package lsieun.asm.analysis.graph;

import lsieun.graphics.Rectangle;
import lsieun.graphics.Text;

public class InsnBox {
    private static final int INNER_PADDING = 10;
    private static final int RECTANGLE_WIDTH = 250;

    // 位置信息
    public int x;
    public int y;

    // 图形信息
    public Rectangle rectangle;

    public final InsnBlock block;

    public InsnBox(InsnBlock block) {
        this.block = block;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public void draw(int x, int y) {
        this.x = x;
        this.y = y;

        int currentX = x + INNER_PADDING;
        int currentY = y + INNER_PADDING;

        int currentWidth = 0;
        int currentHeight = 0;
        for (String item : block.lines) {
            Text text = new Text(currentX, currentY, item);
            text.draw();

            int textWidth = text.getWidth() + 2 * INNER_PADDING;
            if (textWidth > currentWidth) {
                currentWidth = textWidth;
            }
            currentY += text.getHeight();
            currentHeight = currentY - y;
        }

//        int width = Math.max(RECTANGLE_WIDTH, currentWidth);
        int width = RECTANGLE_WIDTH;
        int height = currentHeight + INNER_PADDING;
        rectangle = new Rectangle(x, y, width, height);
        rectangle.draw();
    }

    public int getWidth() {
        if (rectangle != null) {
            return rectangle.getWidth();
        }
        else {
            return 0;
        }
    }

    public int getHeight() {
        if (rectangle != null) {
            return rectangle.getHeight();
        }
        else {
            return 0;
        }
    }
}
