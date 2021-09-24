package lsieun.asm.analysis;

import org.objectweb.asm.tree.analysis.*;

public class NullabilityAnalyzer extends Analyzer<BasicValue> {
    public NullabilityAnalyzer(Interpreter<BasicValue> interpreter) {
        super(interpreter);
    }

    @Override
    protected Frame<BasicValue> newFrame(Frame<? extends BasicValue> frame) {
        return new NullabilityFrame((NullabilityFrame) frame);
    }

    @Override
    protected Frame<BasicValue> newFrame(int numLocals, int numStack) {
        return new NullabilityFrame(numLocals, numStack);
    }
}
