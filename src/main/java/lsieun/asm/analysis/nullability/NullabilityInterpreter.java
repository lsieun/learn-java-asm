package lsieun.asm.analysis.nullability;

import lsieun.cst.Const;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;

import java.util.List;

import static lsieun.asm.analysis.nullability.NullabilityUtils.*;

public class NullabilityInterpreter extends Interpreter<NullabilityValue> implements Opcodes {
    public NullabilityInterpreter(int api) {
        super(api);
    }

    @Override
    public NullabilityValue newValue(Type type) {
        if (type == null) {
            return UNINITIALIZED_VALUE;
        }

        int sort = type.getSort();
        switch (sort) {
            case Type.VOID:
                return null;
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return INT_VALUE;
            case Type.FLOAT:
                return FLOAT_VALUE;
            case Type.LONG:
                return LONG_VALUE;
            case Type.DOUBLE:
                return DOUBLE_VALUE;
            case Type.ARRAY:
            case Type.OBJECT:
                return new NullabilityValue(type);
            default:
                throw new AssertionError();
        }
    }

    @Override
    public NullabilityValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        switch (insn.getOpcode()) {
            case ACONST_NULL:
                return NULL_VALUE;
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case BIPUSH:
            case SIPUSH:
                return INT_VALUE;
            case LCONST_0:
            case LCONST_1:
                return LONG_VALUE;
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
                return FLOAT_VALUE;
            case DCONST_0:
            case DCONST_1:
                return DOUBLE_VALUE;
            case LDC:
                Object value = ((LdcInsnNode) insn).cst;
                if (value instanceof Integer) {
                    return INT_VALUE;
                }
                else if (value instanceof Float) {
                    return FLOAT_VALUE;
                }
                else if (value instanceof Long) {
                    return LONG_VALUE;
                }
                else if (value instanceof Double) {
                    return DOUBLE_VALUE;
                }
                else if (value instanceof String) {
                    return newValue(Type.getObjectType("java/lang/String"));
                }
                else if (value instanceof Type) {
                    int sort = ((Type) value).getSort();
                    if (sort == Type.OBJECT || sort == Type.ARRAY) {
                        return newValue(Type.getObjectType("java/lang/Class"));
                    }
                    else if (sort == Type.METHOD) {
                        return newValue(Type.getObjectType("java/lang/invoke/MethodType"));
                    }
                    else {
                        throw new AnalyzerException(insn, "Illegal LDC value " + value);
                    }
                }
                else if (value instanceof Handle) {
                    return newValue(Type.getObjectType("java/lang/invoke/MethodHandle"));
                }
                else if (value instanceof ConstantDynamic) {
                    return newValue(Type.getType(((ConstantDynamic) value).getDescriptor()));
                }
                else {
                    throw new AnalyzerException(insn, "Illegal LDC value " + value);
                }
            case JSR:
                return RETURN_ADDRESS_VALUE;
            case GETSTATIC:
                return newValue(Type.getType(((FieldInsnNode) insn).desc));
            case NEW:
                return newValue(Type.getObjectType(((TypeInsnNode) insn).desc));
            default:
                throw new AssertionError();
        }
    }

    @Override
    public NullabilityValue copyOperation(AbstractInsnNode insn, NullabilityValue value) {
        return value;
    }

    @Override
    public NullabilityValue unaryOperation(AbstractInsnNode insn, NullabilityValue value) throws AnalyzerException {
        switch (insn.getOpcode()) {
            case INEG:
            case IINC:
            case L2I:
            case F2I:
            case D2I:
            case I2B:
            case I2C:
            case I2S:
            case ARRAYLENGTH:
            case INSTANCEOF:
                return INT_VALUE;
            case FNEG:
            case I2F:
            case L2F:
            case D2F:
                return FLOAT_VALUE;
            case LNEG:
            case I2L:
            case F2L:
            case D2L:
                return LONG_VALUE;
            case DNEG:
            case I2D:
            case L2D:
            case F2D:
                return DOUBLE_VALUE;
            case GETFIELD:
                return newValue(Type.getType(((FieldInsnNode) insn).desc));
            case NEWARRAY:
                switch (((IntInsnNode) insn).operand) {
                    case T_BOOLEAN:
                        return newValue(Type.getType("[Z"));
                    case T_CHAR:
                        return newValue(Type.getType("[C"));
                    case T_BYTE:
                        return newValue(Type.getType("[B"));
                    case T_SHORT:
                        return newValue(Type.getType("[S"));
                    case T_INT:
                        return newValue(Type.getType("[I"));
                    case T_FLOAT:
                        return newValue(Type.getType("[F"));
                    case T_DOUBLE:
                        return newValue(Type.getType("[D"));
                    case T_LONG:
                        return newValue(Type.getType("[J"));
                    default:
                        break;
                }
                throw new AnalyzerException(insn, "Invalid array type");
            case ANEWARRAY:
                return newValue(Type.getType("[" + Type.getObjectType(((TypeInsnNode) insn).desc)));
            case CHECKCAST:
                return value;
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case TABLESWITCH:
            case LOOKUPSWITCH:
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case PUTSTATIC:
            case ATHROW:
            case MONITORENTER:
            case MONITOREXIT:
            case IFNULL:
            case IFNONNULL:
                return null;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public NullabilityValue binaryOperation(AbstractInsnNode insn,
                                            NullabilityValue value1,
                                            NullabilityValue value2) {
        switch (insn.getOpcode()) {
            case IALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
            case IADD:
            case ISUB:
            case IMUL:
            case IDIV:
            case IREM:
            case ISHL:
            case ISHR:
            case IUSHR:
            case IAND:
            case IOR:
            case IXOR:
            case LCMP:
            case FCMPL:
            case FCMPG:
            case DCMPL:
            case DCMPG:
                return INT_VALUE;
            case FALOAD:
            case FADD:
            case FSUB:
            case FMUL:
            case FDIV:
            case FREM:
                return FLOAT_VALUE;
            case LALOAD:
            case LADD:
            case LSUB:
            case LMUL:
            case LDIV:
            case LREM:
            case LSHL:
            case LSHR:
            case LUSHR:
            case LAND:
            case LOR:
            case LXOR:
                return LONG_VALUE;
            case DALOAD:
            case DADD:
            case DSUB:
            case DMUL:
            case DDIV:
            case DREM:
                return DOUBLE_VALUE;
            case AALOAD:
                return UNKNOWN_VALUE;
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case PUTFIELD:
                return null;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public NullabilityValue ternaryOperation(AbstractInsnNode insn,
                                             NullabilityValue value1,
                                             NullabilityValue value2,
                                             NullabilityValue value3) {
        return null;
    }

    @Override
    public NullabilityValue naryOperation(AbstractInsnNode insn,
                                          List<? extends NullabilityValue> values) {
        int opcode = insn.getOpcode();
        if (opcode == MULTIANEWARRAY) {
            return newValue(Type.getType(((MultiANewArrayInsnNode) insn).desc));
        }
        else if (opcode == INVOKEDYNAMIC) {
            return newValue(Type.getReturnType(((InvokeDynamicInsnNode) insn).desc));
        }
        else {
            return newValue(Type.getReturnType(((MethodInsnNode) insn).desc));
        }
    }

    @Override
    public void returnOperation(AbstractInsnNode insn,
                                NullabilityValue value,
                                NullabilityValue expected) {
        // Nothing to do.
    }

    @Override
    public NullabilityValue merge(NullabilityValue value1, NullabilityValue value2) {
        NullabilityValue result;
        if (value1.isReference() && value2.isReference()) {
            // 这里的情况是：two reference types
            if (value1.equals(value2)) {
                // unknown + unknown = unknown(特殊性)
                // not-null + not-null = not-null
                // null + null = null
                // nullable + nullable = nullable
                result = value1;
            }
            else if (isNullable(value1) || isNullable(value2)) {
                // nullable + other = nullable
                result = NULLABLE_VALUE;
            }
            else if ((isNull(value1) && isNotNull(value2)) ||
                    (isNotNull(value1) && isNull(value2))) {
                // not-null + null = nullable
                result = NULLABLE_VALUE;
            }
            else if ((isNotNull(value1) && isUnknown(value2)) ||
                    (isUnknown(value1) && isNotNull(value2))) {
                // unknown + not-null = not-null
                result = NOT_NULL_VALUE;
            }
            else if ((isNull(value1) && isUnknown(value2)) ||
                    (isUnknown(value1) && isNull(value2))) {
                // unknown + null = null
                result = NULL_VALUE;
            }
            else if (isUnknown(value1) && isUnknown(value2)) {
                // unknown + unknown = unknown（一般性）
                // NOTE: 这里的处理有点“草率”。
                //       从整体思路上来说，返回一个UNKNOWN_VALUE，不会出现什么错误；
                //  但是，从严谨性角度来说，应该计算value1和value2的共同父类是谁。
                //       为了代码简单，我选择了直接返回UNKNOWN_VALUE
                result = UNKNOWN_VALUE;
            }
            else {
                throw new RuntimeException(String.format("value1: %s, value2: %s", value1, value2));
            }
        }
        else {
            // 这里可能有两种情况：
            // 第一种情况：two primitive types
            // 第二种情况：one primitive type + one reference type
            if (!value1.equals(value2)) {
                result = UNINITIALIZED_VALUE;
            }
            else {
                result = value1;
            }
        }

        if (Const.DEBUG) {
            String line = String.format("[DEBUG] merge: %s + %s = %s", NullabilityUtils.getString(value1), NullabilityUtils.getString(value2), NullabilityUtils.getString(result));
            System.out.println(line);
        }
        return result;
    }

    private boolean isUnknown(NullabilityValue value) {
        return value.isReference() && (value != NOT_NULL_VALUE) && (value != NULL_VALUE) && (value != NULLABLE_VALUE);
    }

    private boolean isNotNull(NullabilityValue value) {
        return (value == NOT_NULL_VALUE);
    }

    private boolean isNull(NullabilityValue value) {
        return (value == NULL_VALUE);
    }

    private boolean isNullable(NullabilityValue value) {
        return (value == NULLABLE_VALUE);
    }
}
