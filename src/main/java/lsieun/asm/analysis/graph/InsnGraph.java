package lsieun.asm.analysis.graph;

import lsieun.cst.Const;
import lsieun.graphics.Line;
import lsieun.graphics.Rectangle;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class InsnGraph {
    private static final int START_X = 10;
    private static final int START_Y = 10;

    private static final int ROW_SPACE = 30;
    private static final int COLUMN_SPACE = 30;

    private static final int LINE_SPACE = 20;
    private static final int ARROW_LENGTH = 5;

    private final int startX;
    private final int startY;

    private final InsnBox[] boxes;

    public InsnGraph(InsnBlock[] blocks) {
        this(START_X, START_Y, blocks);
    }

    public InsnGraph(int startX, int startY, InsnBlock[] blocks) {
        this.startX = startX;
        this.startY = startY;
        int length = blocks.length;
        this.boxes = new InsnBox[length];
        for (int i = 0; i < length; i++) {
            this.boxes[i] = new InsnBox(blocks[i]);
        }

        if (Const.DEBUG) {
            printBlocks(blocks);
        }
    }

    public void draw() {
        int length = boxes.length;
        if (length < 1) return;

        drawBlockRectangles();
        drawConnectionLines();

        if (Const.DEBUG) {
            printBoxes();
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void drawBlockRectangles() {
        int currentX = this.startX;
        int currentY = this.startY;

        int length = boxes.length;
        for (int i = 0; i < length; i++) {
            InsnBox box = boxes[i];
            if (i != 0) {
                InsnBox previousBox = boxes[i - 1];
                currentY = previousBox.y + previousBox.getHeight() + ROW_SPACE;
            }

            box.draw(currentX, currentY);
        }
    }

    private void drawConnectionLines() {
        int length = boxes.length;
        for (int i = 0; i < length; i++) {
            InsnBox currentBox = boxes[i];

            List<InsnBox> nextBoxList = findNextBoxes(currentBox);
            for (InsnBox nextBox : nextBoxList) {
                connectTop2BottomBlock(currentBox.rectangle, nextBox.rectangle);
            }

            List<InsnBox> jumpBoxList = findJumpBoxes(currentBox);
            for (InsnBox jumpBox : jumpBoxList) {
                jumpOne2Another(currentBox.rectangle, jumpBox.rectangle, i);
            }
        }
    }

    private List<InsnBox> findNextBoxes(InsnBox currentBox) {
        List<InsnBox> nextBoxList = new ArrayList<>();

        List<InsnBlock> nextBlockList = currentBox.block.nextBlockList;
        if (nextBlockList.size() < 1) {
            return nextBoxList;
        }

        for (InsnBox box : boxes) {
            if (box == currentBox) continue;

            if (nextBlockList.contains(box.block)) {
                nextBoxList.add(box);
            }
        }

        return nextBoxList;
    }

    private List<InsnBox> findJumpBoxes(InsnBox currentBox) {
        List<InsnBox> jumpBoxList = new ArrayList<>();

        List<InsnBlock> jumpBlockList = currentBox.block.jumpBlockList;
        if (jumpBlockList.size() < 1) {
            return jumpBoxList;
        }

        for (InsnBox box : boxes) {
            if (box == currentBox) continue;

            if (jumpBlockList.contains(box.block)) {
                jumpBoxList.add(box);
            }
        }

        return jumpBoxList;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void connectTop2BottomBlock(Rectangle box1, Rectangle box2) {
        int x1 = box1.getX();
        int y1 = box1.getY();

        int x2 = box2.getX();
        int y2 = box2.getY();

        int x3 = x1 + box1.getWidth() / 2;
        int y3 = y1 + box1.getHeight();

        int x4 = x2 + box2.getWidth() / 2;
        int y4 = y2;
        drawLine(x3, y3, x4, y4);
        drawLine(x4 - ARROW_LENGTH, y4 - ARROW_LENGTH, x4, y4);
        drawLine(x4 + ARROW_LENGTH, y4 - ARROW_LENGTH, x4, y4);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void jumpOne2Another(Rectangle box1, Rectangle box2, int index) {
        int x1 = box1.getX();
        int y1 = box1.getY();

        int x2 = box2.getX();
        int y2 = box2.getY();

        int x3 = x1 + box1.getWidth();
        int y3 = y1 + box1.getHeight() / 2;

        int x4 = x3 + COLUMN_SPACE + index * LINE_SPACE;
        int y4 = y3;
        drawLine(x3, y3, x4, y4);
        drawLine(x4 - ARROW_LENGTH, y4 - ARROW_LENGTH, x4, y4);
        drawLine(x4 - ARROW_LENGTH, y4 + ARROW_LENGTH, x4, y4);

        int x5 = x2 + box2.getWidth();
        int y5 = y2;

        int x6 = x4;
        int y6 = y5;
        drawLine(x5, y5, x6, y6);

        drawLine(x5, y5, x5 + ARROW_LENGTH, y5 - ARROW_LENGTH);
        drawLine(x5, y5, x5 + ARROW_LENGTH, y5 + ARROW_LENGTH);

        drawLine(x4, y4, x6, y6);
        if (y4 < y6) {
            drawLine(x6 - ARROW_LENGTH, y6 - ARROW_LENGTH, x6, y6);
            drawLine(x6 + ARROW_LENGTH, y6 - ARROW_LENGTH, x6, y6);
        }
        else {
            drawLine(x6 - ARROW_LENGTH, y6 + ARROW_LENGTH, x6, y6);
            drawLine(x6 + ARROW_LENGTH, y6 + ARROW_LENGTH, x6, y6);
        }
    }

    private static void drawLine(int x1, int y1, int x2, int y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.draw();
    }

    private void printBlocks(InsnBlock[] blocks) {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        for (InsnBlock block : blocks) {
            fm.format(Const.DEBUG_FORMAT, block);
            for (String item : block.lines) {
                fm.format(Const.DEBUG_FORMAT, "    " + item);
            }
            for (InsnBlock nextBlock : block.nextBlockList) {
                fm.format(Const.DEBUG_FORMAT, "--->" + nextBlock);
            }
            for (InsnBlock nextBlock : block.jumpBlockList) {
                fm.format(Const.DEBUG_FORMAT, "-+->" + nextBlock);
            }
            fm.format(Const.DEBUG_FORMAT, Const.DIVISION_LINE);
        }
        System.out.println(sb);
    }

    private void printBoxes() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        for (InsnBox box : boxes) {
            String msg = String.format("(%d,%d) - %s", box.x, box.y, box.block);
            fm.format(Const.DEBUG_FORMAT, msg);
        }
        fm.format(Const.DEBUG_FORMAT, Const.DIVISION_LINE);
        System.out.print(sb);
    }
}
