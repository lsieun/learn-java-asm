package lsieun.asm.analysis.nullability;

import org.objectweb.asm.Type;

public class NullabilityType {
    public static final Type UNKNOWN_TYPE = Type.getObjectType("unknown");
    public static final Type NOT_NULL_TYPE = Type.getObjectType("not-null");
    public static final Type NULL_TYPE = Type.getObjectType("null");
    public static final Type NULLABLE_TYPE = Type.getObjectType("nullable");
}
