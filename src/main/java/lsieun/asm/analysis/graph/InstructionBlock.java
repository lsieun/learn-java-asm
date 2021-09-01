package lsieun.asm.analysis.graph;

import lsieun.graphics.Rectangle;
import lsieun.graphics.Text;

import java.util.ArrayList;
import java.util.List;

public class InstructionBlock {
    private static final int INNER_PADDING = 10;
    private static final int RECTANGLE_WIDTH = 250;

    // 位置信息
    public int x;
    public int y;

    // 文字信息
    public List<String> lines;

    // 图形信息
    public Rectangle box;

    // 关联关系
    public final List<InstructionBlock> nextBlockList = new ArrayList<>();
    public final List<InstructionBlock> jumpBlockList = new ArrayList<>();

    public InstructionBlock() {
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public void draw(int x, int y) {
        this.x = x;
        this.y = y;

        int currentX = x + INNER_PADDING;
        int currentY = y + INNER_PADDING;

        int currentWidth = 0;
        int currentHeight = 0;
        for (String item : lines) {
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
        box = new Rectangle(x, y, width, height);
        box.draw();
    }

    public int getWidth() {
        if (box != null) {
            return box.getWidth();
        }
        else {
            return 0;
        }
    }

    public int getHeight() {
        if (box != null) {
            return box.getHeight();
        }
        else {
            return 0;
        }
    }
}
