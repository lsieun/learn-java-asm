package lsieun.classfile;

import java.util.Arrays;

public class ClassFile {
    private final byte[] classFileBuffer;

    private final int constantPoolCount;
    private final int[] cpInfoOffsets;

    private final int header;

    private final int interfaces_count_offset;
    public final int interfacesCount;

    private final int fields_count_offset;
    public final int fieldCount;
    private final int[] fieldInfoOffsets;

    private final int methods_count_offset;
    public final int methodCount;
    private final int[] methodInfoOffsets;

    private final int attributes_count_offset;
    public final int attributeCount;
    private final int[] attributeInfoOffsets;

    public ClassFile(byte[] classFileBuffer) {
        this(classFileBuffer, 0, classFileBuffer.length);
    }

    public ClassFile(byte[] classFileBuffer, int classFileOffset, int classFileLength) {
        this.classFileBuffer = classFileBuffer;
        this.constantPoolCount = readUnsignedShort(classFileOffset + 8);
        this.cpInfoOffsets = new int[constantPoolCount];

        int currentCpInfoIndex = 1;
        int currentCpInfoOffset = classFileOffset + 10;

        while (currentCpInfoIndex < constantPoolCount) {
            this.cpInfoOffsets[currentCpInfoIndex++] = currentCpInfoOffset + 1;
            int cpInfoSize;
            switch (classFileBuffer[currentCpInfoOffset]) {
                case CPInfo.CONSTANT_FIELDREF_TAG:
                case CPInfo.CONSTANT_METHODREF_TAG:
                case CPInfo.CONSTANT_INTERFACE_METHODREF_TAG:
                case CPInfo.CONSTANT_INTEGER_TAG:
                case CPInfo.CONSTANT_FLOAT_TAG:
                case CPInfo.CONSTANT_NAME_AND_TYPE_TAG:
                    cpInfoSize = 5;
                    break;
                case CPInfo.CONSTANT_DYNAMIC_TAG:
                    cpInfoSize = 5;
                    break;
                case CPInfo.CONSTANT_INVOKE_DYNAMIC_TAG:
                    cpInfoSize = 5;
                    break;
                case CPInfo.CONSTANT_LONG_TAG:
                case CPInfo.CONSTANT_DOUBLE_TAG:
                    cpInfoSize = 9;
                    currentCpInfoIndex++;
                    break;
                case CPInfo.CONSTANT_UTF8_TAG:
                    cpInfoSize = 3 + readUnsignedShort(currentCpInfoOffset + 1);
                    break;
                case CPInfo.CONSTANT_METHOD_HANDLE_TAG:
                    cpInfoSize = 4;
                    break;
                case CPInfo.CONSTANT_CLASS_TAG:
                case CPInfo.CONSTANT_STRING_TAG:
                case CPInfo.CONSTANT_METHOD_TYPE_TAG:
                case CPInfo.CONSTANT_PACKAGE_TAG:
                case CPInfo.CONSTANT_MODULE_TAG:
                    cpInfoSize = 3;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            currentCpInfoOffset += cpInfoSize;
        }

        // The Classfile's access_flags field is just after the last constant pool entry.
        this.header = currentCpInfoOffset;

        // interfaces_count is after the access_flags, this_class and super_class fields (2 bytes each).
        int currentOffset = header + 6;
        this.interfaces_count_offset = currentOffset;
        this.interfacesCount = readUnsignedShort(currentOffset);

        currentOffset += 2 + interfacesCount * 2;
        this.fields_count_offset = currentOffset;
        this.fieldCount = readUnsignedShort(currentOffset);
        this.fieldInfoOffsets = new int[fieldCount];

        currentOffset += 2;
        for (int i = 0; i < this.fieldCount; i++) {
            this.fieldInfoOffsets[i] = currentOffset;

            int count = readUnsignedShort(currentOffset + 6);

            currentOffset += 8;
            for (int j = 0; j < count; j++) {
                int size = getAttributeSize(currentOffset);
                currentOffset += size;
            }
        }

        this.methods_count_offset = currentOffset;
        this.methodCount = readUnsignedShort(currentOffset);
        this.methodInfoOffsets = new int[methodCount];

        currentOffset += 2;
        for (int i = 0; i < this.methodCount; i++) {
            this.methodInfoOffsets[i] = currentOffset;

            int count = readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            for (int j = 0; j < count; j++) {
                int size = getAttributeSize(currentOffset);
                currentOffset += size;
            }
        }

        this.attributes_count_offset = currentOffset;
        this.attributeCount = readUnsignedShort(currentOffset);
        this.attributeInfoOffsets = new int[attributeCount];

        currentOffset += 2;
        for (int i = 0; i < this.attributeCount; i++) {
            this.attributeInfoOffsets[i] = currentOffset;
            int size = getAttributeSize(currentOffset);
            currentOffset += size;
        }
    }

    public byte[] getCode(int methodIndex) {
        int currentOffset = methodInfoOffsets[methodIndex];
        int count = readUnsignedShort(currentOffset + 6);

        currentOffset += 8;
        for (int i = 0; i < count; i++) {
            int attribute_name_index = readUnsignedShort(currentOffset);
            String attrName = readUTF8(attribute_name_index);
            if ("Code".equals(attrName)) {
                break;
            }

            int size = getAttributeSize(currentOffset);
            currentOffset += size;
        }

        currentOffset += 10;
        int code_length = readInt(currentOffset);

        currentOffset += 4;
        return Arrays.copyOfRange(classFileBuffer, currentOffset, currentOffset + code_length);
    }

    private int getAttributeSize(int attribute_info_offset) {
        int size = 6;
        int attribute_length = readInt(attribute_info_offset + 2);
        size += attribute_length;
        return size;
    }

    public int readByte(final int offset) {
        return classFileBuffer[offset] & 0xFF;
    }

    public int readUnsignedShort(final int offset) {
        byte[] classBuffer = classFileBuffer;
        return ((classBuffer[offset] & 0xFF) << 8) | (classBuffer[offset + 1] & 0xFF);
    }

    public short readShort(final int offset) {
        byte[] classBuffer = classFileBuffer;
        return (short) (((classBuffer[offset] & 0xFF) << 8) | (classBuffer[offset + 1] & 0xFF));
    }

    public int readInt(final int offset) {
        byte[] classBuffer = classFileBuffer;
        return ((classBuffer[offset] & 0xFF) << 24)
                | ((classBuffer[offset + 1] & 0xFF) << 16)
                | ((classBuffer[offset + 2] & 0xFF) << 8)
                | (classBuffer[offset + 3] & 0xFF);
    }

    public long readLong(final int offset) {
        long l1 = readInt(offset);
        long l0 = readInt(offset + 4) & 0xFFFFFFFFL;
        return (l1 << 32) | l0;
    }

    private String readUTF8(int cp_info_index) {
        int utfOffset = cpInfoOffsets[cp_info_index];
        int utfLength = readUnsignedShort(utfOffset);
        return readUTF8(utfOffset + 2, utfLength);
    }

    private String readUTF8(final int utfOffset, final int utfLength) {
        char[] charBuffer = new char[utfLength];
        int currentOffset = utfOffset;
        int endOffset = currentOffset + utfLength;
        int strLength = 0;
        byte[] classBuffer = classFileBuffer;
        while (currentOffset < endOffset) {
            int currentByte = classBuffer[currentOffset++];
            if ((currentByte & 0x80) == 0) {
                charBuffer[strLength++] = (char) (currentByte & 0x7F);
            }
            else if ((currentByte & 0xE0) == 0xC0) {
                charBuffer[strLength++] =
                        (char) (((currentByte & 0x1F) << 6) + (classBuffer[currentOffset++] & 0x3F));
            }
            else {
                charBuffer[strLength++] =
                        (char)
                                (((currentByte & 0xF) << 12)
                                        + ((classBuffer[currentOffset++] & 0x3F) << 6)
                                        + (classBuffer[currentOffset++] & 0x3F));
            }
        }
        return new String(charBuffer, 0, strLength);
    }
}
