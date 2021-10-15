package lsieun.asm.analysis.diagnosis;

import lsieun.trove.TIntArrayList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.*;

import java.util.Arrays;

public class RedundantVariableDiagnosis {
    public static int[] diagnose(String className, MethodNode mn) throws AnalyzerException {
        // 第一步，准备工作。使用SimpleVerifier进行分析，得到frames信息
        Analyzer<BasicValue> analyzer = new Analyzer<>(new SimpleVerifier());
        Frame<BasicValue>[] frames = analyzer.analyze(className, mn);

        // 第二步，利用frames信息，查看local variable当中哪些slot数据出现了冗余
        TIntArrayList localIndexList = new TIntArrayList();
        for (Frame<BasicValue> f : frames) {
            int locals = f.getLocals();
            for (int i = 0; i < locals; i++) {
                BasicValue val1 = f.getLocal(i);
                if (val1 == BasicValue.UNINITIALIZED_VALUE) {
                    continue;
                }
                for (int j = i + 1; j < locals; j++) {
                    BasicValue val2 = f.getLocal(j);
                    if (val2 == BasicValue.UNINITIALIZED_VALUE) {
                        continue;
                    }
                    if (val1 == val2) {
                        if (!localIndexList.contains(j)) {
                            localIndexList.add(j);
                        }
                    }
                }
            }
        }

        // 第三步，将slot的索引值（local index）转换成instruction的索引值（insn index）
        TIntArrayList insnIndexList = new TIntArrayList();
        InsnList instructions = mn.instructions;
        int size = instructions.size();
        for (int i = 0; i < size; i++) {
            AbstractInsnNode node = instructions.get(i);
            int opcode = node.getOpcode();
            if (opcode >= Opcodes.ISTORE && opcode <= Opcodes.ASTORE) {
                VarInsnNode varInsnNode = (VarInsnNode) node;
                if (localIndexList.contains(varInsnNode.var)) {
                    if (!insnIndexList.contains(i)) {
                        insnIndexList.add(i);
                    }
                }
            }
        }

        // 第四步，将insnIndexList转换成int[]形式
        int[] array = insnIndexList.toNativeArray();
        Arrays.sort(array);
        return array;
    }
}
