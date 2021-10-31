package lsieun.asm.analysis.nullability;

public enum Nullability {
    UNKNOWN(0),
    NOT_NULL(1),
    NULL(1),
    NULLABLE(2);

    public final int priority;

    Nullability(int priority) {
        this.priority = priority;
    }

    public static Nullability merge(Nullability value1, Nullability value2) {
        // 第一种情况，两者相等，则直接返回一个
        if (value1 == value2) {
            return value1;
        }

        // 第二种情况，两者不相等，比较优先级大小，谁大返回谁
        int priority1 = value1.priority;
        int priority2 = value2.priority;
        if (priority1 > priority2) {
            return value1;
        }
        else if (priority1 < priority2) {
            return value2;
        }

        // 第三种情况，两者不相等，但优先级相等，则一个是NOT_NULL，另一个是NULL
        return NULLABLE;
    }
}
