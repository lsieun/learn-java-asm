package lsieun.asm.analysis.nullability;

import org.objectweb.asm.Type;

public class NullabilityUtils {
    // reference types
    public static final Type UNKNOWN_TYPE = Type.getObjectType("unknown");
    public static final Type NOT_NULL_TYPE = Type.getObjectType("not-null");
    public static final Type NULL_TYPE = Type.getObjectType("null");
    public static final Type NULLABLE_TYPE = Type.getObjectType("nullable");

    // primitive values
    public static final NullabilityValue INT_VALUE = new NullabilityValue(Type.INT_TYPE);
    public static final NullabilityValue FLOAT_VALUE = new NullabilityValue(Type.FLOAT_TYPE);
    public static final NullabilityValue LONG_VALUE = new NullabilityValue(Type.LONG_TYPE);
    public static final NullabilityValue DOUBLE_VALUE = new NullabilityValue(Type.DOUBLE_TYPE);

    // reference values
    public static final NullabilityValue UNKNOWN_VALUE = new NullabilityValue(NullabilityUtils.UNKNOWN_TYPE); // TODO: 应该没有一种具体的unknown type
    public static final NullabilityValue NOT_NULL_VALUE = new NullabilityValue(NullabilityUtils.NOT_NULL_TYPE);
    public static final NullabilityValue NULL_VALUE = new NullabilityValue(NullabilityUtils.NULL_TYPE);
    public static final NullabilityValue NULLABLE_VALUE = new NullabilityValue(NullabilityUtils.NULLABLE_TYPE);

    // special values
    public static final NullabilityValue UNINITIALIZED_VALUE = new NullabilityValue(null);
    public static final NullabilityValue RETURN_ADDRESS_VALUE = new NullabilityValue(Type.VOID_TYPE);

    public static String getString(NullabilityValue value) {
        if (value == INT_VALUE) {
            return "int";
        }
        else if (value == FLOAT_VALUE) {
            return "float";
        }
        else if (value == LONG_VALUE) {
            return "long";
        }
        else if (value == DOUBLE_VALUE) {
            return "double";
        }
        else if (value == UNKNOWN_VALUE) {
            return "unknown";
        }
        else if (value == NOT_NULL_VALUE) {
            return "not-null";
        }
        else if (value == NULL_VALUE) {
            return "null";
        }
        else if (value == NULLABLE_VALUE) {
            return "nullable";
        }
        else if (value == UNINITIALIZED_VALUE) {
            return ".";
        }
        else if (value == RETURN_ADDRESS_VALUE) {
            return "address";
        }
        else if (value.isReference()) {
            return value.getType().getClassName();
        }
        else {
            String message = String.format("illegal value: %s", value.getType());
            throw new RuntimeException(message);
        }
    }
}
