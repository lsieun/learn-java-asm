package lsieun.utils;

import org.objectweb.asm.*;

public class ASMUtilsCore {
    public int read(ClassReader cr, int off, char[] buf,
                    int codeOff, Label[] labels) {
        int methodOff = getMethodOffset(cr, codeOff, buf);
        int acc = cr.readUnsignedShort(methodOff); // method access
        String name = cr.readUTF8(methodOff + 2, buf); // method name
        String desc = cr.readUTF8(methodOff + 4, buf); // method desc
        return -1;
    }

    public static int getMethodOffset(ClassReader cr, int codeOff, char[] buf) {
        int off = cr.header + 6;
        int interfacesCount = cr.readUnsignedShort(off);
        off += 2 + interfacesCount * 2;

        int fieldsCount = cr.readUnsignedShort(off);
        off += 2;
        for (; fieldsCount > 0; --fieldsCount) {
            int attrCount = cr.readUnsignedShort(off + 6);
            off += 8;  // fields
            for (; attrCount > 0; --attrCount) {
                off += 6 + cr.readInt(off + 2);
            }
        }

        int methodsCount = cr.readUnsignedShort(off);
        off += 2;
        for (; methodsCount > 0; --methodsCount) {
            int methodOff = off;
            int attrCount = cr.readUnsignedShort(off + 6);
            off += 8;  // methods
            for (; attrCount > 0; --attrCount) {
                String attrName = cr.readUTF8(off, buf);
                off += 6;
                if (attrName.equals("Code")) {
                    if (codeOff == off) {
                        return methodOff;
                    }
                }
                off += cr.readInt(off - 4);
            }
        }
        return -1;
    }

    // org.objectweb.asm.commons.GeneratorAdapter.swap(org.objectweb.asm.Type, org.objectweb.asm.Type)
    public static void swap(MethodVisitor mv, Type stackTop, Type belowTop) {
        if (stackTop.getSize() == 1) {
            if (belowTop.getSize() == 1) {
                // Top = 1, below = 1
                mv.visitInsn(Opcodes.SWAP);
            }
            else {
                // Top = 1, below = 2
                mv.visitInsn(Opcodes.DUP_X2);
                mv.visitInsn(Opcodes.POP);
            }
        }
        else {
            if (belowTop.getSize() == 1) {
                // Top = 2, below = 1
                mv.visitInsn(Opcodes.DUP2_X1);
            }
            else {
                // Top = 2, below = 2
                mv.visitInsn(Opcodes.DUP2_X2);
            }
            mv.visitInsn(Opcodes.POP2);
        }
    }
}
