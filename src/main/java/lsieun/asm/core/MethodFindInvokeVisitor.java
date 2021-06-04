package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.Printer;

import java.util.ArrayList;
import java.util.List;

public class MethodFindInvokeVisitor extends ClassVisitor {
    private final String methodName;
    private final String methodDesc;

    public MethodFindInvokeVisitor(int api, ClassVisitor classVisitor, String methodName, String methodDesc) {
        super(api, classVisitor);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (methodName.equals(name) && methodDesc.equals(descriptor)) {
            return new MethodFindInvokeAdapter(api, null);
        }
        return null;
    }

    private static class MethodFindInvokeAdapter extends MethodVisitor {
        private final List<String> list = new ArrayList<>();

        public MethodFindInvokeAdapter(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            // 首先，处理自己的代码逻辑
            String info = String.format("%s %s.%s%s", Printer.OPCODES[opcode], owner, name, descriptor);
            if (!list.contains(info)) {
                list.add(info);
            }

            // 其次，调用父类的方法实现
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitEnd() {
            // 首先，处理自己的代码逻辑
            for (String item : list) {
                System.out.println(item);
            }

            // 其次，调用父类的方法实现
            super.visitEnd();
        }
    }
}
