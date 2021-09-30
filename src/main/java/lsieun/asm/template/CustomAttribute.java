package lsieun.asm.template;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassWriter;

public class CustomAttribute extends Attribute {
    private static final byte[] CODE_BLOB = new byte[]{
            (byte) 0xC0, (byte) 0xDE, (byte) 0xB1, 0x0B
    };

    private final byte[] info;

    public CustomAttribute(String type, byte[] info) {
        super(type);
        this.info = info;
    }

    @Override
    protected ByteVector write(ClassWriter classWriter, byte[] code, int codeLength, int maxStack, int maxLocals) {
        ByteVector byteVector = new ByteVector();
        byteVector.putByteArray(CODE_BLOB, 0, CODE_BLOB.length);
        byteVector.putByteArray(info, 0, info.length);
        return byteVector;
    }
}
