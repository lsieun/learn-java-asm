package lsieun.asm.analysis.nullability;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;

import java.util.List;

public class NullabilityInterpreter extends Interpreter<NullabilityValue> implements Opcodes {
    public static final Type NULL_TYPE = Type.getObjectType("null");

    public static final NullabilityValue UNINITIALIZED_VALUE = new NullabilityValue(null);
    public static final NullabilityValue RETURN_ADDRESS_VALUE = new NullabilityValue(Type.VOID_TYPE);

    private final ClassLoader loader = getClass().getClassLoader();

    public NullabilityInterpreter(int api) {
        super(api);
    }

    @Override
    public NullabilityValue newValue(Type type) {
        if (type == null) {
            return UNINITIALIZED_VALUE;
        }

        int sort = type.getSort();
        if (sort == Type.VOID) {
            return null;
        }
        return new NullabilityValue(type);
    }

    @Override
    public NullabilityValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        switch (insn.getOpcode()) {
            case ACONST_NULL:
                return new NullabilityValue(NULL_TYPE, Nullability.NULL);
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case BIPUSH:
            case SIPUSH:
                return newValue(Type.INT_TYPE);
            case LCONST_0:
            case LCONST_1:
                return newValue(Type.LONG_TYPE);
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
                return newValue(Type.FLOAT_TYPE);
            case DCONST_0:
            case DCONST_1:
                return newValue(Type.DOUBLE_TYPE);
            case LDC:
                Object value = ((LdcInsnNode) insn).cst;
                if (value instanceof Integer) {
                    return newValue(Type.INT_TYPE);
                }
                else if (value instanceof Float) {
                    return newValue(Type.FLOAT_TYPE);
                }
                else if (value instanceof Long) {
                    return newValue(Type.LONG_TYPE);
                }
                else if (value instanceof Double) {
                    return newValue(Type.DOUBLE_TYPE);
                }
                else if (value instanceof String) {
                    return new NullabilityValue(Type.getObjectType("java/lang/String"), Nullability.NOT_NULL);
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
                return newValue(Type.INT_TYPE);
            case FNEG:
            case I2F:
            case L2F:
            case D2F:
                return newValue(Type.FLOAT_TYPE);
            case LNEG:
            case I2L:
            case F2L:
            case D2L:
                return newValue(Type.LONG_TYPE);
            case DNEG:
            case I2D:
            case L2D:
            case F2D:
                return newValue(Type.DOUBLE_TYPE);
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
                return newValue(Type.INT_TYPE);
            case FALOAD:
            case FADD:
            case FSUB:
            case FMUL:
            case FDIV:
            case FREM:
                return newValue(Type.FLOAT_TYPE);
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
                return newValue(Type.LONG_TYPE);
            case DALOAD:
            case DADD:
            case DSUB:
            case DMUL:
            case DDIV:
            case DREM:
                return newValue(Type.LONG_TYPE);
            case AALOAD:
                return getElementValue(value1);
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
        // 合并两者的状态
        Nullability mergedState = Nullability.merge(value1.getState(), value2.getState());


        // 第一种情况，两个value的类型相同且状态（state）相同
        if (value1.equals(value2)) {
            return value1;
        }

        // 第二种情况，两个value的类型相同，但状态（state）不同，需要合并它们的状态（state）
        Type type1 = value1.getType();
        Type type2 = value2.getType();
        if (type1 != null && type1.equals(type2)) {
            Type type = value1.getType();
            return new NullabilityValue(type, mergedState);
        }

        // 第三种情况，两个value的类型不相同的，而且要合并它们的状态（state）
        if (type1 != null
                && (type1.getSort() == Type.OBJECT || type1.getSort() == Type.ARRAY)
                && type2 != null
                && (type2.getSort() == Type.OBJECT || type2.getSort() == Type.ARRAY)) {
            if (type1.equals(NULL_TYPE)) {
                return new NullabilityValue(type2, mergedState);
            }
            if (type2.equals(NULL_TYPE)) {
                return new NullabilityValue(type1, mergedState);
            }
            if (isAssignableFrom(type1, type2)) {
                return new NullabilityValue(type1, mergedState);
            }
            if (isAssignableFrom(type2, type1)) {
                return new NullabilityValue(type2, mergedState);
            }
            int numDimensions = 0;
            if (type1.getSort() == Type.ARRAY
                    && type2.getSort() == Type.ARRAY
                    && type1.getDimensions() == type2.getDimensions()
                    && type1.getElementType().getSort() == Type.OBJECT
                    && type2.getElementType().getSort() == Type.OBJECT) {
                numDimensions = type1.getDimensions();
                type1 = type1.getElementType();
                type2 = type2.getElementType();
            }


            while (true) {
                if (type1 == null || isInterface(type1)) {
                    NullabilityValue arrayValue = newArrayValue(Type.getObjectType("java/lang/Object"), numDimensions);
                    return new NullabilityValue(arrayValue.getType(), mergedState);
                }
                type1 = getSuperClass(type1);
                if (isAssignableFrom(type1, type2)) {
                    NullabilityValue arrayValue = newArrayValue(type1, numDimensions);
                    return new NullabilityValue(arrayValue.getType(), mergedState);
                }
            }
        }
        return UNINITIALIZED_VALUE;
    }

    protected boolean isInterface(final Type type) {
        return getClass(type).isInterface();
    }

    protected Type getSuperClass(final Type type) {
        Class<?> superClass = getClass(type).getSuperclass();
        return superClass == null ? null : Type.getType(superClass);
    }

    private NullabilityValue newArrayValue(final Type type, final int dimensions) {
        if (dimensions == 0) {
            return newValue(type);
        }
        else {
            StringBuilder descriptor = new StringBuilder();
            for (int i = 0; i < dimensions; ++i) {
                descriptor.append('[');
            }
            descriptor.append(type.getDescriptor());
            return newValue(Type.getType(descriptor.toString()));
        }
    }

    protected NullabilityValue getElementValue(final NullabilityValue objectArrayValue) {
        Type arrayType = objectArrayValue.getType();
        if (arrayType != null) {
            if (arrayType.getSort() == Type.ARRAY) {
                return newValue(Type.getType(arrayType.getDescriptor().substring(1)));
            }
            else if (arrayType.equals(NULL_TYPE)) {
                return objectArrayValue;
            }
        }
        throw new AssertionError();
    }

    protected boolean isSubTypeOf(final NullabilityValue value, final NullabilityValue expected) {
        Type expectedType = expected.getType();
        Type type = value.getType();
        switch (expectedType.getSort()) {
            case Type.INT:
            case Type.FLOAT:
            case Type.LONG:
            case Type.DOUBLE:
                return type.equals(expectedType);
            case Type.ARRAY:
            case Type.OBJECT:
                if (type.equals(NULL_TYPE)) {
                    return true;
                }
                else if (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
                    if (isAssignableFrom(expectedType, type)) {
                        return true;
                    }
                    else if (getClass(expectedType).isInterface()) {
                        // The merge of class or interface types can only yield class types (because it is not
                        // possible in general to find an unambiguous common super interface, due to multiple
                        // inheritance). Because of this limitation, we need to relax the subtyping check here
                        // if 'value' is an interface.
                        return Object.class.isAssignableFrom(getClass(type));
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            default:
                throw new AssertionError();
        }
    }

    protected boolean isAssignableFrom(final Type type1, final Type type2) {
        if (type1.equals(type2)) {
            return true;
        }
        return getClass(type1).isAssignableFrom(getClass(type2));
    }

    protected Class<?> getClass(final Type type) {
        try {
            if (type.getSort() == Type.ARRAY) {
                return Class.forName(type.getDescriptor().replace('/', '.'), false, loader);
            }
            return Class.forName(type.getClassName(), false, loader);
        }
        catch (ClassNotFoundException e) {
            throw new TypeNotPresentException(e.toString(), e);
        }
    }
}
