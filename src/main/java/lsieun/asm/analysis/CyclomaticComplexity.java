package lsieun.asm.analysis;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

public class CyclomaticComplexity {
    public int getCyclomaticComplexity(String owner, MethodNode mn) throws AnalyzerException {
        Analyzer<BasicValue> analyzer = new Analyzer<BasicValue>(new BasicInterpreter()) {
            @Override
            protected Frame<BasicValue> newFrame(int numLocals, int numStack) {
                return new Node<>(numLocals, numStack);
            }

            @Override
            protected Frame<BasicValue> newFrame(Frame<? extends BasicValue> frame) {
                return new Node<>(frame);
            }

            @Override
            protected void newControlFlowEdge(int insnIndex, int successorIndex) {
                Node<BasicValue> s = (Node<BasicValue>) getFrames()[insnIndex];
                s.successors.add((Node<BasicValue>) getFrames()[successorIndex]);
            }
        };

        Frame<BasicValue>[] frames = analyzer.analyze(owner, mn);
        int edges = 0;
        int nodes = 0;
        for (Frame<BasicValue> frame : frames) {
            if (frame != null) {
                edges += ((Node<BasicValue>) frame).successors.size();
                nodes += 1;
            }
        }
        return edges - nodes + 2;
    }
}
