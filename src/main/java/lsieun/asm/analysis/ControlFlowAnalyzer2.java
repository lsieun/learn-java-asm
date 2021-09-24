package lsieun.asm.analysis;

import lsieun.asm.analysis.graph.InsnBlock;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.util.List;

public class ControlFlowAnalyzer2 extends ControlFlowAnalyzer{
    private AbstractInsnNode[] nodeArray;
    public InsnBlock[] blocks;

    public ControlFlowAnalyzer2() {

    }

    @Override
    public void analyze(String owner, MethodNode method) throws AnalyzerException {
        nodeArray = method.instructions.toArray();
        int length = nodeArray.length;
        blocks = new InsnBlock[length];
        InsnText insnText = new InsnText();
        for (int i = 0; i < length; i++) {
            blocks[i] = getBlock(i);
            AbstractInsnNode node = nodeArray[i];
            List<String> lines = insnText.toLines(node);
            blocks[i].addLines(lines);
        }

        super.analyze(owner, method);
    }

    @Override
    protected void newControlFlowEdge(int insnIndex, int successorIndex) {
        // 首先，处理自己的代码逻辑
        AbstractInsnNode insnNode = nodeArray[insnIndex];
        int insnOpcode = insnNode.getOpcode();
        int insnType = insnNode.getType();

        if (insnType == AbstractInsnNode.JUMP_INSN) {
            if ((insnIndex + 1) == successorIndex) {
                addNext(insnIndex, successorIndex);
            }
            else {
                addJump(insnIndex, successorIndex);
            }
        }
        else if (insnOpcode == LOOKUPSWITCH) {
            addJump(insnIndex, successorIndex);
        }
        else if (insnOpcode == TABLESWITCH) {
            addJump(insnIndex, successorIndex);
        }
        else if (insnOpcode == RET) {
            addJump(insnIndex, successorIndex);
        }
        else if (insnOpcode == ATHROW || (insnOpcode >= IRETURN && insnOpcode <= RETURN)) {
            assert false : "should not be here";
            removeNextAndJump(insnIndex);
        }
        else {
            addNext(insnIndex, successorIndex);
        }

        // 其次，调用父类的方法实现
        super.newControlFlowEdge(insnIndex, successorIndex);
    }

    private void addNext(int fromIndex, int toIndex) {
        InsnBlock currentBlock = getBlock(fromIndex);
        InsnBlock nextBlock = getBlock(toIndex);
        currentBlock.addNext(nextBlock);
    }

    private void addJump(int fromIndex, int toIndex) {
        InsnBlock currentBlock = getBlock(fromIndex);
        InsnBlock nextBlock = getBlock(toIndex);
        currentBlock.addJump(nextBlock);
    }

    private void removeNextAndJump(int insnIndex) {
        InsnBlock currentBlock = getBlock(insnIndex);
        currentBlock.nextBlockList.clear();
        currentBlock.jumpBlockList.clear();
    }

    private InsnBlock getBlock(int insnIndex) {
        InsnBlock block = blocks[insnIndex];
        if (block == null){
            block = new InsnBlock();
            blocks[insnIndex] = block;
        }
        return block;
    }

    public InsnBlock[] getBlocks() {
        return blocks;
    }
}
