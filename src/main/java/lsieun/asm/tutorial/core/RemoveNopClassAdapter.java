package lsieun.asm.tutorial.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class RemoveNopClassAdapter extends ClassVisitor {
    public RemoveNopClassAdapter(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if(mv != null && !name.equals("<init>")) {
            mv = new RemoveNopMethodAdapter(api, mv);
        }
        return mv;
    }
}
