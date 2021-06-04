package lsieun.asm.core.counter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class ClassCounterVisitor extends ClassVisitor {
    private String owner;
    private boolean isInterface;

    public ClassCounterVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        owner = name;
        isInterface = (access & ACC_INTERFACE) != 0;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (!isInterface && mv != null && !name.equals("<init>")) {
            String fieldName = name + "_count";
            mv = new MethodCounterAdapter(api, mv, owner, fieldName);

            FieldVisitor fv = super.visitField(ACC_PUBLIC | ACC_STATIC, fieldName, "I", null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        return mv;
    }
}
