package lsieun.asm.template;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

// 第一个类
public class ClassPrintAnnotationVisitor extends ClassVisitor {
    private String owner;

    public ClassPrintAnnotationVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        // (1) 调用父类的实现
        AnnotationVisitor av = super.visitAnnotation(descriptor, visible);

        // (2) 添加自己的代码逻辑
        String info = String.format("Class: %s - %s", owner, descriptor);
        System.out.println(info);
        return new AnnotationPrinter(api, av);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        mv = new MethodPrintAnnotationAdapter(api, mv, owner, name, descriptor);
        return mv;
    }

    // 第二个类
    private static class MethodPrintAnnotationAdapter extends MethodVisitor {
        private final String owner;
        private final String methodName;
        private final String methodDesc;


        public MethodPrintAnnotationAdapter(int api, MethodVisitor methodVisitor, String owner, String methodName, String methodDesc) {
            super(api, methodVisitor);
            this.owner = owner;
            this.methodName = methodName;
            this.methodDesc = methodDesc;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            // (1) 调用父类的实现
            AnnotationVisitor av = super.visitAnnotation(descriptor, visible);
            
            // (2) 添加自己的代码逻辑
            String info = String.format("Method: %s.%s:%s - %s", owner, methodName, methodDesc, descriptor);
            System.out.println(info);
            return new AnnotationPrinter(api, av);
        }
    }

    // 第三个类
    private static class AnnotationPrinter extends AnnotationVisitor {
        public AnnotationPrinter(int api, AnnotationVisitor annotationVisitor) {
            super(api, annotationVisitor);
        }

        @Override
        public void visit(String name, Object value) {
            // (1) 添加自己的代码逻辑
            String info = String.format("    %s: %s", name, value);
            System.out.println(info);

            // (2) 调用父类的实现
            super.visit(name, value);
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            // (1) 添加自己的代码逻辑
            String info = String.format("    %s: %s %s", name, descriptor, value);
            System.out.println(info);

            // (2) 调用父类的实现
            super.visitEnum(name, descriptor, value);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            // (1) 添加自己的代码逻辑
            String info = String.format("    %s: %s", name, descriptor);
            System.out.println(info);

            // (2) 调用父类的实现
            return super.visitAnnotation(name, descriptor);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            // (1) 添加自己的代码逻辑
            String info = String.format("    %s", name);
            System.out.println(info);

            // (2) 调用父类的实现
            return super.visitArray(name);
        }
    }
}
