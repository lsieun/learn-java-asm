package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodFinallyAdapter extends ClassVisitor {
    public MethodFinallyAdapter(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && "main".equals(name)) {
            mv = new MethodFinallyConverter(api, mv, access, name, descriptor);
        }
        return mv;
    }
}
