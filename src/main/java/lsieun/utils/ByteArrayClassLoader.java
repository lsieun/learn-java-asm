package lsieun.utils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

public class ByteArrayClassLoader extends ClassLoader {
    public final Class<?> defineClass(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        String internalName = cr.getClassName();
        String className = Type.getObjectType(internalName).getClassName();
        return defineClass(className, bytes);
    }

    public Class<?> defineClass(String name, byte[] bytes) {
        return super.defineClass(name, bytes, 0, bytes.length);
    }
}
