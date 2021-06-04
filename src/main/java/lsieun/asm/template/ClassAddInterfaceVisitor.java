package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClassAddInterfaceVisitor extends ClassVisitor {
    private final String[] newInterfaces;

    public ClassAddInterfaceVisitor(int api, ClassVisitor cv, String[] newInterfaces) {
        super(api, cv);
        this.newInterfaces = newInterfaces;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        Set<String> set = new HashSet<>(); // 注意，这里使用Set是为了避免出现重复接口
        if (interfaces != null) {
            set.addAll(Arrays.asList(interfaces));
        }
        if (newInterfaces != null) {
            set.addAll(Arrays.asList(newInterfaces));
        }
        super.visit(version, access, name, signature, superName, set.toArray(new String[0]));
    }
}
