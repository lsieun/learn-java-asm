package lsieun.asm.core;

import org.objectweb.asm.*;

public class ClassDecompileVisitor extends ClassVisitor {

    public ClassDecompileVisitor() {
        super(Opcodes.ASM9);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println(name + " extends " + superName + " {");
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        System.out.println("    " + descriptor + " " + name);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println("    " + name + " " + descriptor);
        return null;
    }

    @Override
    public void visitEnd() {
        System.out.println("}");
    }
}
