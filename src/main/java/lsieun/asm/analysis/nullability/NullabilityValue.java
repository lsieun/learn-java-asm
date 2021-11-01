package lsieun.asm.analysis.nullability;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.Value;

public class NullabilityValue implements Value {
    private final Type type;
    private Nullability state;

    public NullabilityValue(Type type) {
        this(type, Nullability.UNKNOWN);
    }

    public NullabilityValue(Type type, Nullability state) {
        this.type = type;
        this.state = state;
    }

    public Type getType() {
        return type;
    }

    public void setState(Nullability state) {
        this.state = state;
    }

    public Nullability getState() {
        return state;
    }

    @Override
    public int getSize() {
        return type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE ? 2 : 1;
    }

    public boolean isReference() {
        return type != null && (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY);
    }

    @Override
    public boolean equals(final Object value) {
        if (value == this) {
            return true;
        }
        else if (value instanceof NullabilityValue) {
            NullabilityValue another = (NullabilityValue) value;
            if (type == null) {
                return ((NullabilityValue) value).type == null;
            }
            else {
                return type.equals(((NullabilityValue) value).type) && state == another.state;
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
}
