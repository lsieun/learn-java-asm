package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassGetAttributeContentVisitor extends ClassVisitor {
    private final StringBuilder attr = new StringBuilder();

    public ClassGetAttributeContentVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        attr.append(name);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        attr.append(name);
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        attr.append(name);
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public String getAttributeContent() {
        return attr.toString();
    }
}
