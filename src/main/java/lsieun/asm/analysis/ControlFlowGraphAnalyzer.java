package lsieun.asm.analysis;

import lsieun.asm.analysis.graph.InsnBlock;
import org.objectweb.asm.tree.*;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class ControlFlowGraphAnalyzer {
    private final Set<LabelNode> jumpLabelSet = new HashSet<>();
    private final Map<LabelNode, Set<InsnBlock>> jumpLabelMap = new HashMap<>();
    private final Map<LabelNode, InsnBlock> labelInBlockMap = new HashMap<>();
    private final InsnText insnText = new InsnText();

    private InsnBlock currentBlock;
    private final List<AbstractInsnNode> nodeList = new ArrayList<>();
    private final List<InsnBlock> blockList = new ArrayList<>();

    public ControlFlowGraphAnalyzer() {
        this.currentBlock = new InsnBlock();
    }

    public void analyze(MethodNode mn) {
        findJumpLabel(mn);
        instruction2Block(mn);
        linkBlocks();
    }


    private void findJumpLabel(MethodNode mn) {
        InsnList instructions = mn.instructions;

        for (AbstractInsnNode node : instructions) {
            if (node instanceof JumpInsnNode) {
                // 当前block与跳转目标的关系
                JumpInsnNode currentNode = (JumpInsnNode) node;
                jumpLabelSet.add(currentNode.label);
            }
            else if (node instanceof TableSwitchInsnNode) {
                // 当前block与跳转目标的关系
                TableSwitchInsnNode currentNode = (TableSwitchInsnNode) node;
                jumpLabelSet.add(currentNode.dflt);
                jumpLabelSet.addAll(currentNode.labels);
            }
            else if (node instanceof LookupSwitchInsnNode) {
                // 当前block与跳转目标的关系
                LookupSwitchInsnNode currentNode = (LookupSwitchInsnNode) node;
                jumpLabelSet.add(currentNode.dflt);
                jumpLabelSet.addAll(currentNode.labels);
            }
        }

        List<TryCatchBlockNode> tryCatchBlocks = mn.tryCatchBlocks;
        for (TryCatchBlockNode node : tryCatchBlocks) {
            jumpLabelSet.add(node.handler);
        }
    }

    private void instruction2Block(MethodNode mn) {
        InsnList instructions = mn.instructions;

        for (AbstractInsnNode node : instructions) {
            int opcode = node.getOpcode();

            if (node instanceof JumpInsnNode) {
                nodeList.add(node);

                // 当前block收集数据完成
                completeBlock();

                // 当前block与跳转目标的关系
                JumpInsnNode currentNode = (JumpInsnNode) node;
                addJumpFromBlockToLabel(currentNode.label);

                // 当前block与下一个block的关系
                InsnBlock nextBlock = new InsnBlock();
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
                    addJumpFromBlockToLabel(currentNode.labels.get(i - min));
                }
                addJumpFromBlockToLabel(currentNode.dflt);

                // 下一个block成为当前block
                InsnBlock nextBlock = new InsnBlock();
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
                    addJumpFromBlockToLabel(labelNode);
                }
                addJumpFromBlockToLabel(currentNode.dflt);

                // 下一个block成为当前block
                InsnBlock nextBlock = new InsnBlock();
                currentBlock = nextBlock;
            }
            else if (node instanceof LabelNode) {
                LabelNode currentNode = (LabelNode) node;

                if (jumpLabelSet.contains(currentNode)) {
                    if (nodeList.size() > 0) {
                        // 当前block收集数据完成
                        completeBlock();

                        // 下一个block成为当前block
                        InsnBlock nextBlock = new InsnBlock();
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

                    InsnBlock nextBlock = new InsnBlock();
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

    private void linkBlocks() {
        for (Map.Entry<LabelNode, Set<InsnBlock>> item : jumpLabelMap.entrySet()) {
            LabelNode key = item.getKey();
            Set<InsnBlock> set = item.getValue();

            InsnBlock targetBlock = labelInBlockMap.get(key);
            for (InsnBlock block : set) {
                block.jumpBlockList.add(targetBlock);
            }
        }
    }


    private void addJumpFromBlockToLabel(LabelNode labelNode) {
        Set<InsnBlock> list = jumpLabelMap.get(labelNode);
        if (list != null) {
            list.add(currentBlock);

        }
        else {
            list = new HashSet<>();
            list.add(currentBlock);
            jumpLabelMap.put(labelNode, list);
        }
    }

    private void completeBlock() {
        for (AbstractInsnNode node : nodeList) {
            List<String> lines = insnText.toLines(node);
            currentBlock.addLines(lines);
        }

        nodeList.clear();
        blockList.add(currentBlock);
    }

    public InsnBlock[] getBlocks() {
        return blockList.toArray(new InsnBlock[0]);
    }

}
