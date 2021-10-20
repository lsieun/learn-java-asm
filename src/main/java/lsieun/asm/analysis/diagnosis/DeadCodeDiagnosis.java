package lsieun.asm.analysis.diagnosis;

import lsieun.asm.analysis.ControlFlowAnalyzer;
import lsieun.trove.TIntArrayList;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class DeadCodeDiagnosis {
    public static int[] diagnose(String className, MethodNode mn) throws AnalyzerException {
        InsnList instructions = mn.instructions;
        int size = instructions.size();

        boolean[] flags = new boolean[size];
        ControlFlowAnalyzer analyzer = new ControlFlowAnalyzer() {
            @Override
            protected void newControlFlowEdge(int insnIndex, int successorIndex) {
                // 首先，处理自己的代码逻辑
                flags[insnIndex] = true;
                flags[successorIndex] = true;

                // 其次，调用父类的实现
                super.newControlFlowEdge(insnIndex, successorIndex);
            }
        };
        analyzer.analyze(className, mn);


        TIntArrayList intArrayList = new TIntArrayList();
        for (int i = 0; i < size; i++) {
            boolean flag = flags[i];
            if (!flag) {
                intArrayList.add(i);
            }
        }

        return intArrayList.toNativeArray();
    }
}
