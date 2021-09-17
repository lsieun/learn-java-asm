package lsieun.asm.analysis;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AnalyzerAdapter;

import static org.objectweb.asm.Opcodes.*;

public class RemoveUnusedCastVisitor extends ClassVisitor {
    private String owner;

    public RemoveUnusedCastVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && !name.equals("<init>")) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                RemoveUnusedCastAdapter adapter = new RemoveUnusedCastAdapter(api, mv);
                adapter.aa = new AnalyzerAdapter(owner, access, name, descriptor, adapter);
                return adapter.aa;
            }
        }

        return mv;
    }

    private static class RemoveUnusedCastAdapter extends MethodVisitor {
        public AnalyzerAdapter aa;

        public RemoveUnusedCastAdapter(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            // 首先，处理自己的代码逻辑
            if (opcode == CHECKCAST) {
                Class<?> to = getClass(type);
                if (aa.stack != null && aa.stack.size() > 0) {
                    Object operand = aa.stack.get(aa.stack.size() - 1);
                    if (operand instanceof String) {
                        Class<?> from = getClass((String) operand);
                        if (to.isAssignableFrom(from)) {
                            return;
                        }
                    }
                }
            }

            // 其次，调用父类的方法实现
            super.visitTypeInsn(opcode, type);
        }

        private static Class<?> getClass(String desc) {
            try {
                return Class.forName(desc.replace('/', '.'));
            }
            catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex.toString());
            }
        }
    }
}
