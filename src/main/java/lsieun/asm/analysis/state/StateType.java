package lsieun.asm.analysis.state;

public enum StateType {
    DEFAULT, // default
    TOP,
    INT,
    FLOAT,
    LONG,
    DOUBLE,
    NULL,

    CP, // constant pool
    HEAP; // 从堆内存上分配的对象
}
