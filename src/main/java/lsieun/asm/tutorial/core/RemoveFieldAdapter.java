package lsieun.asm.tutorial.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

public class RemoveFieldAdapter extends ClassVisitor {
    private final String fieldName;
    private final String fieldDesc;

    public RemoveFieldAdapter(int api, ClassVisitor classVisitor, String fieldName, String fieldDesc) {
        super(api, classVisitor);
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (name.equals(fieldName) && descriptor.equals(fieldDesc)) {
            return null;
        }
        return super.visitField(access, name, descriptor, signature, value);
    }
}
