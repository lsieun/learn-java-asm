package lsieun.asm.core.empty;

import org.objectweb.asm.FieldVisitor;

public class EmptyFieldVisitor extends FieldVisitor {
    public EmptyFieldVisitor(int api, FieldVisitor fieldVisitor) {
        super(api, fieldVisitor);
    }
}
