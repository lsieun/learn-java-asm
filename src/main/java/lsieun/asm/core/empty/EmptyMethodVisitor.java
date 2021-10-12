package lsieun.asm.core.empty;

import org.objectweb.asm.MethodVisitor;

public class EmptyMethodVisitor extends MethodVisitor {
    public EmptyMethodVisitor(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }
}
