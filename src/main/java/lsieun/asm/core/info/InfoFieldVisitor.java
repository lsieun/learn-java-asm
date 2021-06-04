package lsieun.asm.core.info;

import org.objectweb.asm.FieldVisitor;

public class InfoFieldVisitor extends FieldVisitor {
    public InfoFieldVisitor(int api, FieldVisitor fieldVisitor) {
        super(api, fieldVisitor);
    }

    @Override
    public void visitEnd() {
        String line = String.format("    FieldVisitor.visitEnd();");
        System.out.println(line);
        super.visitEnd();
    }
}
