package lsieun.asm.analysis.graph;

import lsieun.utils.BoxDrawing;
import lsieun.utils.TextStateCanvas;

import java.util.ArrayList;
import java.util.List;

public class TextGraph {
    private static final int ROW_SPACE = 1;
    private static final int COL_SPACE = 3;

    private final InsnBlock[] blockArray;
    private final TextBox[] boxArray;
    private final int boxNum;
    private final int maxInstructionLength;
    private final TextStateCanvas canvas;

    public TextGraph(InsnBlock[] blockArray) {
        this.blockArray = blockArray;
        this.boxNum = blockArray.length;
        this.boxArray = new TextBox[boxNum];
        this.maxInstructionLength = findMaxStringLength(blockArray);
        this.canvas = new TextStateCanvas();
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public void draw(int startRow, int startCol) {
        int row = startRow;
        int col = startCol;
        int length = boxArray.length;
        for (int i = 0; i < length; i++) {
            InsnBlock block = blockArray[i];

            int width = getOdd(maxInstructionLength + 4);
            int height = block.lines.size() + 2;

            TextBox box = TextBox.valueOf(row, col, width, height);
            boxArray[i] = box;
            drawBoxAndText(box, block.lines);

            row += height + ROW_SPACE;
        }

        drawLinks();

        List<String> lines = canvas.getLines();
        for (String item : lines) {
            System.out.println(item);
        }
    }

    private void drawBoxAndText(TextBox box, List<String> lines) {
        int row = box.row;
        int col = box.col;
        int width = box.width;
        int height = box.height;

        canvas.drawRectangle(row, col, height, width);
        canvas.drawMultiLineText(row + 1, col + 2, lines);
    }

    private void drawLinks() {
        for (int i = 0; i < boxNum; i++) {
            InsnBlock currentBlock = blockArray[i];
            TextBox currentBox = boxArray[i];

            List<TextBox> nextBoxes = findBoxes(currentBlock.nextBlockList);
            for (TextBox nextBox : nextBoxes) {
                int rowStart = currentBox.row + currentBox.height - 1;
                int rowStop = nextBox.row;
                int col = currentBox.col + currentBox.width / 2;

                canvas.setPixel(rowStart, col, BoxDrawing.LIGHT_DOWN_AND_HORIZONTAL.val);
                canvas.setPixel(rowStop, col, BoxDrawing.LIGHT_UP_AND_HORIZONTAL.val);
                canvas.drawVerticalLine(rowStart + 1, col, rowStop - rowStart - 1);
            }

            List<TextBox> jumpBoxes = findBoxes(currentBlock.jumpBlockList);
            for (TextBox nextBox : jumpBoxes) {
                int rowStart = currentBox.row + currentBox.height - 2;
                int rowStop = nextBox.row + 1;
                int colStart = currentBox.col + currentBox.width - 1;
                int colStop = currentBox.col + currentBox.width - 1 + (i + 1) * COL_SPACE;
                canvas.setPixel(rowStart, colStart, BoxDrawing.LIGHT_VERTICAL_AND_RIGHT.val);
                canvas.setPixel(rowStop, colStart, BoxDrawing.LIGHT_VERTICAL_AND_RIGHT.val);

                if (rowStart < rowStop) {
                    canvas.moveTo(rowStart, colStart + 1);
                    canvas.turnRight().drawLine(colStop - colStart)
                            .switchDown().drawLine(rowStop - rowStart - 1)
                            .switchLeft().drawLine(colStop - colStart);

                }
                else {
                    canvas.moveTo(rowStart, colStart + 1);
                    canvas.turnRight().drawLine(colStop - colStart)
                            .switchUp().drawLine(rowStart - rowStop - 1)
                            .switchLeft().drawLine(colStop - colStart);
                }
            }
        }
    }

    private List<TextBox> findBoxes(List<InsnBlock> blockList) {
        List<TextBox> boxList = new ArrayList<>();

        for (int i = 0; i < boxNum; i++) {
            InsnBlock block = blockArray[i];
            if (blockList.contains(block)) {
                boxList.add(boxArray[i]);
            }
        }

        return boxList;
    }

    private int getOdd(int num) {
        int remainder = num % 2;
        if (remainder == 0) {
            return num + 1;
        }
        return num;
    }

    private int findMaxStringLength(InsnBlock[] blockArray) {
        int maxLength = 0;
        for (InsnBlock block : blockArray) {
            int length = findMaxStringLength(block.lines);
            if (length > maxLength) {
                maxLength = length;
            }
        }
        return maxLength;
    }

    private int findMaxStringLength(List<String> lines) {
        int maxLength = 0;
        for (String item : lines) {
            if (item == null) continue;
            int length = item.length();
            if (length > maxLength) {
                maxLength = length;
            }
        }
        return maxLength;
    }
}
