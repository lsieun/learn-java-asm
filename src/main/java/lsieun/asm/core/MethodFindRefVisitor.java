package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;

public class MethodFindRefVisitor extends ClassVisitor {
    private final String methodOwner;
    private final String methodName;
    private final String methodDesc;

    private String currentOwner;

    public MethodFindRefVisitor(int api, ClassVisitor classVisitor, String methodOwner, String methodName, String methodDesc) {
        super(api, classVisitor);
        this.methodOwner = methodOwner;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.currentOwner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
        boolean isNativeMethod = (access & ACC_NATIVE) != 0;
        if (!isAbstractMethod && !isNativeMethod) {
            return new MethodFindRefAdaptor(api, null, name, descriptor);
        }
        return null;
    }

    private class MethodFindRefAdaptor extends MethodVisitor {
        private final List<String> list = new ArrayList<>();
        private final String currentMethodName;
        private final String currentMethodDesc;

        public MethodFindRefAdaptor(int api, MethodVisitor methodVisitor, String currentMethodName, String currentMethodDesc) {
            super(api, methodVisitor);
            this.currentMethodName = currentMethodName;
            this.currentMethodDesc = currentMethodDesc;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            // 首先，处理自己的代码逻辑
            if (methodOwner.equals(owner) && methodName.equals(name) && methodDesc.equals(descriptor)) {
                String info = String.format("%s.%s%s", currentOwner, currentMethodName, currentMethodDesc);
                if (!list.contains(info)) {
                    list.add(info);
                }
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
