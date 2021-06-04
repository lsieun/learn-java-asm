package lsieun.asm.tutorial.core;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.NOP;

public class RemoveNopMethodAdapter extends MethodVisitor {
    public RemoveNopMethodAdapter(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode != NOP) {
            mv.visitInsn(opcode);
        }
    }
}
