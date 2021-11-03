package lsieun.asm.analysis.nullability;

import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

public class NullabilityAnalyzer extends Analyzer<NullabilityValue> {
    public NullabilityAnalyzer(Interpreter<NullabilityValue> interpreter) {
        super(interpreter);
    }

    @Override
    protected Frame<NullabilityValue> newFrame(Frame<? extends NullabilityValue> frame) {
        return new NullabilityFrame((NullabilityFrame) frame);
    }

    @Override
    protected Frame<NullabilityValue> newFrame(int numLocals, int numStack) {
        return new NullabilityFrame(numLocals, numStack);
    }
}
