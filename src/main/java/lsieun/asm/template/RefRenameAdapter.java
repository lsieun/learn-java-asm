package lsieun.asm.template;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class RefRenameAdapter extends MethodVisitor {
    private final String oldOwner;
    private final String newOwner;

    public RefRenameAdapter(int api, MethodVisitor mv, String oldOwner, String newOwner) {
        super(api, mv);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    @Override
    public void visitLdcInsn(Object value) {
        if (value instanceof Type) {
            Type t = Type.getType(getDescriptor(oldOwner));
            if (value.equals(t)) {
                super.visitLdcInsn(Type.getType(getDescriptor(newOwner)));
                return;
            }
        }
        super.visitLdcInsn(value);
    }

    public String getDescriptor(String internalName) {
        return String.format("L%s;", internalName);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (owner.equals(this.oldOwner)) {
            super.visitFieldInsn(opcode, newOwner, name, descriptor);
        }
        else {
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (owner.equals(this.oldOwner)) {
            super.visitMethodInsn(opcode, newOwner, name, descriptor, isInterface);
        }
        else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
