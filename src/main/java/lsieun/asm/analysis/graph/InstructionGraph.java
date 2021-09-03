package lsieun.asm.analysis.graph;

import lsieun.graphics.Line;
import lsieun.graphics.Rectangle;
import lsieun.utils.OpcodeConst;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class InstructionGraph {
    private static final String DIVISION_LINE = "================================================================";
    private static final int START_X = 10;
    private static final int START_Y = 10;

    private static final int ROW_SPACE = 30;
    private static final int COLUMN_SPACE = 30;

    private static final int LINE_SPACE = 20;
    private static final int ARROW_LENGTH = 5;
    private static final int SHORT_LINE_LENGTH = 10;
    private static final int SIZE_LIMIT = 35;
    private static final String INSTRUCTION_FORMAT = "%s %s";

    private final Map<LabelNode, String> labelNames = new HashMap<>();
    private final Set<LabelNode> jumpLabels = new HashSet<>();
    private final Map<LabelNode, Set<InstructionBlock>> preJumpMap = new HashMap<>();
    private final Map<LabelNode, InstructionBlock> labelInBlockMap = new HashMap<>();

    private InstructionBlock currentBlock;
    private final List<AbstractInsnNode> nodeList = new ArrayList<>();
    private final List<InstructionBlock> blockList = new ArrayList<>();

    private final int startX;
    private final int startY;

    public InstructionGraph() {
        this(START_X, START_Y);
    }

    public InstructionGraph(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
        this.currentBlock = new InstructionBlock();
    }

    public void init(MethodNode mn) {
        InsnList instructions = mn.instructions;
        int size = instructions.size();
        if (size > SIZE_LIMIT) {
            System.out.println("instruction size should be less than " + SIZE_LIMIT + ".");
            return;
        }

        pickJumpLabelNode(mn);
        splitInstructionBlock(mn);
        link();
    }

    private void pickJumpLabelNode(MethodNode mn) {
        InsnList instructions = mn.instructions;

        for (AbstractInsnNode node : instructions) {
            if (node instanceof JumpInsnNode) {
                // 当前block与跳转目标的关系
                JumpInsnNode currentNode = (JumpInsnNode) node;
                jumpLabels.add(currentNode.label);
            }
            else if (node instanceof TableSwitchInsnNode) {
                // 当前block与跳转目标的关系
                TableSwitchInsnNode currentNode = (TableSwitchInsnNode) node;
                jumpLabels.add(currentNode.dflt);
                jumpLabels.addAll(currentNode.labels);
            }
            else if (node instanceof LookupSwitchInsnNode) {
                // 当前block与跳转目标的关系
                LookupSwitchInsnNode currentNode = (LookupSwitchInsnNode) node;
                jumpLabels.add(currentNode.dflt);
                jumpLabels.addAll(currentNode.labels);
            }
        }

        List<TryCatchBlockNode> tryCatchBlocks = mn.tryCatchBlocks;
        for (TryCatchBlockNode node : tryCatchBlocks) {
            jumpLabels.add(node.handler);
        }
    }

    public void splitInstructionBlock(MethodNode mn) {
        InsnList instructions = mn.instructions;

        for (AbstractInsnNode node : instructions) {
            int opcode = node.getOpcode();

            if (node instanceof JumpInsnNode) {
                nodeList.add(node);

                // 当前block收集数据完成
                completeBlock();

                // 当前block与跳转目标的关系
                JumpInsnNode currentNode = (JumpInsnNode) node;
                addPreJump(currentNode.label);

                // 当前block与下一个block的关系
                InstructionBlock nextBlock = new InstructionBlock();
                if ((opcode >= IFEQ && opcode <= IF_ACMPNE) || (opcode >= IFNULL && opcode <= IFNONNULL)) {
                    currentBlock.nextBlockList.add(nextBlock);
                }

                // 下一个block成为当前block
                currentBlock = nextBlock;
            }
            else if (node instanceof TableSwitchInsnNode) {
                nodeList.add(node);

                // 当前block收集数据完成
                completeBlock();

                // 当前block与跳转目标的关系
                TableSwitchInsnNode currentNode = (TableSwitchInsnNode) node;
                int min = currentNode.min;
                int max = currentNode.max;
                for (int i = min; i <= max; i++) {
                    addPreJump(currentNode.labels.get(i - min));
                }
                addPreJump(currentNode.dflt);

                // 下一个block成为当前block
                InstructionBlock nextBlock = new InstructionBlock();
                currentBlock = nextBlock;
            }
            else if (node instanceof LookupSwitchInsnNode) {
                nodeList.add(node);

                // 当前block收集数据完成
                completeBlock();

                // 当前block与跳转目标的关系
                LookupSwitchInsnNode currentNode = (LookupSwitchInsnNode) node;
                List<LabelNode> labels = currentNode.labels;
                for (LabelNode labelNode : labels) {
                    addPreJump(labelNode);
                }
                addPreJump(currentNode.dflt);

                // 下一个block成为当前block
                InstructionBlock nextBlock = new InstructionBlock();
                currentBlock = nextBlock;
            }
            else if (node instanceof LabelNode) {
                LabelNode currentNode = (LabelNode) node;

                if (jumpLabels.contains(currentNode)) {
                    if (nodeList.size() > 0) {
                        // 当前block收集数据完成
                        completeBlock();

                        // 下一个block成为当前block
                        InstructionBlock nextBlock = new InstructionBlock();
                        currentBlock.nextBlockList.add(nextBlock);
                        currentBlock = nextBlock;
                    }

                }

                nodeList.add(node);
                labelInBlockMap.put(currentNode, currentBlock);
            }
            else if (node instanceof InsnNode) {
                nodeList.add(node);

                // 当前block收集数据完成
                if ((opcode >= IRETURN && opcode <= RETURN) || (opcode == ATHROW)) {
                    completeBlock();

                    InstructionBlock nextBlock = new InstructionBlock();
                    currentBlock = nextBlock;
                }

            }
            else {
                nodeList.add(node);
            }
        }

        if (nodeList.size() > 0) {
            // 当前block收集数据完成
            completeBlock();
        }
    }

    public void link() {
        for (Map.Entry<LabelNode, Set<InstructionBlock>> item : preJumpMap.entrySet()) {
            LabelNode key = item.getKey();
            Set<InstructionBlock> set = item.getValue();

            InstructionBlock targetBlock = labelInBlockMap.get(key);
            for (InstructionBlock block : set) {
                block.jumpBlockList.add(targetBlock);
            }
        }
    }

    public void print() {
        printBlocks();
        printLabelsInBlock();
    }

    private void printBlocks() {
        int blockSize = blockList.size();
        for (int i = 0; i < blockSize; i++) {
            InstructionBlock block = blockList.get(i);
            System.out.println(block);
            for (String item : block.lines) {
                System.out.println("    " + item);
            }
            for (InstructionBlock nextBlock : block.nextBlockList) {
                System.out.println("--->" + nextBlock);
            }
            for (InstructionBlock nextBlock : block.jumpBlockList) {
                System.out.println("-+->" + nextBlock);
            }
            System.out.println(DIVISION_LINE);
        }
    }

    private void printLabelsInBlock() {
        Set<Map.Entry<LabelNode, InstructionBlock>> entries = labelInBlockMap.entrySet();
        if (entries.size() < 1) return;

        System.out.println("labels in blocks:");
        for (Map.Entry<LabelNode, InstructionBlock> entry : entries) {
            LabelNode key = entry.getKey();
            InstructionBlock value = entry.getValue();
            System.out.println(getLabelName(key) + ": " + value);
        }
        System.out.println(DIVISION_LINE);
    }

    public void draw() {
        int blockSize = blockList.size();
        if (blockSize < 1) return;

        drawBlockRectangles();
        drawConnectionLines();
    }

    private void drawBlockRectangles() {
        int currentX = this.startX;
        int currentY = this.startY;

        int blockSize = blockList.size();
        for (int i = 0; i < blockSize; i++) {
            InstructionBlock block = blockList.get(i);
            if (i != 0) {
                InstructionBlock previousBlock = blockList.get(i - 1);
                currentY = previousBlock.y + previousBlock.getHeight() + ROW_SPACE;
            }

            block.draw(currentX, currentY);
            System.out.println("currentX = " + currentX + ", currentY = " + currentY);
        }
        System.out.println(DIVISION_LINE);
    }

    private void drawConnectionLines() {
        int blockSize = blockList.size();
        for (int i = 0; i < blockSize; i++) {
            InstructionBlock currentBlock = blockList.get(i);
            for (InstructionBlock nextBlock : currentBlock.nextBlockList) {
                connectTop2BottomBlock(currentBlock.box, nextBlock.box);
            }

            for (InstructionBlock jumpBlock : currentBlock.jumpBlockList) {
                jumpOne2Another(currentBlock.box, jumpBlock.box, i);
            }
        }
    }

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

    private void addPreJump(LabelNode labelNode) {
        Set<InstructionBlock> list = preJumpMap.get(labelNode);
        if (list != null) {
            list.add(currentBlock);

        }
        else {
            list = new HashSet<>();
            list.add(currentBlock);
            preJumpMap.put(labelNode, list);
        }
    }

    private void completeBlock() {
        List<String> lines = toLines(nodeList);
        currentBlock.setLines(lines);

        nodeList.clear();
        blockList.add(currentBlock);
    }

    private List<String> toLines(List<AbstractInsnNode> nodeList) {
        List<String> lines = new ArrayList<>();
        if (nodeList == null || nodeList.size() < 1) {
            return lines;
        }

        for (AbstractInsnNode node : nodeList) {
            if (node instanceof TableSwitchInsnNode) {
                TableSwitchInsnNode currentNode = (TableSwitchInsnNode) node;
                lines.addAll(toString(currentNode));
            }
            else if (node instanceof LookupSwitchInsnNode) {
                LookupSwitchInsnNode currentNode = (LookupSwitchInsnNode) node;
                lines.addAll(toString(currentNode));
            }
            else {
                String item = toString(node);
                lines.add(item);
            }
        }
        return lines;
    }

    public String toString(AbstractInsnNode currentNode) {
        if (currentNode instanceof InsnNode) {
            return getOpcodeName(currentNode);
        }
        else if (currentNode instanceof IntInsnNode) {
            int opcode = currentNode.getOpcode();
            String opcodeName = getOpcodeName(currentNode);
            IntInsnNode node = (IntInsnNode) currentNode;
            int operand = node.operand;
            if (opcode == BIPUSH || opcode == SIPUSH) {
                return String.format(INSTRUCTION_FORMAT, opcodeName, operand);
            }
            else {
                final String firstArg;
                switch (operand) {
                    case 4: {
                        firstArg = "4 (boolean)";
                        break;
                    }
                    case 5: {
                        firstArg = "5 (char)";
                        break;
                    }
                    case 6: {
                        firstArg = "6 (float)";
                        break;
                    }
                    case 7: {
                        firstArg = "7 (double)";
                        break;
                    }
                    case 8: {
                        firstArg = "8 (byte)";
                        break;
                    }
                    case 9: {
                        firstArg = "9 (short)";
                        break;
                    }
                    case 10: {
                        firstArg = "10 (int)";
                        break;
                    }
                    case 11: {
                        firstArg = "11 (long)";
                        break;
                    }
                    default:
                        throw new RuntimeException("atype is not supported: " + operand);
                }
                return String.format(INSTRUCTION_FORMAT, opcodeName, firstArg);
            }
        }
        else if (currentNode instanceof VarInsnNode) {
            String opcodeName = getOpcodeName(currentNode);
            VarInsnNode node = (VarInsnNode) currentNode;
            int var = node.var;
            if (var >= 0 && var <= 3) {
                return String.format("%s_%d", opcodeName, var);
            }
            else {
                return String.format(INSTRUCTION_FORMAT, opcodeName, var);
            }
        }
        else if (currentNode instanceof TypeInsnNode) {
            String opcodeName = getOpcodeName(currentNode);
            TypeInsnNode node = (TypeInsnNode) currentNode;
            String type = getSimpleName(node.desc);
            return String.format(INSTRUCTION_FORMAT, opcodeName, type);
        }
        else if (currentNode instanceof FieldInsnNode) {
            String opcodeName = getOpcodeName(currentNode);
            FieldInsnNode node = (FieldInsnNode) currentNode;
            String type = getSimpleName(node.owner);
            return String.format("%s %s.%s", opcodeName, type, node.name);
        }
        else if (currentNode instanceof MethodInsnNode) {
            String opcodeName = getOpcodeName(currentNode);
            MethodInsnNode node = (MethodInsnNode) currentNode;
            String type = getSimpleName(node.owner);
            return String.format("%s %s.%s", opcodeName, type, node.name);
        }
        else if (currentNode instanceof InvokeDynamicInsnNode) {
            String opcodeName = getOpcodeName(currentNode);
            InvokeDynamicInsnNode node = (InvokeDynamicInsnNode) currentNode;
            Type methodType = Type.getMethodType(node.desc);
            Type returnType = methodType.getReturnType();
            String type = getSimpleName(returnType.getInternalName());
            return String.format("%s %s.%s", opcodeName, type, node.name);
        }
        else if (currentNode instanceof JumpInsnNode) {
            String opcodeName = getOpcodeName(currentNode);
            JumpInsnNode node = (JumpInsnNode) currentNode;
            String labelName = getLabelName(node.label);
            return String.format(INSTRUCTION_FORMAT, opcodeName, labelName);
        }
        else if (currentNode instanceof LabelNode) {
            LabelNode node = (LabelNode) currentNode;
            String labelName = getLabelName(node);
            return labelName;
        }
        else if (currentNode instanceof LdcInsnNode) {
            String opcodeName = getOpcodeName(currentNode);
            LdcInsnNode node = (LdcInsnNode) currentNode;
            Object cst = node.cst;
            if (cst instanceof Integer) {
                return String.format("%s %s(int)", opcodeName, cst);
            }
            else if (cst instanceof Float) {
                return String.format("%s %s(float)", opcodeName, cst);
            }
            else if (cst instanceof Long) {
                return String.format("%s %s(long)", opcodeName, cst);
            }
            else if (cst instanceof Double) {
                return String.format("%s %s(double)", opcodeName, cst);
            }
            else if (cst instanceof String) {
                return String.format("%s \"%s\"", opcodeName, cst);
            }
            else if (cst instanceof Class<?>) {
                return String.format("%s %s(class)", opcodeName, cst);
            }
            else {
                return String.format("%s %s", opcodeName, cst);
            }
        }
        else if (currentNode instanceof IincInsnNode) {
            String opcodeName = getOpcodeName(currentNode);
            IincInsnNode node = (IincInsnNode) currentNode;
            return String.format("%s %d %d", opcodeName, node.var, node.incr);
        }
        else if (currentNode instanceof MultiANewArrayInsnNode) {
            String opcodeName = getOpcodeName(currentNode);
            MultiANewArrayInsnNode node = (MultiANewArrayInsnNode) currentNode;
            String type = getSimpleName(node.desc);
            return String.format(INSTRUCTION_FORMAT, opcodeName, type);
        }
        else if (currentNode instanceof FrameNode) {
            return "FrameNode";
        }
        else if (currentNode instanceof LineNumberNode) {
            return "LineNumberNode";
        }
        else {
            System.out.println(currentNode.getClass());
        }
        return currentNode.toString();
    }

    public List<String> toString(TableSwitchInsnNode currentNode) {
        String opcodeName = getOpcodeName(currentNode);
        int min = currentNode.min;
        int max = currentNode.max;

        List<String> list = new ArrayList<>();
        list.add(String.format("%s {", opcodeName));
        for (int i = min; i <= max; i++) {
            LabelNode labelNode = currentNode.labels.get(i - min);
            String labelName = getLabelName(labelNode);
            list.add(String.format("    %d: %s", i, labelName));
        }
        list.add(String.format("    default: %s", getLabelName(currentNode.dflt)));
        list.add("}");
        return list;
    }

    public List<String> toString(LookupSwitchInsnNode currentNode) {
        String opcodeName = getOpcodeName(currentNode);
        List<Integer> keys = currentNode.keys;
        int size = keys.size();

        List<String> list = new ArrayList<>();
        list.add(String.format("%s {", opcodeName));
        for (int i = 0; i < size; i++) {
            int caseValue = keys.get(i);
            LabelNode labelNode = currentNode.labels.get(i);
            String labelName = getLabelName(labelNode);
            list.add(String.format("    %d: %s", caseValue, labelName));
        }
        list.add(String.format("    default: %s", getLabelName(currentNode.dflt)));
        list.add("}");
        return list;
    }

    private String getLabelName(final LabelNode labelNode) {
        String labelName = labelNames.get(labelNode);
        if (labelName == null) {
            labelName = "L" + labelNames.size();
            labelNames.put(labelNode, labelName);
        }
        return labelName;
    }

    private static String getOpcodeName(AbstractInsnNode currentNode) {
        int opcode = currentNode.getOpcode();
        return OpcodeConst.getOpcodeName(opcode);
    }

    private static String getSimpleName(String descriptor) {
        int squareIndex = descriptor.lastIndexOf("[");
        String prefix = descriptor.substring(0, squareIndex + 1);

        String simpleName = descriptor.substring(squareIndex + 1);
        if (simpleName.startsWith("L") && simpleName.endsWith(";")) {
            simpleName = simpleName.substring(1, simpleName.length() - 1);
        }

        int slashIndex = simpleName.lastIndexOf("/");
        simpleName = simpleName.substring(slashIndex + 1);

        return prefix + simpleName;
    }

    private static void left_top_2_right_bottom(Rectangle block1, Rectangle block2) {
        int x1 = block1.getX();
        int y1 = block1.getY();
        int x2 = block1.getX() + block1.getWidth();
        int y2 = block1.getY() + block1.getHeight();

        int x3 = block2.getX();
        int y3 = block2.getY();
        int x4 = block2.getX() + block2.getWidth();
        int y4 = block2.getY() + block2.getHeight();

        int x5 = (x1 + x2) / 2;
        int y5 = y2;
        int x6 = x5;
        int y6 = y2 + SHORT_LINE_LENGTH;
        drawLine(x5, y5, x6, y6);

        int x7 = (x3 + x4) / 2;
        int y7 = y6;
        drawLine(x6, y6, x7, y7);

        int x8 = (x3 + x4) / 2;
        int y8 = y3;
        drawLine(x7, y7, x8, y8);
    }

    private static void left_bottom_2_right_top(Rectangle block1, Rectangle block2) {
        int x1 = block1.getX();
        int y1 = block1.getY();
        int x2 = block1.getX() + block1.getWidth();
        int y2 = block1.getY() + block1.getHeight();

        int x3 = block2.getX();
        int y3 = block2.getY();
        int x4 = block2.getX() + block2.getWidth();
        int y4 = block2.getY() + block2.getHeight();

        int x5 = (x1 + x2) / 2;
        int y5 = y2;
        int x6 = x5;
        int y6 = y2 + SHORT_LINE_LENGTH;
        drawLine(x5, y5, x6, y6);

        int x7 = x6 + (x3 - x1) / 2;
        int y7 = y6;
        drawLine(x6, y6, x7, y7);

        int x8 = x7;
        int y8 = y7 + (y3 - y2) - 2 * SHORT_LINE_LENGTH;
        drawLine(x7, y7, x8, y8);

        int x9 = (x3 + x4) / 2;
        int y9 = y3 - SHORT_LINE_LENGTH;
        drawLine(x8, y8, x9, y9);

        int x10 = x9;
        int y10 = y3;
        drawLine(x9, y9, x10, y10);
    }

    private static void drawLine(int x1, int y1, int x2, int y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.draw();
    }
}
