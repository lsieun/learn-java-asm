package lsieun.asm.template;

import lsieun.utils.ByteUtils;
import lsieun.utils.HexFormat;
import lsieun.utils.HexUtils;
import org.objectweb.asm.*;

public class CustomAttribute extends Attribute {
    private static final byte[] CODE_BLOB_BYTE_ARRAY = new byte[]{
            (byte) 0xC0, (byte) 0xDE, (byte) 0xB1, 0x0B
    };
    private static final int CODE_BLOB_INT_VALUE = 0xC0DEB10B;

    private final byte[] info;

    public CustomAttribute(String type, byte[] info) {
        super(type);
        this.info = info;
    }

    @Override
    protected Attribute read(ClassReader classReader, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
        int magic = classReader.readInt(offset);
        if (magic != CODE_BLOB_INT_VALUE) {
            throw new RuntimeException("magic is not right! expected: " + CODE_BLOB_INT_VALUE + ", actual: " + magic);
        }
        int value = classReader.readInt(offset + 4);
        byte[] info = ByteUtils.intToByteArray(value);
        return new CustomAttribute(CustomAttribute.class.getSimpleName(), info);
    }

    @Override
    protected ByteVector write(ClassWriter classWriter, byte[] code, int codeLength, int maxStack, int maxLocals) {
        ByteVector byteVector = new ByteVector();
        byteVector.putByteArray(CODE_BLOB_BYTE_ARRAY, 0, CODE_BLOB_BYTE_ARRAY.length);
        byteVector.putByteArray(info, 0, info.length);
        return byteVector;
    }

    @Override
    public String toString() {
        return String.format("%s {name='%s'}", type, HexUtils.format(info, HexFormat.FORMAT_FF_FF));
    }
}
