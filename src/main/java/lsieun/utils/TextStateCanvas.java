package lsieun.utils;

public class TextStateCanvas extends TextCanvas {
    private int row;
    private int col;
    private TextDirection direction;

    public void moveTo(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void moveUp(int count) {
        row -= count;
    }

    public void moveDown(int count) {
        row += count;
    }

    public void moveLeft(int count) {
        col -= count;
    }

    public void moveRight(int count) {
        col += count;
    }

    public TextStateCanvas drawLine(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count should be greater than zero: " + count);
        }

        switch (direction) {
            case UP:
                drawVerticalLine(row - count + 1, col, count);
                moveUp(count);
                break;
            case DOWN:
                drawVerticalLine(row, col, count);
                moveDown(count);
                break;
            case LEFT:
                drawHorizontalLine(row, col - count + 1, count);
                moveLeft(count);
                break;
            case RIGHT:
                drawHorizontalLine(row, col, count);
                moveRight(count);
                break;
            default:
                throw new RuntimeException("Unsupported Direction: " + direction);
        }
        return this;
    }

//    public TextStateCanvas drawUp(int count) {
//        direction = TextDirection.UP;
//        drawVerticalLine(row - count, col, count);
//        return moveUp(count);
//    }
//
//    public TextStateCanvas drawDown(int count) {
//        direction = TextDirection.DOWN;
//        drawVerticalLine(row, col, count);
//        return moveDown(count);
//    }
//
//    public TextStateCanvas drawLeft(int count) {
//        direction = TextDirection.LEFT;
//        drawHorizontalLine(row, col - count, count);
//        return moveLeft(count);
//    }
//
//    public TextStateCanvas drawRight(int count) {
//        direction = TextDirection.RIGHT;
//        drawHorizontalLine(row, col, count);
//        return moveRight(count);
//    }

    public TextStateCanvas turnUp() {
        direction = TextDirection.UP;
        return this;
    }

    public TextStateCanvas turnDown() {
        direction = TextDirection.DOWN;
        return this;
    }

    public TextStateCanvas turnLeft() {
        direction = TextDirection.LEFT;
        return this;
    }

    public TextStateCanvas turnRight() {
        direction = TextDirection.RIGHT;
        return this;
    }

    public TextStateCanvas switchUp() {
        if (direction == TextDirection.LEFT) {
            mergePixel(row, col, BoxDrawing.LIGHT_UP_AND_RIGHT.val);
        }
        else if (direction == TextDirection.RIGHT) {
            mergePixel(row, col, BoxDrawing.LIGHT_UP_AND_LEFT.val);
        }
        else {
            // do nothing
        }

        moveUp(1);
        direction = TextDirection.UP;
        return this;
    }

    public TextStateCanvas switchDown() {
        if (direction == TextDirection.LEFT) {
            mergePixel(row, col, BoxDrawing.LIGHT_DOWN_AND_RIGHT.val);
        }
        else if (direction == TextDirection.RIGHT) {
            mergePixel(row, col, BoxDrawing.LIGHT_DOWN_AND_LEFT.val);
        }
        else {
            // do nothing
        }

        moveDown(1);
        direction = TextDirection.DOWN;
        return this;
    }

    public TextStateCanvas switchLeft() {
        if (direction == TextDirection.UP) {
            mergePixel(row, col, BoxDrawing.LIGHT_DOWN_AND_LEFT.val);
        }
        else if (direction == TextDirection.DOWN) {
            mergePixel(row, col, BoxDrawing.LIGHT_UP_AND_LEFT.val);
        }
        else {
            // do nothing
        }

        moveLeft(1);
        direction = TextDirection.LEFT;
        return this;
    }

    public TextStateCanvas switchRight() {
        if (direction == TextDirection.UP) {
            mergePixel(row, col, BoxDrawing.LIGHT_DOWN_AND_RIGHT.val);
        }
        else if (direction == TextDirection.DOWN) {
            mergePixel(row, col, BoxDrawing.LIGHT_UP_AND_RIGHT.val);
        }
        else {
            // do nothing
        }

        moveRight(1);
        direction = TextDirection.RIGHT;
        return this;
    }

    public static void main(String[] args) {
        TextStateCanvas canvas = new TextStateCanvas();
        canvas.moveTo(10, 20);

        canvas.turnUp().drawLine(3)
                .switchRight().drawLine(10)
                .switchDown().drawLine(5)
                .switchLeft().drawLine(20)
                .switchUp().drawLine(7)
                .switchRight().drawLine(30);

        canvas.moveTo(20, 30);
        canvas.turnUp().drawLine(3)
                .switchLeft().drawLine(10)
                .switchDown().drawLine(5)
                .switchRight().drawLine(20)
                .switchUp().drawLine(7)
                .switchLeft().drawLine(30);

        canvas.getLines().forEach(System.out::println);
//        canvas.printPixels();
    }
}
