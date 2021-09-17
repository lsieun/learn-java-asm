package lsieun.asm.analysis;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Value;

public class IsNullInterpreter extends BasicInterpreter {
    public final static BasicValue NULL_VALUE = new BasicValue(null);
    public final static BasicValue MAYBE_NULL_VALUE = new BasicValue(null);

    public IsNullInterpreter(int api) {
        super(api);
    }

    @Override
    public BasicValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        if (insn.getOpcode() == ACONST_NULL) {
            return NULL_VALUE;
        }
        return super.newOperation(insn);
    }

    @Override
    public BasicValue merge(BasicValue value1, BasicValue value2) {
        if (isRef(value1) && isRef(value2) && value1 != value2) {
            return MAYBE_NULL_VALUE;
        }
        return super.merge(value1, value2);
    }

    private boolean isRef(Value v) {
        return v == BasicValue.REFERENCE_VALUE || v == NULL_VALUE || v == MAYBE_NULL_VALUE;
    }
}
