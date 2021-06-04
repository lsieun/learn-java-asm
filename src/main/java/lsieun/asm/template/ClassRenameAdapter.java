package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassRenameAdapter extends ClassVisitor {
    private final String oldOwner;
    private final String newOwner;

    public ClassRenameAdapter(int api, ClassVisitor classVisitor, String oldOwner, String newOwner) {
        super(api, classVisitor);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (name.equals(oldOwner)) {
            super.visit(version, access, newOwner, signature, superName, interfaces);
        }
        else {
            super.visit(version, access, name, signature, superName, interfaces);
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        String oldDesc = getDescriptor(oldOwner);
        if (descriptor.contains(oldDesc)) {
            String newDesc = getDescriptor(newOwner);
            String desc = descriptor.replaceAll(oldDesc, newDesc);
            return super.visitField(access, name, desc, signature, value);
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        String desc = descriptor;
        String oldDesc = getDescriptor(oldOwner);
        if (descriptor.contains(oldDesc)) {
            String newDesc = getDescriptor(newOwner);
            desc = descriptor.replaceAll(oldDesc, newDesc);
        }
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) {
            mv = new RefRenameAdapter(api, mv, oldOwner, newOwner);
        }
        return mv;
    }

    public String getDescriptor(String internalName) {
        return String.format("L%s;", internalName);
    }
}
