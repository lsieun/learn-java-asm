package lsieun.asm.analysis;

import lsieun.asm.analysis.graph.InsnBlock;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlFlowEdgeAnalyzer2<V extends Value> extends ControlFlowEdgeAnalyzer<V> {

    public ControlFlowEdgeAnalyzer2(Interpreter<V> interpreter) {
        super(interpreter);
    }

    @Override
    public InsnBlock[] getBlocks() {
        //（1）调用父类实现
        InsnBlock[] blocks = super.getBlocks();

        //（2）如果结果为空，则提前返回
        if (blocks == null || blocks.length < 1) {
            return blocks;
        }

        //（3）记录“分隔点”
        Set<InsnBlock> newBlockSet = new HashSet<>();
        int length = blocks.length;
        for (int i = 0; i < length; i++) {
            InsnBlock currentBlock = blocks[i];
            List<InsnBlock> nextBlockList = currentBlock.nextBlockList;
            List<InsnBlock> jumpBlockList = currentBlock.jumpBlockList;

            boolean hasNext = false;
            boolean hasJump = false;

            if (nextBlockList.size() > 0) {
                hasNext = true;
            }

            if (jumpBlockList.size() > 0) {
                hasJump = true;
            }

            if (!hasNext && (i + 1) < length) {
                newBlockSet.add(blocks[i + 1]);
            }

            if (hasJump) {
                newBlockSet.addAll(jumpBlockList);

                if (hasNext) {
                    newBlockSet.add(blocks[i + 1]);
                }
            }
        }

        //（4）利用“分隔点”，合并成不同的分组
        List<InsnBlock> resultList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            InsnBlock currentBlock = blocks[i];

            if (i == 0) {
                resultList.add(currentBlock);
            }
            else if (newBlockSet.contains(currentBlock)) {
                resultList.add(currentBlock);
            }
            else {
                int size = resultList.size();
                InsnBlock lastBlock = resultList.get(size - 1);
                lastBlock.lines.addAll(currentBlock.lines);
                lastBlock.jumpBlockList.clear();
                lastBlock.jumpBlockList.addAll(currentBlock.jumpBlockList);
                lastBlock.nextBlockList.clear();
                lastBlock.nextBlockList.addAll(currentBlock.nextBlockList);
            }
        }

        return resultList.toArray(new InsnBlock[0]);
    }
}
