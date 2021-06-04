package lsieun.asm.core.counter;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodCounterAdapter extends MethodVisitor {
    private final String owner;
    private final String fieldName;

    public MethodCounterAdapter(int api, MethodVisitor mv, String owner, String fieldName) {
        super(api, mv);
        this.owner = owner;
        this.fieldName = fieldName;
    }

    @Override
    public void visitCode() {
        super.visitFieldInsn(GETSTATIC, owner, fieldName, "I");
        super.visitInsn(ICONST_1);
        super.visitInsn(IADD);
        super.visitFieldInsn(PUTSTATIC, owner, fieldName, "I");
        super.visitCode();
    }
}
