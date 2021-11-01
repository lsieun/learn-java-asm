package lsieun.asm.analysis.cc;

import org.objectweb.asm.tree.analysis.*;

public class CyclomaticComplexityAnalyzer<V extends Value> extends Analyzer<V> {
    public CyclomaticComplexityAnalyzer(Interpreter<V> interpreter) {
        super(interpreter);
    }

    @Override
    protected Frame<V> newFrame(int numLocals, int numStack) {
        return new CyclomaticComplexityFrame<>(numLocals, numStack);
    }

    @Override
    protected Frame<V> newFrame(Frame<? extends V> frame) {
        return new CyclomaticComplexityFrame<>(frame);
    }

    @Override
    protected void newControlFlowEdge(int insnIndex, int successorIndex) {
        CyclomaticComplexityFrame<V> frame = (CyclomaticComplexityFrame<V>) getFrames()[insnIndex];
        frame.successors.add((CyclomaticComplexityFrame<V>) getFrames()[successorIndex]);
    }
}
