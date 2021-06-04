package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public abstract class ClassReplaceMethodBodyVisitor extends ClassVisitor {
    protected String owner;
    protected final String methodName;
    protected final String methodDesc;
    private final boolean keepOriginalMethod;

    public ClassReplaceMethodBodyVisitor(int api, ClassVisitor cv, String methodName, String methodDesc) {
        this(api, cv, methodName, methodDesc, true);
    }

    public ClassReplaceMethodBodyVisitor(int api, ClassVisitor cv, String methodName, String methodDesc, boolean keepOriginalMethod) {
        super(api, cv);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.keepOriginalMethod = keepOriginalMethod;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals(methodName) && descriptor.equals(methodDesc)) {
            // 生成新方法（从抽象逻辑上来说，这是第二步；从代码角度来说，先执行）
            generateNewMethod(access, name, descriptor, signature, exceptions);

            if (keepOriginalMethod) {
                // 修改原来方法的名字（从抽象逻辑上来说，这是第一步；从代码角度来说，后执行）
                String newName = getNewName(name);
                return super.visitMethod(access, newName, descriptor, signature, exceptions);
            }
            else {
                // 删除原来的方法
                return null;
            }

        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    protected String getNewName(String name) {
        return String.format("orig$%s", name);
    }

    private void generateNewMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null) {
            generateMethodBody(mv);
        }
    }

    protected abstract void generateMethodBody(MethodVisitor mv);
}
