package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

public class ClassRemoveFieldVisitor extends ClassVisitor {
    private final String fieldName;
    private final String fieldDesc;

    public ClassRemoveFieldVisitor(int api, ClassVisitor cv, String fieldName, String fieldDesc) {
        super(api, cv);
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (name.equals(fieldName) && descriptor.equals(fieldDesc)) {
            return null;
        }
        else {
            return super.visitField(access, name, descriptor, signature, value);
        }
    }
}
