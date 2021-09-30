package lsieun.asm.template;

import org.objectweb.asm.*;

public class ClassAddAnnotationVisitor extends ClassVisitor {
    private final String annotationDesc;
    private boolean isAnnotationPresent;

    public ClassAddAnnotationVisitor(int api, ClassVisitor classVisitor, String annotationDesc) {
        super(api, classVisitor);
        this.annotationDesc = annotationDesc;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (visible && descriptor.equals(annotationDesc)) {
            isAnnotationPresent = true;
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public void visitNestMember(String nestMember) {
        addAnnotation();
        super.visitNestMember(nestMember);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        addAnnotation();
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        addAnnotation();
        return super.visitRecordComponent(name, descriptor, signature);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        addAnnotation();
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        addAnnotation();
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        addAnnotation();
        super.visitEnd();
    }

    private void addAnnotation() {
        if (!isAnnotationPresent) {
            AnnotationVisitor av = super.visitAnnotation(annotationDesc, true);
            if (av != null) {
                av.visitEnd();
            }
            isAnnotationPresent = true;
        }
    }
}
