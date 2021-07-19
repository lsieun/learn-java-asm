package lsieun.asm.template;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

public class CheckMethodAnnotationVisitor extends ClassVisitor {
    // 需要处理的方法放到这里
    public List<String> result = new ArrayList<>();

    public CheckMethodAnnotationVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        mv = new CheckMethodAnnotationAdapter(api, mv, name, descriptor);
        return mv;
    }

    private class CheckMethodAnnotationAdapter extends MethodVisitor {
        private final String methodName;
        private final String methodDesc;

        public CheckMethodAnnotationAdapter(int api, MethodVisitor methodVisitor, String methodName, String methodDesc) {
            super(api, methodVisitor);
            this.methodName = methodName;
            this.methodDesc = methodDesc;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            // 在这里进行判断：是否需要对方法进行处理
            if (descriptor.equals("Lsample/MyTag;")) {
                String item = methodName + ":" + methodDesc;
                result.add(item);
            }
            return super.visitAnnotation(descriptor, visible);
        }
    }
}
