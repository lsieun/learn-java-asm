package lsieun.asm.analysis.state;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;

import java.util.List;

public class StateInterpreter extends Interpreter<StateValue> implements Opcodes {
    public StateInterpreter() {
        super(ASM9);
        if (getClass() != StateInterpreter.class) {
            throw new IllegalStateException();
        }
    }

    public StateInterpreter(int api) {
        super(api);
    }

    @Override
    public StateValue newValue(Type type) {
        return null;
    }

    @Override
    public StateValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        return null;
    }

    @Override
    public StateValue copyOperation(AbstractInsnNode insn, StateValue value) throws AnalyzerException {
        return null;
    }

    @Override
    public StateValue unaryOperation(AbstractInsnNode insn, StateValue value) throws AnalyzerException {
        return null;
    }

    @Override
    public StateValue binaryOperation(AbstractInsnNode insn, StateValue value1, StateValue value2) throws AnalyzerException {
        return null;
    }

    @Override
    public StateValue ternaryOperation(AbstractInsnNode insn, StateValue value1, StateValue value2, StateValue value3) throws AnalyzerException {
        return null;
    }

    @Override
    public StateValue naryOperation(AbstractInsnNode insn, List<? extends StateValue> values) throws AnalyzerException {
        return null;
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, StateValue value, StateValue expected) throws AnalyzerException {

    }

    @Override
    public StateValue merge(StateValue value1, StateValue value2) {
        return null;
    }
}
