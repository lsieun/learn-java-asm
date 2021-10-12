package lsieun.asm.core;

import org.objectweb.asm.*;

public class SuperPackageAttribute extends Attribute {
    public String name;

    public SuperPackageAttribute() {
        super("Superpackage");
    }

    public SuperPackageAttribute(String name) {
        this();
        this.name = name;
    }

    @Override
    protected Attribute read(ClassReader classReader, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
        String name = classReader.readUTF8(offset, charBuffer);
        return new SuperPackageAttribute(name);
    }

    @Override
    protected ByteVector write(ClassWriter classWriter, byte[] code, int codeLength, int maxStack, int maxLocals) {
        int index = classWriter.newUTF8(name);
        return new ByteVector().putShort(index);
    }

    @Override
    public String toString() {
        return String.format("%s {name='%s'}", type, name);
    }
}
