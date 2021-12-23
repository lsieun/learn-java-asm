package lsieun.asm.core.info;

import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfoClassVisitor extends ClassVisitor {
    public InfoClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String line = String.format("ClassVisitor.visit(%d, %s, %s, %s, %s, %s);", version, getAccess(access), name, signature, superName, Arrays.toString(interfaces));
        System.out.println(line);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        String line = String.format("ClassVisitor.visitAttribute(%s);", attribute);
        System.out.println(line);

        super.visitAttribute(attribute);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        String line = String.format("ClassVisitor.visitField(%s, %s, %s, %s, %s);", getAccess(access), name, descriptor, signature, value);
        System.out.println(line);

        FieldVisitor fv = super.visitField(access, name, descriptor, signature, value);
        return new InfoFieldVisitor(api, fv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        String line = String.format("ClassVisitor.visitMethod(%s, %s, %s, %s, %s);", getAccess(access), name, descriptor, signature, Arrays.toString(exceptions));
        System.out.println(line);

        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new InfoMethodVisitor(api, mv);
    }

    @Override
    public void visitEnd() {
        String line = String.format("ClassVisitor.visitEnd();");
        System.out.println(line);
        super.visitEnd();
    }

    private String getAccess(int access) {
        List<String> list = new ArrayList<>();
        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            list.add("ACC_PUBLIC");
        }
        else if ((access & Opcodes.ACC_PROTECTED) != 0) {
            list.add("ACC_PROTECTED");
        }
        else if ((access & Opcodes.ACC_PRIVATE) != 0) {
            list.add("ACC_PRIVATE");
        }

        if ((access & Opcodes.ACC_STATIC) != 0) {
            list.add("ACC_STATIC");
        }
        if ((access & Opcodes.ACC_FINAL) != 0) {
            list.add("ACC_FINAL");
        }
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            list.add("ACC_NATIVE");
        }
        if ((access & Opcodes.ACC_INTERFACE) != 0) {
            list.add("ACC_INTERFACE");
        }
        if ((access & Opcodes.ACC_ABSTRACT) != 0) {
            list.add("ACC_ABSTRACT");
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            list.add("ACC_SYNTHETIC");
        }
        return list.toString();
    }
}
