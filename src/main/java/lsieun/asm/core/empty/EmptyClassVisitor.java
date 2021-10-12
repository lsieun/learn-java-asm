package lsieun.asm.core.empty;

import org.objectweb.asm.*;

public class EmptyClassVisitor extends ClassVisitor {
    public EmptyClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        ModuleVisitor mv = super.visitModule(name, access, version);
        return new EmptyModuleVisitor(api, mv);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(descriptor, visible);
        return new EmptyAnnotationVisitor(api, av);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        AnnotationVisitor av = super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
        return new EmptyAnnotationVisitor(api, av);
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        RecordComponentVisitor rcv = super.visitRecordComponent(name, descriptor, signature);
        return new EmptyRecordComponentVisitor(api, rcv);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        FieldVisitor fv = super.visitField(access, name, descriptor, signature, value);
        return new EmptyFieldVisitor(api, fv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new EmptyMethodVisitor(api, mv);
    }

}
