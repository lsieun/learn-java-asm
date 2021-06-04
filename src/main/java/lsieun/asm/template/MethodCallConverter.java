package lsieun.asm.template;

import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

public class MethodCallConverter extends MethodVisitor {
    private final List<Info> list;

    public MethodCallConverter(int api, MethodVisitor mv, List<Info> list) {
        super(api, mv);
        if (list == null) {
            this.list = new ArrayList<>();
        }
        else {
            this.list = list;
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        Info info = matchingInfo(opcode, owner, name, descriptor);
        if (info != null) {
            super.visitMethodInsn(info.targetOpcode, info.targetOwner, info.targetName, info.targetDesc, false);
            return;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    private Info matchingInfo(int opcode, String owner, String name, String descriptor) {
        for (Info info : list) {
            if (opcode == info.srcOpcode &&
                    owner.equals(info.srcOwner) &&
                    name.equals(info.srcName) &&
                    descriptor.equals(info.srcDesc)) {
                return info;
            }
        }
        return null;
    }
}
