package lsieun.asm.analysis;

import lsieun.cst.Const;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

public class NullabilityInterpreter extends BasicInterpreter {
    public static final BasicValue UNKNOWN_VALUE = BasicValue.REFERENCE_VALUE;
    public static final BasicValue NOT_NULL_VALUE = new BasicValue(Type.getObjectType("not-null-value"));
    public static final BasicValue NULL_VALUE = new BasicValue(Type.getObjectType("null-value"));
    public static final BasicValue NULLABLE_VALUE = new BasicValue(Type.getObjectType("nullable-value"));

    public NullabilityInterpreter(int api) {
        super(api);
    }

    @Override
    public BasicValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        // 首先，处理自己的代码逻辑
        if (insn.getOpcode() == Opcodes.ACONST_NULL) {
            return NULL_VALUE;
        }

        // 其次，调用父类的方法实现
        return super.newOperation(insn);
    }

    @Override
    public BasicValue unaryOperation(AbstractInsnNode insn, BasicValue value) throws AnalyzerException {
        return super.unaryOperation(insn, value);
    }

    @Override
    public BasicValue merge(BasicValue value1, BasicValue value2) {
        BasicValue result;
        if (isRef(value1) && isRef(value2)) {
            if (value1 == value2) {
                // unknown + unknown = unknown
                // not-null + not-null = not-null
                // null + null = null
                // nullable + nullable = nullable
                assert (value1 == UNKNOWN_VALUE) ||
                        (value1 == NOT_NULL_VALUE) ||
                        (value1 == NULL_VALUE) ||
                        (value1 == NULLABLE_VALUE) :
                        String.format("value1: %s, value2: %s", value1, value2);
                result = value1;
            }
            else if (isNullable(value1) || isNullable(value2)) {
                result = NULLABLE_VALUE;
            }
            else if (isNull(value1) && isNotNull(value2)) {
                result = NULLABLE_VALUE;
            }
            else if (isNotNull(value1) && isNull(value2)) {
                result = NULLABLE_VALUE;
            }
            else if (isNotNull(value1) || isNotNull(value2)) {
                result = NOT_NULL_VALUE;
            }
            else if (isNull(value1) || isNull(value2)) {
                result = NULL_VALUE;
            }
            else {
                throw new RuntimeException(String.format("value1: %s, value2: %s", value1, value2));
            }
        }
        else {
            result = super.merge(value1, value2);
        }

        if (Const.DEBUG) {
            String line = String.format("[DEBUG] merge: %s + %s = %s", value1, value2, result);
            System.out.println(line);
        }
        return result;
    }

    private boolean isRef(BasicValue value) {
        return (value == UNKNOWN_VALUE) || (value == NOT_NULL_VALUE) || (value == NULL_VALUE) || (value == NULLABLE_VALUE);
    }

    private boolean isUnknown(BasicValue value) {
        return (value == UNKNOWN_VALUE);
    }

    private boolean isNotNull(BasicValue value) {
        return (value == NOT_NULL_VALUE);
    }

    private boolean isNull(BasicValue value) {
        return (value == NULL_VALUE);
    }

    private boolean isNullable(BasicValue value) {
        return (value == NULLABLE_VALUE);
    }
}
