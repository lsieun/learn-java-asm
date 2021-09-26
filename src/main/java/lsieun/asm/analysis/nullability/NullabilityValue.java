package lsieun.asm.analysis.nullability;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.Value;

public class NullabilityValue implements Value {
    // primitive types
    public static final NullabilityValue INT_VALUE = new NullabilityValue(Type.INT_TYPE);
    public static final NullabilityValue FLOAT_VALUE = new NullabilityValue(Type.FLOAT_TYPE);
    public static final NullabilityValue LONG_VALUE = new NullabilityValue(Type.LONG_TYPE);
    public static final NullabilityValue DOUBLE_VALUE = new NullabilityValue(Type.DOUBLE_TYPE);

    // reference types
    public static final NullabilityValue UNKNOWN_VALUE = new NullabilityValue(NullabilityType.UNKNOWN_TYPE); // TODO: 应该没有一种具体的unknown type
    public static final NullabilityValue NOT_NULL_VALUE = new NullabilityValue(NullabilityType.NOT_NULL_TYPE);
    public static final NullabilityValue NULL_VALUE = new NullabilityValue(NullabilityType.NULL_TYPE);
    public static final NullabilityValue NULLABLE_VALUE = new NullabilityValue(NullabilityType.NULLABLE_TYPE);

    // special types
    // TODO: 这个字段有用吗？
    public static final NullabilityValue UNINITIALIZED_VALUE = new NullabilityValue(null);
    // TODO: 这个字段有用吗？我觉得，可以用INT_VALUE代替
    public static final NullabilityValue RETURN_ADDRESS_VALUE = new NullabilityValue(Type.VOID_TYPE);


    private final Type type;

    public NullabilityValue(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int getSize() {
        return type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE ? 2 : 1;
    }


    // TODO: 这个方法要修改
    public boolean isReference() {
        return type != null && (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY);
    }

    @Override
    public boolean equals(final Object value) {
        if (value == this) {
            return true;
        }
        else if (value instanceof NullabilityValue) {
            if (type == null) {
                return ((NullabilityValue) value).type == null;
            }
            else {
                return type.equals(((NullabilityValue) value).type);
            }
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return type == null ? 0 : type.hashCode();
    }

    @Override
    public String toString() {
        if (this == INT_VALUE) {
            return "int";
        }
        else if (this == FLOAT_VALUE) {
            return "float";
        }
        else if (this == LONG_VALUE) {
            return "long";
        }
        else if (this == DOUBLE_VALUE) {
            return "double";
        }
        else if (this == UNKNOWN_VALUE) {
            return "unknown";
        }
        else if (this == NOT_NULL_VALUE) {
            return "not-null";
        }
        else if (this == NULL_VALUE) {
            return "null";
        }
        else if (this == NULLABLE_VALUE) {
            return "nullable";
        }
        else if (this == UNINITIALIZED_VALUE) {
            return ".";
        }
        else if (this == RETURN_ADDRESS_VALUE) {
            return "address";
        }
        else if (isReference()) {
            return type.getClassName();
        }
        else {
            String message = String.format("illegal value: %s", type);
            throw new RuntimeException(message);
        }
    }
}
