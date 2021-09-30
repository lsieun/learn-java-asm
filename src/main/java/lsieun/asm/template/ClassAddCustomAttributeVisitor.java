package lsieun.asm.template;

import lsieun.utils.ByteUtils;
import org.objectweb.asm.*;

public class ClassAddCustomAttributeVisitor extends ClassVisitor {
    private final String attrName;
    private final String attrContent;
    private boolean isAttrPresent;

    public ClassAddCustomAttributeVisitor(int api, ClassVisitor classVisitor, String attrName, String attrContent) {
        super(api, classVisitor);
        this.attrName = attrName;
        this.attrContent = attrContent;
        this.isAttrPresent = false;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        if (attribute.type.equals(attrName)) {
            isAttrPresent = true;
        }
        super.visitAttribute(attribute);
    }

    @Override
    public void visitNestMember(String nestMember) {
        addAttribute();
        super.visitNestMember(nestMember);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        addAttribute();
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        addAttribute();
        return super.visitRecordComponent(name, descriptor, signature);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        addAttribute();
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        addAttribute();
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        addAttribute();
        super.visitEnd();
    }

    private void addAttribute() {
        if (!isAttrPresent) {
            int hashCode = attrContent.hashCode();
            byte[] info = ByteUtils.intToByteArray(hashCode);
            Attribute attr = new CustomAttribute(attrName, info);
            super.visitAttribute(attr);
            isAttrPresent = true;
        }
    }
}
