package lsieun.asm.tutorial.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class ChangeVersionClassAdapter extends ClassVisitor {
    public ChangeVersionClassAdapter(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(Opcodes.V1_7, access, name, signature, superName, interfaces);
    }
}
