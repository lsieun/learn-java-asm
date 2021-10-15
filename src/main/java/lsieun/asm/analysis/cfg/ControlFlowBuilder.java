package lsieun.asm.analysis.cfg;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;

public final class ControlFlowBuilder {
    public static ControlFlowGraph buildCFG(String className, MethodNode methodNode) throws AnalyzerException {
        if ((methodNode.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0) {
            throw new RuntimeException("method body is empty");
        }

        int size = methodNode.instructions.size();
        SimpleEdgeCreator edgeCreator = new SimpleEdgeCreator(size);
        FramelessAnalyzer myAnalyzer = new FramelessAnalyzer(edgeCreator);
        myAnalyzer.analyze(methodNode);


        int[][] transitions = new int[edgeCreator.transitions.length][];
        for (int i = 0; i < transitions.length; i++) {
            transitions[i] = edgeCreator.transitions[i].toNativeArray();
        }
        int[][] errorTransitions = new int[edgeCreator.errorTransitions.length][];
        for (int i = 0; i < errorTransitions.length; i++) {
            errorTransitions[i] = edgeCreator.errorTransitions[i].toNativeArray();
        }

        return new ControlFlowGraph(transitions, errorTransitions);
    }
}
