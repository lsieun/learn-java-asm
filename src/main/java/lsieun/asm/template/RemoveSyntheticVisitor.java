package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RemoveSyntheticVisitor extends ClassVisitor {
    public RemoveSyntheticVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access & (~Opcodes.ACC_SYNTHETIC), name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access & (~Opcodes.ACC_SYNTHETIC), name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return super.visitMethod(access & ~(Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE), name, descriptor, signature, exceptions);
    }
}
