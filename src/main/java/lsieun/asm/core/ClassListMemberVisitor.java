package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.Formatter;

public class ClassListMemberVisitor extends ClassVisitor {
    private final Formatter fm = new Formatter();

    public ClassListMemberVisitor(int api) {
        super(api);
    }

    public ClassListMemberVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        fm.format("%s extends %s implements %s {%n", name, superName, Arrays.toString(interfaces));
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        fm.format("    %s: %s%n", name, descriptor);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        fm.format("    %s: %s%n", name, descriptor);
        return null;
    }

    @Override
    public void visitEnd() {
        fm.format("}");
    }

    public String getText() {
        return fm.toString();
    }
}
