package lsieun.utils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;

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
}
