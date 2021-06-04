package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class FieldAccessAdapter extends ClassVisitor {
    private final List<Info> list;

    public FieldAccessAdapter(int api, ClassVisitor cv, List<Info> list) {
        super(api, cv);
        this.list = list;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null) {
            mv = new FieldAccessConverter(api, mv, list);
        }
        return mv;
    }
}
