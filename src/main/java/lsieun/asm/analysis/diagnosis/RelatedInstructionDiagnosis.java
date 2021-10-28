package lsieun.asm.analysis.diagnosis;

import lsieun.asm.analysis.transition.DestinationInterpreter;
import lsieun.trove.TIntArrayList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RelatedInstructionDiagnosis {
    public static int[] diagnose(String className, MethodNode mn, int insnIndex) throws AnalyzerException {
        // 第一步，判断insnIndex范围是否合理
        InsnList instructions = mn.instructions;
        int size = instructions.size();
        if (insnIndex < 0 || insnIndex >= size) {
            String message = String.format("the 'insnIndex' argument should in range [0, %d]", size - 1);
            throw new IllegalArgumentException(message);
        }

        // 第二步，获取两个Frame
        Frame<SourceValue>[] sourceFrames = getSourceFrames(className, mn);
        Frame<SourceValue>[] destinationFrames = getDestinationFrames(className, mn);

        // 第三步，循环处理，所有结果记录到这个intArrayList变量中
        TIntArrayList intArrayList = new TIntArrayList();
        // 循环tmpInsnList
        List<AbstractInsnNode> tmpInsnList = new ArrayList<>();
        AbstractInsnNode insnNode = instructions.get(insnIndex);
        tmpInsnList.add(insnNode);
        for (int i = 0; i < tmpInsnList.size(); i++) {
            AbstractInsnNode currentNode = tmpInsnList.get(i);
            int opcode = currentNode.getOpcode();

            int index = instructions.indexOf(currentNode);
            intArrayList.add(index);

            // 第一种情况，处理load相关的opcode情况
            Frame<SourceValue> srcFrame = sourceFrames[index];
            if (opcode >= Opcodes.ILOAD && opcode <= Opcodes.ALOAD) {
                VarInsnNode varInsnNode = (VarInsnNode) currentNode;
                int localIndex = varInsnNode.var;
                SourceValue value = srcFrame.getLocal(localIndex);
                for (AbstractInsnNode insn : value.insns) {
                    if (!tmpInsnList.contains(insn)) {
                        tmpInsnList.add(insn);
                    }
                }
            }

            // 第二种情况，从dstFrame到srcFrame查找
            Frame<SourceValue> dstFrame = destinationFrames[index];
            int stackSize = dstFrame.getStackSize();
            for (int j = 0; j < stackSize; j++) {
                SourceValue value = dstFrame.getStack(j);
                if (value.insns.contains(currentNode)) {
                    for (AbstractInsnNode insn : srcFrame.getStack(j).insns) {
                        if (!tmpInsnList.contains(insn)) {
                            tmpInsnList.add(insn);
                        }
                    }
                }
            }
        }

        // 第四步，将intArrayList变量转换成int[]，并进行排序
        int[] array = intArrayList.toNativeArray();
        Arrays.sort(array);
        return array;
    }


    private static Frame<SourceValue>[] getSourceFrames(String className, MethodNode mn) throws AnalyzerException {
        Analyzer<SourceValue> analyzer = new Analyzer<>(new SourceInterpreter());
        return analyzer.analyze(className, mn);
    }

    private static Frame<SourceValue>[] getDestinationFrames(String className, MethodNode mn) throws AnalyzerException {
        Analyzer<SourceValue> analyzer = new Analyzer<>(new DestinationInterpreter());
        return analyzer.analyze(className, mn);
    }
}
