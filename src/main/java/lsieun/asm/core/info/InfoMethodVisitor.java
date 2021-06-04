package lsieun.asm.core.info;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.Printer;

public class InfoMethodVisitor extends MethodVisitor {
    public InfoMethodVisitor(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    @Override
    public void visitCode() {
        String line = String.format("    MethodVisitor.visitCode();");
        System.out.println(line);
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        String line = String.format("    MethodVisitor.visitInsn(%s);", Printer.OPCODES[opcode]);
        System.out.println(line);
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        String line = String.format("    MethodVisitor.visitIntInsn(%s, %s);", Printer.OPCODES[opcode], operand);
        System.out.println(line);
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        String line = String.format("    MethodVisitor.visitVarInsn(%s, %s);", Printer.OPCODES[opcode], var);
        System.out.println(line);
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        String line = String.format("    MethodVisitor.visitTypeInsn(%s, %s);", Printer.OPCODES[opcode], type);
        System.out.println(line);
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        String line = String.format("    MethodVisitor.visitFieldInsn(%s, %s, %s, %s);", Printer.OPCODES[opcode], owner, name, descriptor);
        System.out.println(line);
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        String line = String.format("    MethodVisitor.visitMethodInsn(%s, %s, %s, %s, %s);", Printer.OPCODES[opcode], owner, name, descriptor, isInterface);
        System.out.println(line);
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        String line = String.format("    MethodVisitor.visitJumpInsn(%s, %s);", Printer.OPCODES[opcode], label);
        System.out.println(line);
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        String line = String.format("    MethodVisitor.visitLabel(%s);", label);
        System.out.println(line);
        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object value) {
        String line = String.format("    MethodVisitor.visitLdcInsn(%s);", value);
        System.out.println(line);
        super.visitLdcInsn(value);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        String line = String.format("    MethodVisitor.visitIincInsn(%s, %s);", var, increment);
        System.out.println(line);
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        String line = String.format("    MethodVisitor.visitMaxs(%s, %s);", maxStack, maxLocals);
        System.out.println(line);
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        String line = String.format("    MethodVisitor.visitEnd();");
        System.out.println(line);
        super.visitEnd();
    }
}
