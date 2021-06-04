package lsieun.asm.tutorial.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

public class AddFieldAdapter extends ClassVisitor {
    private final int fieldAccess;
    private final String fieldName;
    private final String fieldDesc;
    private boolean isFieldPresent;

    public AddFieldAdapter(int api, ClassVisitor classVisitor, int fieldAccess, String fieldName, String fieldDesc) {
        super(api, classVisitor);
        this.fieldAccess = fieldAccess;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (name.equals(fieldName)) {
            isFieldPresent = true;
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
        if (!isFieldPresent) {
            FieldVisitor fv = cv.visitField(fieldAccess, fieldName, fieldDesc, null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        super.visitEnd();
    }
}
